package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;
import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;

public class CreateEventActivity extends AppCompatActivity {

    private static List<Convite> listaConvidadosEvento;

    private EditText dataEditText;
    private EditText horaInicioEditText;
    private EditText horaFimEditText;
    private FloatingActionButton fab;
    private EditText nomeEventoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        nomeEventoEditText = (EditText) findViewById(R.id.nomeEventoCreateEventActivityEditText);
        dataEditText = (EditText) findViewById(R.id.dataEventoCreateEventActivityEditText);
        horaInicioEditText = (EditText) findViewById(R.id.horaInicioEventoCreateEventActivityEditText);
        horaFimEditText = (EditText) findViewById(R.id.horaFimEventoCreateEventActivityEditText);
        fab = (FloatingActionButton) findViewById(R.id.criarEventoCreateEventActivityFab);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Evento evento = new Evento();
                evento.setCriadorEvento(MainActivity.nomeUsuario);
                evento.setIdCriadorEvento(MainActivity.idUsuarioGoogle);
                evento.setNomeEvento(nomeEventoEditText.getText().toString());
                evento.setHoraInicio(horaInicioEditText.getText().toString());
                evento.setHoraFim(horaFimEditText.getText().toString());
                //evento.setConvidados();
            }
        });

        Button adicionarConvidadoButton = (Button) findViewById(R.id.adicionarConvidadoCreateEventActivityButton);
        adicionarConvidadoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public static List<Convite> getListaConvidadosEvento() {
        return listaConvidadosEvento;
    }

    public static void setListaConvidadosEvento(List<Convite> listaConvidadosEvento) {
        CreateEventActivity.listaConvidadosEvento = listaConvidadosEvento;
    }
}
