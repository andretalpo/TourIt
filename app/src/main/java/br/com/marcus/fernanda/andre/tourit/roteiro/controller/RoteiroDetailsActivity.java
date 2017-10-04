package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;
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

        Roteiro roteiro = new RoteiroService(this, MainActivity.idUsuarioGoogle).consultarRoteiro(getIntent().getIntExtra("idRoteiro", -1));
        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
    }
}
