package br.com.marcus.fernanda.andre.tourit.evento.model;

/**
 * Created by André on 06/02/2018.
 */

public class Convite {

    public static int ACEITO = 1;
    public static int RECUSADO = -1;
    public static int AGUARDANDO_RESPOSTA = 0;

    private String usuarioConvidado;
    private int respostaConvite;

    public Convite() {
    }

    public String getUsuarioConvidado() {
        return usuarioConvidado;
    }

    public void setUsuarioConvidado(String usuarioConvidado) {
        this.usuarioConvidado = usuarioConvidado;
    }

    public int getRespostaConvite() {
        return respostaConvite;
    }

    public void setRespostaConvite(int respostaConvite) {
        this.respostaConvite = respostaConvite;
    }
}
