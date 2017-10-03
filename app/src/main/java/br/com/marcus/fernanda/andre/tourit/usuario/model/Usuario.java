package br.com.marcus.fernanda.andre.tourit.usuario.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by André on 22/08/2017.
 */

public class Usuario {
    private String idGoogle;
    private String username;
    private String nomeUsuario;
    private String emailUsuario;
    private Bitmap fotoUsuario;
    private List<String> roteiros;
    private boolean admnistrador;
    private boolean ativo;

    public Usuario(){
        //vazio
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

    public List<String> getRoteiros() {
        return roteiros;
    }

    public void setRoteiros(List<String> roteiros) {
        this.roteiros = roteiros;
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
