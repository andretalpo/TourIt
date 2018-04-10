package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.evento.dao.EventoDAO;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuarioService;

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

    public List<Convite> consultarConvitesEventoSqlite(String idEvento) {
        return new EventoDAO(context, idGoogle).consultarConvitesEvento(idEvento);
    }

    public List<Evento> consultarEventosConvidado(String idUsuarioGoogle) {
        EventoDAO eventoDAO = new EventoDAO(context, idUsuarioGoogle);
        List<Evento> eventosConvidado = eventoDAO.consultarEventosConvidado(idUsuarioGoogle);
        return eventosConvidado;
    }

    public void excluirEventosConvidadoSqlite(String idGoogle){
        new EventoDAO(context, idGoogle).excluirConvitesSQLite(idGoogle);
    }

    public void atualizarEventosConvidado(List<Evento> eventos){
        for (Evento evento: eventos) {
            for(Convite convite : evento.getConvidados()){
                if(convite.getIdUsuarioGoogleConvidado().equals(idGoogle) && convite.getRespostaConvite() != Convite.RECUSADO) {
                    new EventoDAO(context, idGoogle).salvarEventoSQLite(evento);
                }
            }
        }
    }

    public List<Evento> consultarEventosConvidadoSqlite() {
        return new EventoDAO(context, idGoogle).consultarEventosConvidadoSqlite();
    }

    public void aceitarConvite(Evento evento) {
        EventoDAO eventoDAO = new EventoDAO(context, idGoogle);
        List<Convite> convites = eventoDAO.consultarConvitesEventoFirebase(evento.getIdEventoFirebase());
        for(Convite convite: convites){
            if(convite.getIdUsuarioGoogleConvidado().equals(idGoogle)){
                convite.setRespostaConvite(Convite.ACEITO);
            }
        }
        eventoDAO.responderConviteFirebase(evento.getIdEventoFirebase(), convites);
        RoteiroService roteiroService = new RoteiroService(context, idGoogle);
        Roteiro roteiro = roteiroService.consultarRoteiroSQLite(evento.getIdRoteiroFirebase());
        roteiroService.setarRoteiroSeguidoSqlite(roteiro);
        new UsuarioService().adicionarRoteiroSeguidoUsuario(idGoogle, roteiro.getIdRoteiroFirebase());
    }

    public void recusarConvite(Evento evento) {
        EventoDAO eventoDAO = new EventoDAO(context, idGoogle);
        List<Convite> convites = eventoDAO.consultarConvitesEventoFirebase(evento.getIdEventoFirebase());
        for(Convite convite: convites){
            if(convite.getIdUsuarioGoogleConvidado().equals(idGoogle)){
                convite.setRespostaConvite(Convite.RECUSADO);
            }
        }
        eventoDAO.responderConviteFirebase(evento.getIdEventoFirebase(), convites);
        RoteiroService roteiroService = new RoteiroService(context, idGoogle);
        Roteiro roteiro = roteiroService.consultarRoteiroSQLite(evento.getIdRoteiroFirebase());
        roteiroService.setarRoteiroNaoSeguidoSqlite(roteiro);
        new UsuarioDAO(context, idGoogle).excluirRoteiroSeguidoUsuario(roteiro.getIdRoteiroFirebase());
    }

    public List<Convite> consultarConvitesEventoFirebase(String idEvento) {
        return new EventoDAO(context, idGoogle).consultarConvitesEventoFirebase(idEvento);
    }

    public void atualizarConviteSqlite(String idEvento, Convite convite) {
        new EventoDAO(context, idGoogle).atualizarConviteSqlite(idEvento, convite);
    }
}
