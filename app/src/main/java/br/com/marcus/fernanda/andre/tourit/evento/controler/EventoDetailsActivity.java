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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.dao.EventoDAO;
import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;
import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;
import br.com.marcus.fernanda.andre.tourit.evento.model.EventoService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;

public class EventoDetailsActivity extends AppCompatActivity {

    private Evento evento;
    private TextView nomeEventoTextView;
    private TextView criadorEventoTextView;
    private TextView dataEventoTextView;
    private TextView horaInicioTextView;
    private TextView horaFimTextView;

    private Roteiro roteiro;
    private ImageView roteiroImageView;
    private TextView nomeRoteiroTextView;
    private RatingBar notaRatingBar;
    private TextView tipoRoteiro;

    private static boolean consultando = true;
    private ProgressDialog progressDialog;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_details);

        nomeEventoTextView = (TextView) findViewById(R.id.nomeEventoEventoDetailsTextView);
        criadorEventoTextView = (TextView) findViewById(R.id.criadorEventoEventoDetailsTextView);
        horaInicioTextView = (TextView) findViewById(R.id.horaInicioEventoEventoDetailsTextView);
        horaFimTextView = (TextView) findViewById(R.id.horaFimEventoEventoDetailsTextView);
        dataEventoTextView = (TextView) findViewById(R.id.dataEventoEventoDetailsTextView);

        roteiroImageView = (ImageView) findViewById(R.id.imagemRoteiroEventoDetailsImageView);
        nomeRoteiroTextView = (TextView) findViewById(R.id.nomeRoteiroEventoDetailsTextView);
        notaRatingBar = (RatingBar) findViewById(R.id.roteiroEventoDetailsRatingBar);
        tipoRoteiro = (TextView) findViewById(R.id.tipoRoteiroEventoDetailsTextView);

        String id = getIntent().getStringExtra("idEvento");
        evento = new EventoService(this, MainActivity.idUsuarioGoogle).consultarEventoPorIdFirebase(getIntent().getStringExtra("idEvento"));
        roteiro = new RoteiroService(this, MainActivity.idUsuarioGoogle).consultarRoteiroSQLite(evento.getIdRoteiroFirebase());

        ImageView roteiroImageView = (ImageView) findViewById(R.id.imagemRoteiroEventoDetailsImageView);
        storageReference = FirebaseStorage.getInstance().getReference().child("imagemRoteiro/" + roteiro.getIdRoteiroFirebase() + ".jpeg");
        Glide.with(this).using(new FirebaseImageLoader()).load(storageReference).into(roteiroImageView);
        //Buscar imagem pelo glide no sqlite

        nomeEventoTextView.setText(evento.getNomeEvento());
        criadorEventoTextView.setText(evento.getCriadorEvento());
        dataEventoTextView.setText(evento.getDataEvento());
        horaFimTextView.setText(evento.getHoraFim());
        horaInicioTextView.setText(evento.getHoraInicio());

        roteiroImageView.setImageBitmap(roteiro.getImagemRoteiro());
        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        notaRatingBar.setRating(roteiro.getNotaRoteiro());
        tipoRoteiro.setText(roteiro.getTipoRoteiro());

        ImageView excluirImageView = (ImageView) findViewById(R.id.excluirEventoDetailsActivityImageView);
        ImageView alterarImageVIew = (ImageView) findViewById(R.id.editarEventoDetailsActivityImageView);
        ImageView aceitarImageView = (ImageView) findViewById(R.id.responderSimConviteDetailsActivityImageView);
        ImageView recusarImageView = (ImageView) findViewById(R.id.responderNaoConviteDetailsActivityImageView);
        TextView respostaTextView = (TextView) findViewById(R.id.respostaConviteEventoDetailsTextView);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ConviteListFragment conviteFragment = new ConviteListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("acao", "consulta");
        bundle.putString("idEvento", evento.getIdEventoFirebase());
        consultando = true;
        conviteFragment.setArguments(bundle);

        if(MainActivity.idUsuarioGoogle.equals(evento.getIdCriadorEvento())){
            aceitarImageView.setVisibility(View.GONE);
            recusarImageView.setVisibility(View.GONE);
            excluirImageView.setVisibility(View.VISIBLE);
            alterarImageVIew.setVisibility(View.VISIBLE);
        }else{
            aceitarImageView.setVisibility(View.VISIBLE);
            recusarImageView.setVisibility(View.VISIBLE);
            excluirImageView.setVisibility(View.GONE);
            alterarImageVIew.setVisibility(View.GONE);
            respostaTextView.setVisibility(View.VISIBLE);
            inicializarRespostaConvite(respostaTextView);
        }

        transaction.replace(R.id.listaConvidadosEventoDetailsFrameLayout, conviteFragment);
        transaction.commit();

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
                alterarEvento(evento);
            }
        });

        aceitarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceitarConvite(evento);
            }
        });

        recusarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recusarConvite(evento);
            }
        });

    }

    private void inicializarRespostaConvite(TextView respostaTextView) {
        List<Convite> convites =  new EventoService(this, MainActivity.idUsuarioGoogle).consultarConvitesEvento(evento.getIdEventoFirebase());
        int resposta = Convite.AGUARDANDO_RESPOSTA;
        if(convites != null) {
            for (Convite convite : convites) {
                if (convite.getIdUsuarioGoogleConvidado().equals(MainActivity.idUsuarioGoogle)) {
                    resposta = convite.getRespostaConvite();
                }
            }
        }

        switch (resposta){
            case 1:
                respostaTextView.setText(getResources().getString(R.string.aceito));
                respostaTextView.setTextColor(getResources().getColor(R.color.cor_verde));
                break;
            case -1:
                respostaTextView.setText(getResources().getString(R.string.recusado));
                respostaTextView.setTextColor(getResources().getColor(R.color.cor_vermelho));
                break;
            default:
                respostaTextView.setText(getResources().getString(R.string.aguardando_resposta));
                respostaTextView.setTextColor(getResources().getColor(R.color.cor_azul));
                break;
        }
    }

    private void recusarConvite(final Evento evento) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventoDetailsActivity.this, R.style.DialogTheme).setMessage(R.string.mensagem_excluir_convite);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.aceitar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new RecusarConviteTask().execute(evento);
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

    private void aceitarConvite(Evento evento) {
        new AceitarConviteTask().execute(evento);
    }

    public static boolean isConsultando() {
        return consultando;
    }

    public static void setConsultando(boolean consultando) {
        EventoDetailsActivity.consultando = consultando;
    }

    private class AceitarConviteTask extends AsyncTask<Evento, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Evento... evento) {
            new EventoService(EventoDetailsActivity.this, MainActivity.idUsuarioGoogle).aceitarConvite(evento[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            Toast.makeText(EventoDetailsActivity.this, getResources().getString(R.string.convite_aceito), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("atualizarListaEventos");
            sendBroadcast(intent);
            EventoDetailsActivity.this.finish();
        }
    }

    private class RecusarConviteTask extends AsyncTask<Evento, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Evento... evento) {
            new EventoService(EventoDetailsActivity.this, MainActivity.idUsuarioGoogle).recusarConvite(evento[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            Toast.makeText(EventoDetailsActivity.this, getResources().getString(R.string.convite_recusado), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("atualizarListaEventos");
            sendBroadcast(intent);
            EventoDetailsActivity.this.finish();
        }
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

    private void alterarEvento(Evento evento){
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra("idEventoFirebase", evento.getIdEventoFirebase());
        intent.putExtra("idEventoSqlite", evento.getIdEventoSqlite());
        intent.putExtra("idRoteiro", evento.getIdRoteiroFirebase());
        startActivity(intent);
        finish();
    }
}
