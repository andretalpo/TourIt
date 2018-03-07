package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;
import br.com.marcus.fernanda.andre.tourit.evento.model.EventoService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;

public class EventoDetailsActivity extends AppCompatActivity {

    private Evento evento;
    private TextView nomeEventoTextView;
    private TextView dataEventoTextView;
    private TextView horaInicioTextView;
    private TextView horaFimTextView;

    private Roteiro roteiro;
    private ImageView roteiroImageView;
    private TextView nomeRoteiroTextView;
    private RatingBar notaRatingBar;

    private static boolean consultando = true;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_details);

        nomeEventoTextView = (TextView) findViewById(R.id.nomeEventoEventoDetailsTextView);
        horaInicioTextView = (TextView) findViewById(R.id.horaInicioEventoEventoDetailsTextView);
        horaFimTextView = (TextView) findViewById(R.id.horaFimEventoEventoDetailsTextView);
        dataEventoTextView = (TextView) findViewById(R.id.dataEventoEventoDetailsTextView);

        roteiroImageView = (ImageView) findViewById(R.id.imagemRoteiroEventoDetailsImageView);
        nomeRoteiroTextView = (TextView) findViewById(R.id.nomeRoteiroEventoDetailsTextView);
        notaRatingBar = (RatingBar) findViewById(R.id.roteiroEventoDetailsRatingBar);

        String id = getIntent().getStringExtra("idEvento");
        evento = new EventoService(this, MainActivity.idUsuarioGoogle).consultarEventoPorIdFirebase(getIntent().getStringExtra("idEvento"));
        roteiro = new RoteiroService(this, MainActivity.idUsuarioGoogle).consultarRoteiro(evento.getIdRoteiroFirebase());

        nomeEventoTextView.setText(evento.getNomeEvento());
        dataEventoTextView.setText(evento.getDataEvento());
        horaFimTextView.setText(evento.getHoraFim());
        horaInicioTextView.setText(evento.getHoraInicio());

        roteiroImageView.setImageBitmap(roteiro.getImagemRoteiro());
        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        notaRatingBar.setRating(roteiro.getNotaRoteiro());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ConviteListFragment conviteFragment = new ConviteListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("acao", "consulta");
        bundle.putString("idEvento", evento.getIdEventoFirebase());
        consultando = true;
        conviteFragment.setArguments(bundle);

        transaction.replace(R.id.listaConvidadosEventoDetailsFrameLayout, conviteFragment);
        transaction.commit();

        ImageView excluirImageView = (ImageView) findViewById(R.id.excluirEventoDetailsActivityImageView);
        ImageView alterarImageVIew = (ImageView) findViewById(R.id.editarEventoDetailsActivityImageView);

        excluirImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventoDetailsActivity.this, R.style.DialogTheme).setMessage(R.string.mensagem_excluir_evento);

                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new ExcluirEventoTask().execute(evento.getIdEventoFirebase());
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        alterarImageVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterarEvento(evento.getIdEventoFirebase(), evento.getIdRoteiroFirebase());
            }
        });

        ImageView aceitarImageView = (ImageView) findViewById(R.id.responderSimConviteDetailsActivityImageView);
        ImageView recusarImageView = (ImageView) findViewById(R.id.responderNaoConviteDetailsActivityImageView);

        aceitarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recusarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public static boolean isConsultando() {
        return consultando;
    }

    public static void setConsultando(boolean consultando) {
        EventoDetailsActivity.consultando = consultando;
    }

    private class ExcluirEventoTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EventoDetailsActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.excluindo_roteiro));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... idEvento) {
            new EventoService(EventoDetailsActivity.this, MainActivity.idUsuarioGoogle).excluirEvento(idEvento[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            Toast.makeText(EventoDetailsActivity.this, getResources().getString(R.string.evento_excluido_sucesso), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            EventoDetailsActivity.this.finish();
        }
    }

    private void alterarEvento(String idEvento, String idRoteiro){
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra("idEvento", idEvento);
        intent.putExtra("idRoteiro", idRoteiro);
        startActivity(intent);
        finish();
    }
}
