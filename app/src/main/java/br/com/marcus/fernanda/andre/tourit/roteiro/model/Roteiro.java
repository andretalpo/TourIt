package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import java.io.Serializable;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.model.Local;

/**
 * Created by André on 30/09/2017.
 */

public class Roteiro implements Serializable {

    private int idRoteiro;
    private String nomeRoteiro;
    private String criadorRoteiro;
    private String tipoRoteiro;
    private float notaRoteiro;
    private List<String> locaisRoteiro;
    private boolean publicado;

    public Roteiro() {
    }

    public int getIdRoteiro() {
        return idRoteiro;
    }

    public void setIdRoteiro(int idRoteiro) {
        this.idRoteiro = idRoteiro;
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

    public List<String> getLocaisRoteiro() {
        return locaisRoteiro;
    }

    public void setLocaisRoteiro(List<String> locaisRoteiro) {
        this.locaisRoteiro = locaisRoteiro;
    }

    public boolean isPublicado() {
        return publicado;
    }

    public void setPublicado(boolean publicado) {
        this.publicado = publicado;
    }
}
