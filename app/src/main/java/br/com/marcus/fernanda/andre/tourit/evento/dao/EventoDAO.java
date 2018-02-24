package br.com.marcus.fernanda.andre.tourit.evento.dao;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;

/**
 * Created by André on 24/02/2018.
 */

public class EventoDAO {

    private Context context;
    private String idGoogle;

    public EventoDAO(Context context, String idGoogle) {
        this.context = context;
        this.idGoogle = idGoogle;
    }


    public void salvarEvento(Evento evento) {
        String key = FirebaseDatabase.getInstance().getReference().child("Eventos").push().getKey();
        evento.setIdEventoFirebase(key);
        FirebaseDatabase.getInstance().getReference().child("Eventos").child(key).setValue(evento);
    }
}
