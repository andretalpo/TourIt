package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;

import java.util.List;

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
        EventoDAO eventoDAO = new EventoDAO(context, idGoogle);
        eventoDAO.salvarEventoFirebase(evento);
        eventoDAO.salvarEventoSQLite(evento);
    }

    public List<Evento> consultarMeusEventos() {
        EventoDAO eventoDAO = new EventoDAO(context, idGoogle);
        return eventoDAO.consultarMeusEventos();
    }
}
