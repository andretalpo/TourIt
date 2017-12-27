package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GoogleDirectionsServices;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalSearchActivity;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

public class CreateRoteiroActivity extends AppCompatActivity {

    private static List<Local> listaLocaisRoteiroAtual;
    public static List<String> listaTiposRoteiro = new ArrayList<>();
    private EditText nomeRoteiroEditText;
    private Spinner spinner;
    private ProgressDialog progressDialog;
    private Roteiro roteiroAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roteiro);

        nomeRoteiroEditText = (EditText) findViewById(R.id.nomeRoteiroRoteiroActivityEditText);
        spinner = (Spinner) findViewById(R.id.tipoRoteiroActivitySpinner);

        inicializarSpinnerTipo();

        if (listaLocaisRoteiroAtual == null) {
            listaLocaisRoteiroAtual = new ArrayList<>();
        }

        Button adicionarLocalButton = (Button) findViewById(R.id.adicionarLocalRoteiroActivityButton);
        adicionarLocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irParaTelaBuscaLocais();
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.salvarRoteiroActivityFab);
        //alteraçao de roteiro
        roteiroAtual = (Roteiro) getIntent().getSerializableExtra("roteiro");
        if(roteiroAtual != null){
            setTitle("Alteração de Roteiro");
            nomeRoteiroEditText.setText(roteiroAtual.getNomeRoteiro());
            for (int i=0; i<listaTiposRoteiro.size(); i++){
                if(listaTiposRoteiro.get(i).equals(roteiroAtual.getTipoRoteiro())){
                    spinner.setSelection(i);
                }
            }
            listaLocaisRoteiroAtual = new LocalService(this, MainActivity.idUsuarioGoogle).buscarLocaisRoteiro(roteiroAtual.getIdRoteiroSqlite());
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nomeRoteiroEditText.length() >= 4) {
                        if(!listaLocaisRoteiroAtual.isEmpty()) {
                            roteiroAtual.setNomeRoteiro(nomeRoteiroEditText.getText().toString());
                            roteiroAtual.setTipoRoteiro((String) spinner.getSelectedItem());
                            List<String> idLocais = new ArrayList<>();
                            List<String> nomeLocais = new ArrayList<>();
                            for (Local local : listaLocaisRoteiroAtual) {
                                idLocais.add(local.getIdPlaces());
                                nomeLocais.add(local.getNome());
                            }
                            roteiroAtual.setIdLocaisRoteiro(idLocais);
                            roteiroAtual.setNomeLocaisRoteiro(nomeLocais);
                            RoteiroService roteiroService = new RoteiroService(CreateRoteiroActivity.this, MainActivity.idUsuarioGoogle);
                            roteiroAtual.setNotaRoteiro(roteiroService.calcularNotaRoteiro(listaLocaisRoteiroAtual));
                            roteiroAtual.setImagemRoteiro(roteiroService.montarImagemRoteiro(listaLocaisRoteiroAtual));
                            new AlterarRoteiroTask().execute(roteiroAtual);
                        }else{
                            Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.necessario_local), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.minimo_nome_roteiro), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else{ //criação de roteiro
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nomeRoteiroEditText.length() >= 4) {
                        if (!listaLocaisRoteiroAtual.isEmpty()) {
                            roteiroAtual = new Roteiro();
                            roteiroAtual.setNomeRoteiro(nomeRoteiroEditText.getText().toString());
                            roteiroAtual.setTipoRoteiro(spinner.getSelectedItem().toString());
                            roteiroAtual.setCriadorRoteiro(MainActivity.nomeUsuario);
                            List<String> idLocais = new ArrayList<>();
                            List<String> nomeLocais = new ArrayList<>();
                            for (Local local : listaLocaisRoteiroAtual) {
                                idLocais.add(local.getIdPlaces());
                                nomeLocais.add(local.getNome());
                            }
                            roteiroAtual.setIdLocaisRoteiro(idLocais);
                            roteiroAtual.setNomeLocaisRoteiro(nomeLocais);
                            RoteiroService roteiroService = new RoteiroService(CreateRoteiroActivity.this, MainActivity.idUsuarioGoogle);
                            roteiroAtual.setNotaRoteiro(roteiroService.calcularNotaRoteiro(listaLocaisRoteiroAtual));
                            roteiroAtual.setImagemRoteiro(roteiroService.montarImagemRoteiro(listaLocaisRoteiroAtual));
                            new SalvarRoteiroTask().execute(roteiroAtual);
                        }else{
                            Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.necessario_local), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.minimo_nome_roteiro), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalListFragment localFragment = new LocalListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("acao", "consultaLocaisRoteiroAtual");
        localFragment.setArguments(bundle);
        transaction.replace(R.id.listaLocaisRoteiroActivityFrameLayout, localFragment);
        transaction.commit();
    }

    private void inicializarSpinnerTipo() {
        listaTiposRoteiro.clear();
        listaTiposRoteiro.add(getResources().getString(R.string.natureza));
        listaTiposRoteiro.add(getResources().getString(R.string.gastronomia));
        listaTiposRoteiro.add(getResources().getString(R.string.aventura));
        listaTiposRoteiro.add(getResources().getString(R.string.cultural));
        listaTiposRoteiro.add(getResources().getString(R.string.romantico));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaTiposRoteiro);
        spinner.setAdapter(adapter);
    }

    private void irParaTelaBuscaLocais() {
        Intent intent = new Intent(this, LocalSearchActivity.class);
        startActivity(intent);
    }

    public static List<Local> getListaLocaisRoteiroAtual() {
        return listaLocaisRoteiroAtual;
    }

    public static void setListaLocaisRoteiroAtual(List<Local> listaLocaisRoteiroAtual) {
        CreateRoteiroActivity.listaLocaisRoteiroAtual = listaLocaisRoteiroAtual;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listaLocaisRoteiroAtual = null;
    }

    private class SalvarRoteiroTask extends AsyncTask<Roteiro, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CreateRoteiroActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.criando_roteiro));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            try {
                List<LatLng> latLngList = new ArrayList<>();
                for(Local local : listaLocaisRoteiroAtual){
                    latLngList.add(new LatLng(local.getLat(), local.getLng()));
                }
                roteiro[0].setRota(GoogleDirectionsServices.criarRota(latLngList));
                roteiroAtual = new RoteiroService(CreateRoteiroActivity.this, MainActivity.idUsuarioGoogle).salvarRoteiro(roteiro[0], listaLocaisRoteiroAtual);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            progressDialog.dismiss();
            if (sucesso){
                Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.roteiro_criado_sucesso), Toast.LENGTH_SHORT).show();
                irParaTelaRoteiroDetails();
            } else{
                Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.falha_criacao_roteiro), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AlterarRoteiroTask extends AsyncTask<Roteiro, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CreateRoteiroActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.salvando_alteracoes));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            List<LatLng> latLngList = new ArrayList<>();
            for(Local local : listaLocaisRoteiroAtual){
                latLngList.add(new LatLng(local.getLat(), local.getLng()));
            }
            roteiro[0].setRota(GoogleDirectionsServices.criarRota(latLngList));
            new RoteiroService(CreateRoteiroActivity.this, MainActivity.idUsuarioGoogle).alterarRoteiro(roteiro[0], listaLocaisRoteiroAtual);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            progressDialog.dismiss();
            if (sucesso){
                Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.roteiro_salvo_sucesso), Toast.LENGTH_SHORT).show();
                finish();
            } else{
                Toast.makeText(CreateRoteiroActivity.this, getResources().getString(R.string.falha_alteacao_roteiro), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void irParaTelaRoteiroDetails() {
        Intent intent = new Intent(CreateRoteiroActivity.this, RoteiroDetailsActivity.class);
        intent.putExtra("roteiro", roteiroAtual);
        intent.putExtra("imagemRoteiro", ImageConverter.convertBitmapToByte(roteiroAtual.getImagemRoteiro()));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
