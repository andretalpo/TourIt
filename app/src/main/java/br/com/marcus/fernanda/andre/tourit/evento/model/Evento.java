package br.com.marcus.fernanda.andre.tourit.evento.model;

import java.util.List;

/**
 * Created by André on 06/02/2018.
 */

public class Evento {

    private String nomeEvento;
    private String horaInicio;
    private String horaFim;
    private String criadorEvento;
    private String idCriadorEvento; //???
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

    public List<Convite> getConvidados() {
        return convidados;
    }

    public void setConvidados(List<Convite> convidados) {
        this.convidados = convidados;
    }
}
