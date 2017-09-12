package br.com.marcus.fernanda.andre.tourit.local.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by André on 11/09/2017.
 */

public class Local {
    private String nome;
    private String descricao;
    private String tipo;
    private String endereco;
    private Bitmap foto;
    private float nota;
    private List<AvaliacaoLocal> avaliacoes;

    public Local(){
        //vazio
    }

    public Local(String nome, String descricao, String tipo, String endereco, Bitmap foto, float nota, List<AvaliacaoLocal> avaliacoes) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.endereco = endereco;
        this.foto = foto;
        this.nota = nota;
        this.avaliacoes = avaliacoes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
}
