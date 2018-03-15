package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;
import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;
import br.com.marcus.fernanda.andre.tourit.evento.model.EventoService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.controller.PesquisaUsuarioActivity;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

public class CreateEventActivity extends AppCompatActivity {

    private static List<Convite> listaConvidadosEvento;

    private ProgressDialog progressDialog;
    private EditText dataEditText;
    private EditText horaInicioEditText;
    private EditText horaFimEditText;
    private FloatingActionButton fab;
    private EditText nomeEventoEditText;
    private FrameLayout convidadosEventoFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        nomeEventoEditText = (EditText) findViewById(R.id.nomeEventoCreateEventActivityEditText);
        dataEditText = (EditText) findViewById(R.id.dataEventoCreateEventActivityEditText);
        horaInicioEditText = (EditText) findViewById(R.id.horaInicioEventoCreateEventActivityEditText);
        horaFimEditText = (EditText) findViewById(R.id.horaFimEventoCreateEventActivityEditText);
        fab = (FloatingActionButton) findViewById(R.id.criarEventoCreateEventActivityFab);
        convidadosEventoFrameLayout = (FrameLayout) findViewById(R.id.listaConvidadosCreateEventActivityFrameLayout);

        EventoDetailsActivity.setConsultando(false);

        if(listaConvidadosEvento == null){
            listaConvidadosEvento = new ArrayList<>();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ConviteListFragment conviteListFragment = new ConviteListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("acao", "criacao");
        conviteListFragment.setArguments(bundle);
        transaction.replace(R.id.listaConvidadosCreateEventActivityFrameLayout, conviteListFragment);
        transaction.commit();

        final DatePickerDialog.OnDateSetListener dateEventPicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dataEditText.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
            }
        };

        dataEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateEventActivity.this, dateEventPicker, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener timeInicioEventPicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                horaInicioEditText.setText(hourOfDay + ":" + minute);
            }
        };

        horaInicioEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateEventActivity.this, timeInicioEventPicker, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener timeFimEventPicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                horaFimEditText.setText(hourOfDay + ":" + minute);
            }
        };

        horaFimEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateEventActivity.this, timeFimEventPicker, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true).show();
            }
        });

        if(getIntent().getStringExtra("idEventoFirebase") != null){
            Evento evento = new EventoService(this, MainActivity.idUsuarioGoogle).consultarEventoPorIdFirebase(getIntent().getStringExtra("idEventoFirebase"));
            nomeEventoEditText.setText(evento.getNomeEvento());
            dataEditText.setText(evento.getDataEvento());
            horaInicioEditText.setText(evento.getHoraInicio());
            horaFimEditText.setText(evento.getHoraFim());
            listaConvidadosEvento.addAll(evento.getConvidados());
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//André vai validar
                Evento evento = new Evento();
                evento.setCriadorEvento(MainActivity.nomeUsuario);
                evento.setIdCriadorEvento(MainActivity.idUsuarioGoogle);
                evento.setNomeEvento(nomeEventoEditText.getText().toString());
                evento.setHoraInicio(horaInicioEditText.getText().toString());
                evento.setHoraFim(horaFimEditText.getText().toString());
                evento.setDataEvento(dataEditText.getText().toString());
                evento.setIdRoteiroFirebase(getIntent().getStringExtra("idRoteiro"));
                evento.setConvidados(listaConvidadosEvento);
                if(getIntent().getStringExtra("idEventoFirebase") == null){
                    new CriarEventoTask().execute(evento);
                }else {
                    evento.setIdEventoFirebase(getIntent().getStringExtra("idEventoFirebase"));
                    evento.setIdEventoSqlite(getIntent().getLongExtra("idEventoSqlite", 0));
                    new AtualizarEventoTask().execute(evento);
                }
            }
        });

        Button adicionarConvidadoButton = (Button) findViewById(R.id.adicionarConvidadoCreateEventActivityButton);
        adicionarConvidadoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irParaTelaBuscaUsuario();
            }
        });
    }

    private void irParaTelaBuscaUsuario() {
        Intent intent = new Intent(this, PesquisaUsuarioActivity.class);
        startActivity(intent);
    }

    private class CriarEventoTask extends AsyncTask<Evento, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CreateEventActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.salvando_evento));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Evento... evento) {
            String idEvento = new EventoService(CreateEventActivity.this, MainActivity.idUsuarioGoogle).salvarEvento(evento[0]);
            for(Convite convite : evento[0].getConvidados()){
                armazenarImagem(convite, idEvento);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Toast.makeText(CreateEventActivity.this, getResources().getString(R.string.evento_salvo_sucesso), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void armazenarImagem(final Convite convite, final String idEvento){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imagemUsuario/" + convite.getIdUsuarioGoogleConvidado() + ".jpeg");

        final long ONE_MEGABYTE = 300 * 300;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] imagemConvidado) {
                new EventoService(CreateEventActivity.this, MainActivity.idUsuarioGoogle).salvarImagemConvite(imagemConvidado, convite.getIdUsuarioGoogleConvidado(), idEvento);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public static List<Convite> getListaConvidadosEvento() {
        return listaConvidadosEvento;
    }

    public static void setListaConvidadosEvento(List<Convite> listaConvidadosEvento) {
        CreateEventActivity.listaConvidadosEvento = listaConvidadosEvento;
    }

    private class AtualizarEventoTask extends AsyncTask<Evento, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CreateEventActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.alterando_evento));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Evento... evento) {
            new EventoService(CreateEventActivity.this, MainActivity.idUsuarioGoogle).atualizarEvento(evento[0]);
            for(Convite convite : evento[0].getConvidados()){
                armazenarImagem(convite, evento[0].getIdEventoFirebase());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
            Toast.makeText(CreateEventActivity.this, getResources().getString(R.string.evento_alterado_sucesso), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listaConvidadosEvento != null){
            listaConvidadosEvento = null;
        }
    }
}