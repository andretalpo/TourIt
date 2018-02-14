package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.graphics.Bitmap;

/**
 * Created by André on 06/02/2018.
 */

public class Convite {

    public static int ACEITO = 1;
    public static int RECUSADO = -1;
    public static int AGUARDANDO_RESPOSTA = 0;

    private String usuarioConvidado;
    private String usernameUsuarioConvidado;
    private int respostaConvite;
    private transient Bitmap fotoConvidado;

    public Convite() {
    }

    public String getUsuarioConvidado() {
        return usuarioConvidado;
    }

    public void setUsuarioConvidado(String usuarioConvidado) {
        this.usuarioConvidado = usuarioConvidado;
    }

    public String getUsernameUsuarioConvidado() {
        return usernameUsuarioConvidado;
    }

    public void setUsernameUsuarioConvidado(String usernameUsuarioConvidado) {
        this.usernameUsuarioConvidado = usernameUsuarioConvidado;
    }

    public int getRespostaConvite() {
        return respostaConvite;
    }

    public void setRespostaConvite(int respostaConvite) {
        this.respostaConvite = respostaConvite;
    }

    public Bitmap getFotoConvidado() {
        return fotoConvidado;
    }

    public void setFotoConvidado(Bitmap fotoConvidado) {
        this.fotoConvidado = fotoConvidado;
    }
}
