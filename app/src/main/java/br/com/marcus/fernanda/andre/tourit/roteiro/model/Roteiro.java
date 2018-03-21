package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created by André on 30/09/2017.
 */

public class Roteiro implements Serializable, Comparable {

    private Long idRoteiroSqlite;
    private String idRoteiroFirebase;
    private String nomeRoteiro;
    private String criadorRoteiro;
    private String tipoRoteiro;
    private float notaRoteiro;
    private List<String> idLocaisRoteiro;
    private List<String> nomeLocaisRoteiro;
    private boolean publicado;
    private boolean seguido;
    private transient Bitmap imagemRoteiro;
    private String rota;
    private int duracao;
    private int preco;
    private int numSeguidores;
    private String dicas;

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
    public Long getIdRoteiroSqlite() {
        return idRoteiroSqlite;
    }

    public void setIdRoteiroSqlite(Long idRoteiroSqlite) {
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

    public List<String> getIdLocaisRoteiro() {
        return idLocaisRoteiro;
    }

    public void setIdLocaisRoteiro(List<String> idLocaisRoteiro) {
        this.idLocaisRoteiro = idLocaisRoteiro;
    }

    public boolean isPublicado() {
        return publicado;
    }

    public void setPublicado(boolean publicado) {
        this.publicado = publicado;
    }

    @Exclude
    public boolean isSeguido() {
        return seguido;
    }

    public void setSeguido(boolean seguido) {
        this.seguido = seguido;
    }

    public String getRota() {
        return rota;
    }

    public void setRota(String rota) {
        this.rota = rota;
    }

    public List<String> getNomeLocaisRoteiro() {
        return nomeLocaisRoteiro;
    }

    public void setNomeLocaisRoteiro(List<String> nomeLocaisRoteiro) {
        this.nomeLocaisRoteiro = nomeLocaisRoteiro;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public int getPreco() {
        return preco;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }

    public String getDicas() {
        return dicas;
    }

    public void setDicas(String dicas) {
        this.dicas = dicas;
    }

    public int getNumSeguidores() {
        return numSeguidores;
    }

    public void setNumSeguidores(int numSeguidores) {
        this.numSeguidores = numSeguidores;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Roteiro roteiro = (Roteiro) o;
        float notaDesse = this.getNotaRoteiro();
        float notaComparado = roteiro.getNotaRoteiro();
        if(notaDesse > notaComparado){
            return 1;
        }
        if(notaDesse < notaComparado){
            return -1;
        }
        return 0;
    }
}
