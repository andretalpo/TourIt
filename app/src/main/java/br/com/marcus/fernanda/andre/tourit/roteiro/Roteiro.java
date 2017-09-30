package br.com.marcus.fernanda.andre.tourit.roteiro;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.model.Local;

/**
 * Created by André on 30/09/2017.
 */

public class Roteiro {

    private String nomeRoteiro;
    private String criadorRoteiro;
    private String tipoRoteiro;
    private float notaRoteiro;
    private List<Local> locaisRoteiro;
    private boolean publicado;

    public Roteiro() {
    }

    public String getNomeRoteiro() {
        return nomeRoteiro;
    }

    public void setNomeRoteiro(String nomeRoteiro) {
        this.nomeRoteiro = nomeRoteiro;
    }

    public String getCriadorRoteiro() {
        return criadorRoteiro;
    }

    public void setCriadorRoteiro(String criadorRoteiro) {
        this.criadorRoteiro = criadorRoteiro;
    }

    public String getTipoRoteiro() {
        return tipoRoteiro;
    }

    public void setTipoRoteiro(String tipoRoteiro) {
        this.tipoRoteiro = tipoRoteiro;
    }

    public float getNotaRoteiro() {
        return notaRoteiro;
    }

    public void setNotaRoteiro(float notaRoteiro) {
        this.notaRoteiro = notaRoteiro;
    }

    public List<Local> getLocaisRoteiro() {
        return locaisRoteiro;
    }

    public void setLocaisRoteiro(List<Local> locaisRoteiro) {
        this.locaisRoteiro = locaisRoteiro;
    }

    public boolean isPublicado() {
        return publicado;
    }

    public void setPublicado(boolean publicado) {
        this.publicado = publicado;
    }
}
