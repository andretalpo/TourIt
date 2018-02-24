package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;

import br.com.marcus.fernanda.andre.tourit.evento.dao.EventoDAO;

/**
 * Created by André on 24/02/2018.
 */

public class EventoService {

    private Context context;
    private String idGoogle;

    public EventoService(Context context, String idGoogle) {
        this.context = context;
        this.idGoogle = idGoogle;
    }


    public void salvarEvento(Evento evento) {
        new EventoDAO(context, idGoogle).salvarEvento(evento);
    }
}
