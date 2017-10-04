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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalSearchActivity;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;

public class CreateRoteiroActivity extends AppCompatActivity {

    private static List<Local> listaLocaisRoteiroAtual;
    public static List<String> listaTiposRoteiro = new ArrayList<>();
    private EditText nomeRoteiroEditText;
    private Spinner spinner;
    private ProgressDialog progressDialog;
    private int idRoteiro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roteiro);
        listaLocaisRoteiroAtual = new ArrayList<>();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalListFragment localFragment = new LocalListFragment();

        transaction.replace(R.id.listaLocaisRoteiroActivityFrameLayout, localFragment);
        transaction.commit();

        nomeRoteiroEditText = (EditText) findViewById(R.id.nomeRoteiroRoteiroActivityEditText);
        spinner = (Spinner) findViewById(R.id.tipoRoteiroActivitySpinner);

        Button adicionarLocalButton = (Button) findViewById(R.id.adicionarLocalRoteiroActivityButton);
        adicionarLocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irParaTelaBuscaLocais();
            }
        });

        inicializarSpinnerTipo();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.salvarRoteiroActivityFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Roteiro roteiro = new Roteiro();
                roteiro.setNomeRoteiro(nomeRoteiroEditText.getText().toString());
                roteiro.setNotaRoteiro(0);//mudar
                roteiro.setPublicado(false);
                roteiro.setTipoRoteiro(spinner.getSelectedItem().toString());
                roteiro.setCriadorRoteiro(getIntent().getStringExtra("nomeUsuario"));
                List<String> locais = new ArrayList<>();
                for (Local local : listaLocaisRoteiroAtual) {
                    locais.add(local.getIdPlaces());
                }
                roteiro.setLocaisRoteiro(locais);
                new SalvarRoteiroTask().execute(roteiro);
            }
        });
    }

    private void inicializarSpinnerTipo() {
        listaTiposRoteiro.clear();
        listaTiposRoteiro.add("Natureza");
        listaTiposRoteiro.add("Gastronomia");
        listaTiposRoteiro.add("Aventura");
        listaTiposRoteiro.add("Cultural");
        listaTiposRoteiro.add("Romântico");
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

    public static List<String> getListaTiposRoteiro() {
        return listaTiposRoteiro;
    }

    public static void setListaTiposRoteiro(List<String> listaTiposRoteiro) {
        CreateRoteiroActivity.listaTiposRoteiro = listaTiposRoteiro;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listaLocaisRoteiroAtual = null;
    }

    private class SalvarRoteiroTask extends AsyncTask<Roteiro, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CreateRoteiroActivity.this, "Criando roteiro.", "Aguarde", true, false);
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            try {
                idRoteiro = new RoteiroService(CreateRoteiroActivity.this, getIntent().getStringExtra("idUsuarioGoogle")).salvarRoteiro(roteiro[0], listaLocaisRoteiroAtual);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (sucesso){
                Toast.makeText(CreateRoteiroActivity.this, "Roteiro criado com sucesso!", Toast.LENGTH_SHORT).show();
                irParaTelaRoteiroDetails();
            } else{
                Toast.makeText(CreateRoteiroActivity.this, "Falha na criação de roteiro", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private void irParaTelaRoteiroDetails() {
        Intent intent = new Intent(CreateRoteiroActivity.this, RoteiroDetailsActivity.class);
        intent.putExtra("idRoteiro", idRoteiro);
        startActivity(intent);
        finish();
    }
}
