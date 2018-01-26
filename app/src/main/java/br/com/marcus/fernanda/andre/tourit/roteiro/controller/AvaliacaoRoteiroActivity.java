package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.AvaliacaoRoteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;

public class AvaliacaoRoteiroActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText comentario;
    private String idRoteiro;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao_roteiro);

        idRoteiro = getIntent().getStringExtra("idRoteiro");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.salvarAvalicaoRoteiroActivityFab);
        comentario = (EditText) findViewById(R.id.comentarioAvaliacaoRoteiroEditText);
        ratingBar = (RatingBar) findViewById(R.id.avaliacaoRoteiroActivityRatingBar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AvaliacaoRoteiro avaliacaoRoteiro = new AvaliacaoRoteiro();
                avaliacaoRoteiro.setComentario(comentario.getText().toString());
                avaliacaoRoteiro.setIdAvaliador(MainActivity.idUsuarioGoogle);
                avaliacaoRoteiro.setNomeAvaliador(MainActivity.nomeUsuario);
                avaliacaoRoteiro.setNota(((int) ratingBar.getRating()));
                avaliacaoRoteiro.setIdRoteiro(idRoteiro);

                new AvaliarRoteiroTask().execute(avaliacaoRoteiro);
            }
        });
    }

    private class AvaliarRoteiroTask extends AsyncTask<AvaliacaoRoteiro, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AvaliacaoRoteiroActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.registrando_avaliacao));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(AvaliacaoRoteiro... avaliacaoRoteiro) {
            new RoteiroService(AvaliacaoRoteiroActivity.this, MainActivity.idUsuarioGoogle).salvarAvaliacaoRoteiro(avaliacaoRoteiro[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            finish();
        }
    }
}
