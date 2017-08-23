package br.com.marcus.fernanda.andre.tourit;

import android.graphics.Bitmap;

/**
 * Created by André on 22/08/2017.
 */

public class Usuario {
    private String idGoogle;
    private String username;
    private String nomeUsuario;
    private String emailUsuario;
    private Bitmap fotoUsuario;

    public Usuario(String idGoogle, String username, String nomeUsuario, String emailUsuario, Bitmap fotoUsuario) {
        this.idGoogle = idGoogle;
        this.username = username;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.fotoUsuario = fotoUsuario;
    }

    public Usuario(){
        //Construtor vazio para firebase
    }

    public String getIdGoogle() {
        return idGoogle;
    }

    public void setIdGoogle(String idGoogle) {
        this.idGoogle = idGoogle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public Bitmap getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(Bitmap fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

}
