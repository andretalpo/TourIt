package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalDetailsActivity;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;

public class RoteiroDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roteiro_details);

        TextView nomeRoteiroTextView = (TextView) findViewById(R.id.nomeRoteiroRoteiroDetailsTextView);
        TextView nomeCriadorTextView = (TextView) findViewById(R.id.nomeCriadorRoteiroDetailsTextView);
        TextView tipoRoteiroTextView = (TextView) findViewById(R.id.tipoRoteiroRoteiroDetailsTextView);
        RatingBar roteiroRatingBar = (RatingBar) findViewById(R.id.avaliacaoRoteiroRoteiroDetailsRatingBar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalListFragment localFragment = new LocalListFragment();

        Roteiro roteiro = (Roteiro) getIntent().getSerializableExtra("roteiro");

        Bundle bundle = new Bundle();
        bundle.putString("acao", "consultaLocaisBanco");
        bundle.putInt("idRoteiro", roteiro.getIdRoteiro());
        localFragment.setArguments(bundle);

        transaction.replace(R.id.listaLocaisRoteiroDetailsFrameLayout, localFragment);
        transaction.commit();

        //Roteiro roteiro = new RoteiroService(this, MainActivity.idUsuarioGoogle).consultarRoteiro(getIntent().getIntExtra("idRoteiro", -1));
        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        nomeCriadorTextView.setText(roteiro.getCriadorRoteiro());
        tipoRoteiroTextView.setText(roteiro.getTipoRoteiro());
        roteiroRatingBar.setRating(roteiro.getNotaRoteiro());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalDetailsActivity.setConsultando(false);
    }
}
