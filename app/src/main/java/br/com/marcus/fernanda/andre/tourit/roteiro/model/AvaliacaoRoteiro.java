package br.com.marcus.fernanda.andre.tourit.roteiro.model;

/**
 * Created by André on 26/01/2018.
 */

public class AvaliacaoRoteiro {

    private int nota;
    private String comentario;
    private String nomeAvaliador;
    private String idAvaliador;
    private String idRoteiro;

    public AvaliacaoRoteiro() {
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNomeAvaliador() {
        return nomeAvaliador;
    }

    public void setNomeAvaliador(String nomeAvaliador) {
        this.nomeAvaliador = nomeAvaliador;
    }

    public String getIdAvaliador() {
        return idAvaliador;
    }

    public void setIdAvaliador(String idAvaliador) {
        this.idAvaliador = idAvaliador;
    }

    public String getIdRoteiro() {
        return idRoteiro;
    }

    public void setIdRoteiro(String idRoteiro) {
        this.idRoteiro = idRoteiro;
    }
}
