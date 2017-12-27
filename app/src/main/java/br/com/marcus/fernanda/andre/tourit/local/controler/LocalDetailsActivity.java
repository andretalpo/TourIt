package br.com.marcus.fernanda.andre.tourit.local.controler;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.AvaliacaoLocal;
import br.com.marcus.fernanda.andre.tourit.local.model.AvaliacaoLocalAdapter;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.CreateRoteiroActivity;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

public class LocalDetailsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Local local;
    private List<AvaliacaoLocal> listaAvaliacoes;
    private AvaliacaoLocalAdapter adapter;
    private static boolean consultando;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_local_details);
        setSupportActionBar(toolbar);

        local = (Local) getIntent().getSerializableExtra("local");
        setTitle(local.getNome());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLocalDetails);
        mapFragment.getMapAsync(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.adicionarLocalDetailsFab);

        if(CreateRoteiroActivity.getListaLocaisRoteiroAtual() != null){
            inicializarBotaoAdicionar();
        } else{
            inicializarBotaoCriarRoteiro();
        }

        List<Local> listaLocaisRoteiroAtual = CreateRoteiroActivity.getListaLocaisRoteiroAtual();
        if(listaLocaisRoteiroAtual != null) {
            for (Local localAtual : listaLocaisRoteiroAtual) {
                if (localAtual.getIdPlaces().equals(local.getIdPlaces())) {
                    inicializarBotaoExcluir();
                }
            }
        }

        RecyclerView avaliacoesRecyclerView = (RecyclerView) findViewById(R.id.avaliacoesLocalDetailsRecyclerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        avaliacoesRecyclerView.setLayoutManager(layout);

        listaAvaliacoes = new ArrayList<>();
        adapter = new AvaliacaoLocalAdapter(listaAvaliacoes, this);
        avaliacoesRecyclerView.setAdapter(adapter);

        new CarregarAvaliacoesAsyncTask().execute(local.getIdPlaces());

        //Conversão de byte array para bitmap, depois para bitmap drawble(Para que seja possível aplicar no layout)
        Bitmap bmp = ImageConverter.convertByteToBitmap(getIntent().getByteArrayExtra("arrayFoto"));
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_local_details);
        toolbarLayout.setBackground(bitmapDrawable);

        local.setFoto(bmp);

        TextView enderecoTextView = (TextView) findViewById(R.id.enderecoLocalDetailsTextView);
        TextView tipoTextView = (TextView) findViewById(R.id.tipoLocalDetailsTextView);
        TextView notaTextView = (TextView) findViewById(R.id.notaLocalDetailsTextView);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.localDetailsRatingBar);

        enderecoTextView.setText(local.getEndereco());
        notaTextView.setText(String.valueOf(local.getNota()));
        ratingBar.setRating(local.getNota());
        for (int i = 0; i < local.getTipo().size(); i++) {
            if(i < local.getTipo().size() - 1) {
                tipoTextView.append(local.getTipo().get(i) + ", ");
            } else {
                tipoTextView.append(local.getTipo().get(i) + ".");
            }
        }

        if(isConsultando()){
            floatingActionButton.hide();
        }
    }

    private void inicializarBotaoExcluir() {
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete, null));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cor_vermelho)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Local> listaLocaisRoteiroAtual = CreateRoteiroActivity.getListaLocaisRoteiroAtual();
                for(int i = 0; i<listaLocaisRoteiroAtual.size(); i++){
                    if(listaLocaisRoteiroAtual.get(i).getIdPlaces().equals(local.getIdPlaces())){
                        listaLocaisRoteiroAtual.remove(i);
                    }
                }
                CreateRoteiroActivity.setListaLocaisRoteiroAtual(listaLocaisRoteiroAtual);
                inicializarBotaoAdicionar();
                Toast.makeText(LocalDetailsActivity.this, getResources().getString(R.string.local_excluido_sucesso), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inicializarBotaoAdicionar() {
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, null));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CreateRoteiroActivity.getListaLocaisRoteiroAtual() == null){
                    CreateRoteiroActivity.setListaLocaisRoteiroAtual(new ArrayList<Local>());
                }
                CreateRoteiroActivity.getListaLocaisRoteiroAtual().add(local);
                inicializarBotaoExcluir();
                Toast.makeText(LocalDetailsActivity.this, getResources().getString(R.string.local_adicionado_sucesso), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inicializarBotaoCriarRoteiro() {
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, null));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CreateRoteiroActivity.getListaLocaisRoteiroAtual() == null){
                    CreateRoteiroActivity.setListaLocaisRoteiroAtual(new ArrayList<Local>());
                }
                CreateRoteiroActivity.getListaLocaisRoteiroAtual().add(local);
                irParaCriacaoRoteiro();
            }
        });
    }

    private void irParaCriacaoRoteiro() {
        Intent intent = new Intent(this, CreateRoteiroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_32);
        googleMap.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f)
                .position(new LatLng(local.getLat(), local.getLng()))
                .title(local.getNome()).icon(BitmapDescriptorFactory.fromBitmap(icon)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(local.getLat(), local.getLng()), 15);
        googleMap.moveCamera(cameraUpdate);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
    }

    private class CarregarAvaliacoesAsyncTask extends AsyncTask<String, Void, List<AvaliacaoLocal>>{

        @Override
        protected List<AvaliacaoLocal> doInBackground(String... idPlaces) {
            return GooglePlacesServices.buscarAvaliacoesLocal(idPlaces[0]);
        }

        @Override
        protected void onPostExecute(List<AvaliacaoLocal> avaliacoes) {
            listaAvaliacoes.clear();
            if(avaliacoes != null) {
                listaAvaliacoes.addAll(avaliacoes);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public static boolean isConsultando() {
        return consultando;
    }

    public static void setConsultando(boolean consultando) {
        LocalDetailsActivity.consultando = consultando;
    }

}
