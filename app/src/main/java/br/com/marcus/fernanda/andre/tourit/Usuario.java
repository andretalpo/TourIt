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
    private String fotoUsuario;
    private boolean admnistrador;
    private boolean ativo;

    public Usuario(String idGoogle, String username, String nomeUsuario, String emailUsuario, String fotoUsuario, boolean admnistrador, boolean ativo) {
        this.idGoogle = idGoogle;
        this.username = username;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.fotoUsuario = fotoUsuario;
        this.admnistrador = admnistrador;
        this.ativo = ativo;
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

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public boolean isAdmnistrador() {
        return admnistrador;
    }

    public void setAdmnistrador(boolean admnistrador) {
        this.admnistrador = admnistrador;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
