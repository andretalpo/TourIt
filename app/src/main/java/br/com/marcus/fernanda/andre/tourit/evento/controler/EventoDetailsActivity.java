package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

    private FrameLayout listaConvidadosFrameLayout;

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

        listaConvidadosFrameLayout = (FrameLayout) findViewById(R.id.listaConvidadosEventoDetailsFrameLayout);

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
        conviteFragment.setArguments(bundle);

        transaction.replace(R.id.listaConvidadosEventoDetailsFrameLayout, conviteFragment);
        transaction.commit();

    }
}
