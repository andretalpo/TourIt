package br.com.marcus.fernanda.andre.tourit.local.controler;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.AvaliacaoLocal;
import br.com.marcus.fernanda.andre.tourit.local.model.AvaliacaoLocalAdapter;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.roteiro.CreateRoteiroActivity;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

public class LocalDetailsActivity extends AppCompatActivity {

    private Local local;
    private List<AvaliacaoLocal> listaAvaliacoes;
    private AvaliacaoLocalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_local_details);
        setSupportActionBar(toolbar);

        inicializarBotaoAdicionar();

        local = (Local) getIntent().getSerializableExtra("local");
        setTitle(local.getNome());

        List<Local> listaLocaisRoteiroAtual = CreateRoteiroActivity.getListaLocaisRoteiroAtual();
        if(listaLocaisRoteiroAtual != null) {
            for (Local localAtual : listaLocaisRoteiroAtual) {
                if (localAtual.getIdPlaces().equals(local.getIdPlaces())) {
                    inicializarBotaoExcluir();
                }
            }
        }

//        if(new LocalDAO(this, MainActivity.idUsuarioGoogle).buscarLocal(local.getIdPlaces()) != null){
//            inicializarBotaoExcluir();
//        }

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
        for (String tipo : local.getTipo()) {
            tipoTextView.append(tipo + " ");
        }
    }

    private void inicializarBotaoExcluir() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.adicionarLocalDetailsFab);
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete, null));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cor_vermelho)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new LocalDAO(LocalDetailsActivity.this, MainActivity.idUsuarioGoogle).deleteLocal(local.getIdPlaces());
                List<Local> listaLocaisRoteiroAtual = CreateRoteiroActivity.getListaLocaisRoteiroAtual();
                for(int i = 0; i<listaLocaisRoteiroAtual.size(); i++){
                    if(listaLocaisRoteiroAtual.get(i).getIdPlaces().equals(local.getIdPlaces())){
                        listaLocaisRoteiroAtual.remove(i);
                    }
                }
                CreateRoteiroActivity.setListaLocaisRoteiroAtual(listaLocaisRoteiroAtual);
                inicializarBotaoAdicionar();
                Toast.makeText(LocalDetailsActivity.this, "Local excluído com sucesso.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inicializarBotaoAdicionar() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.adicionarLocalDetailsFab);
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, null));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new LocalDAO(LocalDetailsActivity.this, MainActivity.idUsuarioGoogle).inserirLocalSQLite(local);
                CreateRoteiroActivity.getListaLocaisRoteiroAtual().add(local);
                inicializarBotaoExcluir();
                Toast.makeText(LocalDetailsActivity.this, "Local adicionado com sucesso.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class CarregarAvaliacoesAsyncTask extends AsyncTask<String, Void, List<AvaliacaoLocal>>{

        @Override
        protected List<AvaliacaoLocal> doInBackground(String... idPlaces) {
            return GooglePlacesServices.buscarAvaliacoesLocal(idPlaces[0]);
        }

        @Override
        protected void onPostExecute(List<AvaliacaoLocal> avaliacoes) {
            listaAvaliacoes.clear();
            listaAvaliacoes.addAll(avaliacoes);
            adapter.notifyDataSetChanged();
        }
    }

}
