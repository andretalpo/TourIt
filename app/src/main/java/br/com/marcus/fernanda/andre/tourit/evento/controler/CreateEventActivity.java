package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import br.com.marcus.fernanda.andre.tourit.R;

public class CreateEventActivity extends AppCompatActivity {

    private EditText dataEditText;
    private EditText horaInicioEditText;
    private EditText horaFimEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        dataEditText = (EditText) findViewById(R.id.dataEventoCreateEventActivityEditText);
        horaInicioEditText = (EditText) findViewById(R.id.horaInicioEventoCreateEventActivityEditText);
        horaFimEditText = (EditText) findViewById(R.id.horaFimEventoCreateEventActivityEditText);

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
    }
}
