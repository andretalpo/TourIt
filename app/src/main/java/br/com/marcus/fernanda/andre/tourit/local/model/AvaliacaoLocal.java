package br.com.marcus.fernanda.andre.tourit.local.model;

import java.io.Serializable;

/**
 * Created by André on 11/09/2017.
 */

public class AvaliacaoLocal implements Serializable {
    private String nomeAvaliador;
    private String comentario;
    private float nota;

    public AvaliacaoLocal(){
        //vazio
    }

    public String getNomeAvaliador() {
        return nomeAvaliador;
    }

    public void setNomeAvaliador(String nomeAvaliador) {
        this.nomeAvaliador = nomeAvaliador;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }
}