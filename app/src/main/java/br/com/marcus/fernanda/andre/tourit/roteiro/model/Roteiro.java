package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created by André on 30/09/2017.
 */

public class Roteiro implements Serializable {

    private int idRoteiroSqlite;
    private String idRoteiroFirebase;
    private String nomeRoteiro;
    private String criadorRoteiro;
    private String tipoRoteiro;
    private float notaRoteiro;
    private List<String> locaisRoteiro;
    private boolean publicado;
    private transient Bitmap imagemRoteiro;

    public Roteiro() {
    }

    @Exclude
    public Bitmap getImagemRoteiro() {
        return imagemRoteiro;
    }

    public void setImagemRoteiro(Bitmap imagemRoteiro) {
        this.imagemRoteiro = imagemRoteiro;
    }

    public String getIdRoteiroFirebase() {
        return idRoteiroFirebase;
    }

    public void setIdRoteiroFirebase(String idRoteiroFirebase) {
        this.idRoteiroFirebase = idRoteiroFirebase;
    }

    @Exclude
    public int getIdRoteiroSqlite() {
        return idRoteiroSqlite;
    }

    public void setIdRoteiroSqlite(int idRoteiroSqlite) {
        this.idRoteiroSqlite = idRoteiroSqlite;
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
