package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GoogleDirectionsServices;
import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalDetailsActivity;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuarioService;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

import static br.com.marcus.fernanda.andre.tourit.R.id.alterarRoteiroDetailsActivityImageView;

public class RoteiroDetailsActivity extends AppCompatActivity implements OnMapReadyCallback{

    ProgressDialog progressDialog;
    private GoogleMap map;
    private static List<Local> listaLocais;
    private List<Polyline> polylinePaths;
    private ImageView seguirRoteiroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roteiro_details);
        listaLocais = new ArrayList<>();

        TextView nomeRoteiroTextView = (TextView) findViewById(R.id.nomeRoteiroRoteiroDetailsTextView);
        TextView nomeCriadorTextView = (TextView) findViewById(R.id.nomeCriadorRoteiroDetailsTextView);
        TextView tipoRoteiroTextView = (TextView) findViewById(R.id.tipoRoteiroRoteiroDetailsTextView);
        RatingBar roteiroRatingBar = (RatingBar) findViewById(R.id.avaliacaoRoteiroRoteiroDetailsRatingBar);
        ImageView roteiroImageView = (ImageView) findViewById(R.id.imagemRoteiroDetailsActivity);

        final Roteiro roteiro = (Roteiro) getIntent().getSerializableExtra("roteiro");

        roteiro.setImagemRoteiro(ImageConverter.convertByteToBitmap(getIntent().getByteArrayExtra("imagemRoteiro")));
        roteiroImageView.setImageBitmap(roteiro.getImagemRoteiro());

        ImageView excluirButton = (ImageView) findViewById(R.id.excluirRoteiroDetailsActivityImageView);
        excluirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExcluirRoteiroTask().execute(roteiro);
            }
        });

        ImageView alterarButton = (ImageView) findViewById(alterarRoteiroDetailsActivityImageView);
        alterarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterarRoteiro(roteiro);
            }
        });

        ImageView publicarRoteiroButton = (ImageView) findViewById(R.id.publicarRoteiroDetailsActivityImageView);
        if(roteiro.isPublicado()){
            publicarRoteiroButton.setVisibility(View.GONE);
        } else{
            publicarRoteiroButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roteiro.setPublicado(true);
                    new PublicarRoteiroTask().execute(roteiro);
                }
            });
        }

        seguirRoteiroButton = (ImageView) findViewById(R.id.seguirRoteiroDetailsActivityImageView);
        seguirRoteiroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SeguirRoteiroTask().execute(roteiro);
            }
        });

        if(roteiro.getIdRoteiroSqlite() == null){//carrega locais da api - roteiros publicados
            alterarButton.setVisibility(View.GONE);
            excluirButton.setVisibility(View.GONE);
            new InicializarBotaoSeguirTask().execute(roteiro);
            new CarregarLocaisTask().execute(roteiro);
        }else{//carrega locais do sqlite - meus roteiros
            carregarLocaisRoteiroBanco(roteiro.getIdRoteiroSqlite());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            LocalListFragment localFragment = new LocalListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("acao", "consultaLocaisBanco");
            localFragment.setArguments(bundle);

            transaction.replace(R.id.listaLocaisRoteiroDetailsFrameLayout, localFragment);
            transaction.commit();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRoteiroDetails);
            mapFragment.getMapAsync(this);
        }

        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        nomeCriadorTextView.setText(roteiro.getCriadorRoteiro());
        tipoRoteiroTextView.setText(roteiro.getTipoRoteiro());
        roteiroRatingBar.setRating(roteiro.getNotaRoteiro());

    }

    private void alterarRoteiro(Roteiro roteiro){
        Intent intent = new Intent(this, CreateRoteiroActivity.class);
        intent.putExtra("roteiro", roteiro);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalDetailsActivity.setConsultando(false);
    }

    private class ExcluirRoteiroTask extends AsyncTask<Roteiro, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RoteiroDetailsActivity.this, "Excluindo roteiro.", "Aguarde", true, false);
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).excluirRoteiro(roteiro [0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            Toast.makeText(RoteiroDetailsActivity.this, "Roteiro excluído com sucesso!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            RoteiroDetailsActivity.this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private class PublicarRoteiroTask extends AsyncTask<Roteiro, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RoteiroDetailsActivity.this, "Publicando roteiro.", "Aguarde", true, false);
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).publicarRoteiro(roteiro[0].getIdRoteiroFirebase());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            progressDialog.dismiss();
            if (sucesso){
                Toast.makeText(RoteiroDetailsActivity.this, "Roteiro publicado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else{
                Toast.makeText(RoteiroDetailsActivity.this, "Falha na publicação do roteiro", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalDetailsActivity.setConsultando(true);
    }

    private void carregarLocaisRoteiroBanco(Long idRoteiro) {
        List<Local> locais = new LocalService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).buscarLocaisRoteiro(idRoteiro);
        listaLocais.clear();

        if(locais != null) {
            listaLocais.addAll(locais);
            LocalDetailsActivity.setConsultando(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        polylinePaths = new ArrayList<>();

        int meioLista = Math.round(listaLocais.size()/2);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(listaLocais.get(meioLista).getLat(), listaLocais.get(meioLista).getLng()), 15);
        map.moveCamera(cameraUpdate);
        List<LatLng> listaLatLng = new ArrayList<>();
        for(Local local : listaLocais){
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(local.getLat(), local.getLng()))
                    .title(local.getNome()));
            listaLatLng.add(new LatLng(local.getLat(), local.getLng()));
        }

        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);

        new CriarRotaTask().execute(listaLatLng);
    }

    public static List<Local> getListaLocais() {
        return listaLocais;
    }

    public static void setListaLocais(List<Local> listaLocais) {
        RoteiroDetailsActivity.listaLocais = listaLocais;
    }

    private class CriarRotaTask extends AsyncTask<List<LatLng>, Void, List<LatLng>>{

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RoteiroDetailsActivity.this, "Carregando roteiro", "Aguarde", true, false);
        }

        @Override
        protected List<LatLng> doInBackground(List<LatLng>... listaLatLng) {
            return GoogleDirectionsServices.criarRota(listaLatLng[0]);
        }

        @Override
        protected void onPostExecute(List<LatLng> listaPontos) {
            progressDialog.dismiss();
            PolylineOptions polylineOptions = new PolylineOptions().width(10).color(getResources().getColor(R.color.colorAccent));

            polylineOptions.addAll(listaPontos);
            polylinePaths.add(map.addPolyline(polylineOptions));
        }
    }

    private class CarregarLocaisTask extends AsyncTask<Roteiro, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RoteiroDetailsActivity.this, "Carregando locais.", "Aguarde", true, false);
        }

        @Override
        protected Void doInBackground(Roteiro... roteiro) {
            for (String local : roteiro[0].getLocaisRoteiro()) {
                listaLocais.add(GooglePlacesServices.buscarLocalIdPlaces(local));
            }
            LocalDetailsActivity.setConsultando(true);
            return null;
        }

        @Override
        protected void onPostExecute(Void nada) {
            progressDialog.dismiss();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            LocalListFragment localFragment = new LocalListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("acao", "consultaLocaisBanco");
            localFragment.setArguments(bundle);

            transaction.replace(R.id.listaLocaisRoteiroDetailsFrameLayout, localFragment);
            transaction.commit();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRoteiroDetails);
            mapFragment.getMapAsync(RoteiroDetailsActivity.this);
        }
    }

    private class InicializarBotaoSeguirTask extends AsyncTask<Roteiro, Void, Roteiro> {
        @Override
        protected Roteiro doInBackground(Roteiro... roteiro) {
            return new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).consultarRoteiro(roteiro[0].getIdRoteiroFirebase());
        }

        @Override
        protected void onPostExecute(Roteiro roteiro) {
            if(roteiro == null){
                seguirRoteiroButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private class SeguirRoteiroTask extends AsyncTask<Roteiro, Void, Void> {
        @Override
        protected Void doInBackground(Roteiro... roteiro) {
            new UsuarioService().adicionarRoteiroSeguidoUsuario(MainActivity.idUsuarioGoogle, roteiro[0].getIdRoteiroFirebase());
            //download do roteiro para o sqlite
            return null;
        }

        @Override
        protected void onPostExecute(Void nada) {
            Toast.makeText(RoteiroDetailsActivity.this, "Roteiro seguido com sucesso!", Toast.LENGTH_SHORT).show();
            RoteiroDetailsActivity.this.finish();
        }
    }
}