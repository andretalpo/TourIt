package br.com.marcus.fernanda.andre.tourit.local.model;

/**
 * Created by André on 11/09/2017.
 */

public class AvaliacaoLocal {
    private String nomeAvaliador;
    private String comentario;
    private double nota;

    public AvaliacaoLocal(String nomeAvaliador, String comentario, double nota) {
        this.nomeAvaliador = nomeAvaliador;
        this.comentario = comentario;
        this.nota = nota;
    }

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

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }
}