package br.com.marcus.fernanda.andre.tourit.local.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by André on 11/09/2017.
 */

public class Local implements Serializable{
    private String idPlaces;
    private String nome;
    private List<String> tipo;
    private String endereco;
    private transient Bitmap foto;
    private float nota;
    private List<AvaliacaoLocal> avaliacoes;
    private double lat;
    private double lng;

    public Local(){
        //vazio
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

    public List<String> getTipo() {
        return tipo;
    }

    public void setTipo(List<String> tipo) {
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
