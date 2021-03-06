package br.com.marcus.fernanda.andre.tourit.evento.model;

import com.google.firebase.database.Exclude;

import java.util.List;

/**
 * Created by André on 06/02/2018.
 */

public class Evento {

    private String nomeEvento;
    private String horaInicio;
    private String horaFim;
    private String criadorEvento;
    private String idCriadorEvento;
    private String idEventoFirebase;
    private String idRoteiroFirebase;
    private String dataEvento;
    private long idEventoSqlite;
    private List<Convite> convidados;

    public Evento() {

    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getCriadorEvento() {
        return criadorEvento;
    }

    public void setCriadorEvento(String criadorEvento) {
        this.criadorEvento = criadorEvento;
    }

    public String getIdCriadorEvento() {
        return idCriadorEvento;
    }

    public void setIdCriadorEvento(String idCriadorEvento) {
        this.idCriadorEvento = idCriadorEvento;
    }

    public String getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(String dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getIdEventoFirebase() {
        return idEventoFirebase;
    }

    public void setIdEventoFirebase(String idEventoFirebase) {
        this.idEventoFirebase = idEventoFirebase;
    }

    public String getIdRoteiroFirebase() {
        return idRoteiroFirebase;
    }

    public void setIdRoteiroFirebase(String idRoteiroFirebase) {
        this.idRoteiroFirebase = idRoteiroFirebase;
    }

    public List<Convite> getConvidados() {
        return convidados;
    }

    public void setConvidados(List<Convite> convidados) {
        this.convidados = convidados;
    }

    @Exclude
    public long getIdEventoSqlite() {
        return idEventoSqlite;
    }

    public void setIdEventoSqlite(long idEventoSqlite) {
        this.idEventoSqlite = idEventoSqlite;
    }
}
