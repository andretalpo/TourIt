package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

/**
 * Created by André on 06/02/2018.
 */

public class Convite {

    public static int ACEITO = 1;
    public static int RECUSADO = -1;
    public static int AGUARDANDO_RESPOSTA = 0;

    private String usuarioConvidado;
    private String usernameUsuarioConvidado;
    private String idUsuarioGoogleConvidado;
    private String emailUsuarioConvidado;
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

    public String getIdUsuarioGoogleConvidado() {
        return idUsuarioGoogleConvidado;
    }

    public void setIdUsuarioGoogleConvidado(String idUsuarioGoogleConvidado) {
        this.idUsuarioGoogleConvidado = idUsuarioGoogleConvidado;
    }

    public String getEmailUsuarioConvidado() {
        return emailUsuarioConvidado;
    }

    public void setEmailUsuarioConvidado(String emailUsuarioConvidado) {
        this.emailUsuarioConvidado = emailUsuarioConvidado;
    }

    public int getRespostaConvite() {
        return respostaConvite;
    }

    public void setRespostaConvite(int respostaConvite) {
        this.respostaConvite = respostaConvite;
    }

    @Exclude
    public Bitmap getFotoConvidado() {
        return fotoConvidado;
    }

    public void setFotoConvidado(Bitmap fotoConvidado) {
        this.fotoConvidado = fotoConvidado;
    }
}
