package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;
import android.graphics.Bitmap;

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

    public String salvarEvento(Evento evento) {
        EventoDAO eventoDAO = new EventoDAO(context, idGoogle);
        eventoDAO.salvarEventoFirebase(evento);
        return eventoDAO.salvarEventoSQLite(evento);
    }

    public List<Evento> consultarMeusEventos() {
        return new EventoDAO(context, idGoogle).consultarMeusEventos();
    }

    public void salvarImagemConvite(byte[] imagemConvidado, String idUsuarioGoogleConvidado, String idEvento) {
        new EventoDAO(context, idGoogle).salvarImagemConvite(imagemConvidado, idUsuarioGoogleConvidado, idEvento);
    }

    public void atualizarEvento(Evento evento){
        new EventoDAO(context, idGoogle).atualizarEventoFirebase(evento);
        new EventoDAO(context, idGoogle).atualizarEventoSqlite(evento);
    }

    public void excluirEvento(String idEvento){
        EventoDAO eventoDAO = new EventoDAO(context, idGoogle);
        eventoDAO.excluirEventoFirebase(idEvento);
        eventoDAO.excluirEventoSqlite(idEvento);
    }

    public Evento consultarEventoPorIdFirebase(String idEvento) {
        return new EventoDAO(context, idGoogle).consultarEventoPorIdFirebase(idEvento);
    }

    public List<Convite> consultarConvitesEvento(String idEvento) {
        return new EventoDAO(context, idGoogle).consultarConvitesEvento(idEvento);
    }

    public List<Evento> consultarEventosConvidado(String idUsuarioGoogle) {
//        return new EventoDAO(context, idUsuarioGoogle).consultarEventosConvidado(idUsuarioGoogle);
        return null;
    }
}
