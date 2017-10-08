package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalDetailsActivity;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;

public class RoteiroDetailsActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

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

        final Roteiro roteiro = (Roteiro) getIntent().getSerializableExtra("roteiro");

        Bundle bundle = new Bundle();
        bundle.putString("acao", "consultaLocaisBanco");
        bundle.putInt("idRoteiro", roteiro.getIdRoteiroSqlite());
        localFragment.setArguments(bundle);

        transaction.replace(R.id.listaLocaisRoteiroDetailsFrameLayout, localFragment);
        transaction.commit();

        //Roteiro roteiro = new RoteiroService(this, MainActivity.idUsuarioGoogle).consultarRoteiro(getIntent().getIntExtra("idRoteiro", -1));
        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        nomeCriadorTextView.setText(roteiro.getCriadorRoteiro());
        tipoRoteiroTextView.setText(roteiro.getTipoRoteiro());
        roteiroRatingBar.setRating(roteiro.getNotaRoteiro());

        Button button = (Button) findViewById(R.id.botaoExcluir);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExcluirRoteiroTask().execute(roteiro);
            }
        });

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
            new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).excluirRoteiro(roteiro [0].getIdRoteiroSqlite());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            Toast.makeText(RoteiroDetailsActivity.this, "Roteiro excluído com sucesso!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            RoteiroDetailsActivity.this.finish();
        }
    }
}
