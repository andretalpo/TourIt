package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GoogleDirectionsServices;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalDetailsActivity;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

import static br.com.marcus.fernanda.andre.tourit.R.id.alterarRoteiroDetailsActivityImageView;

public class RoteiroDetailsActivity extends AppCompatActivity implements OnMapReadyCallback{

    ProgressDialog progressDialog;
    private GoogleMap map;
    private static List<Local> listaLocais;
    private List<Polyline> polylinePaths;

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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalListFragment localFragment = new LocalListFragment();

        final Roteiro roteiro = (Roteiro) getIntent().getSerializableExtra("roteiro");

        roteiro.setImagemRoteiro(ImageConverter.convertByteToBitmap(getIntent().getByteArrayExtra("imagemRoteiro")));
        roteiroImageView.setImageBitmap(roteiro.getImagemRoteiro());

        carregarLocaisRoteiroBanco(roteiro.getIdRoteiroSqlite());
        Bundle bundle = new Bundle();
        bundle.putString("acao", "consultaLocaisBanco");
        localFragment.setArguments(bundle);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRoteiroDetails);
        mapFragment.getMapAsync(this);

        transaction.replace(R.id.listaLocaisRoteiroDetailsFrameLayout, localFragment);
        transaction.commit();

        //Roteiro roteiro = new RoteiroService(this, MainActivity.idUsuarioGoogle).consultarRoteiro(getIntent().getIntExtra("idRoteiro", -1));
        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        nomeCriadorTextView.setText(roteiro.getCriadorRoteiro());
        tipoRoteiroTextView.setText(roteiro.getTipoRoteiro());
        roteiroRatingBar.setRating(roteiro.getNotaRoteiro());

        ImageView excluirButton = (ImageView) findViewById(R.id.excluirRoteiroDetailsActivityImageView);
        excluirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExcluirRoteiroTask().execute(roteiro);
            }
        });

        ImageView alterarButtton = (ImageView) findViewById(alterarRoteiroDetailsActivityImageView);
        alterarButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterarRoteiro(roteiro);
            }
        });

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

}
