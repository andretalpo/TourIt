package br.com.marcus.fernanda.andre.tourit.local.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by André on 11/09/2017.
 */

public class Local {
    private String idPlaces;
    private String nome;
    private String tipo;
    private String endereco;
    private Bitmap foto;
    private float nota;
    private List<AvaliacaoLocal> avaliacoes;
    private LatLng latLng;

    public Local(){
        //vazio
    }

    public Local(String idPlaces, String nome, String tipo, String endereco, Bitmap foto, float nota, List<AvaliacaoLocal> avaliacoes, LatLng latLng) {
        this.idPlaces = idPlaces;
        this.nome = nome;
        this.tipo = tipo;
        this.endereco = endereco;
        this.foto = foto;
        this.nota = nota;
        this.avaliacoes = avaliacoes;
        this.latLng = latLng;
    }

    public String getIdPlaces() {
        return idPlaces;
    }

    public void setIdPlaces(String idPlaces) {
        this.idPlaces = idPlaces;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public List<AvaliacaoLocal> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<AvaliacaoLocal> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
