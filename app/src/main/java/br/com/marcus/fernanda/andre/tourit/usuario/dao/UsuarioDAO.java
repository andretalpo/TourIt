package br.com.marcus.fernanda.andre.tourit.usuario.dao;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;

/**
 * Created by André on 03/09/2017.
 */

public class UsuarioDAO {

    public static void salvarUsuario(Usuario usuario, byte[] imagemBytes) {
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("imagemUsuario/" + usuario.getIdGoogle() + ".jpeg").putBytes(imagemBytes);
        FirebaseDatabase.getInstance().getReference().child("Usuarios").push().setValue(usuario);
    }

    public static Usuario consultarUsuario(String parametro, String valor){
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Usuarios.json?orderBy=%22" +
                    parametro + "%22&equalTo=%22" + valor +"%22");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK){
                StringBuilder builder = new StringBuilder ();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                    String line;
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                }
                catch (IOException e){
                    //nada
                }
                return convertJSONToUsuario(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            //nada
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    public static List<Usuario> listarUsuariosUsername(String username){
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Usuarios.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK){
                StringBuilder builder = new StringBuilder ();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                    String line;
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                }
                catch (IOException e){
                    //nada
                }
                return filtrarBuscaUsuarios(convertJSONToListaUsuarios(new JSONObject(builder.toString())), username);
            }
        }
        catch (Exception e){
            //nada
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    private static Usuario convertJSONToUsuario(JSONObject jsonUusuarios) {
        try {
            Iterator<String> iter = jsonUusuarios.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonUusuario = jsonUusuarios.getJSONObject(key);
                Gson gson = new Gson();
                Usuario usuario = gson.fromJson(jsonUusuario.toString(), Usuario.class);
                return usuario;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertJSONToKeyUsuario(JSONObject jsonUusuarios) {
        Iterator<String> iter = jsonUusuarios.keys();
        while (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    private static List<Usuario> convertJSONToListaUsuarios(JSONObject jsonUsuarios) {
        try {
            List<Usuario> listaUsuarios = new ArrayList<>();
            Iterator<String> iter = jsonUsuarios.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonUsuario = jsonUsuarios.getJSONObject(key);
                Gson gson = new Gson();
                Usuario usuario = gson.fromJson(jsonUsuario.toString(), Usuario.class);
                listaUsuarios.add(usuario);
            }
            return listaUsuarios;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Usuario> filtrarBuscaUsuarios(List<Usuario> usuarios, String filtro) {
        List<Usuario> usuariosFiltrado = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if(usuario.getUsername().contains(filtro)){
                usuariosFiltrado.add(usuario);
            }
        }
        return usuariosFiltrado;
    }

    public static String buscarKeyUsuario(String idGoogle) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Usuarios.json?orderBy=%22idGoogle%22&equalTo=%22" + idGoogle +"%22");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK){
                StringBuilder builder = new StringBuilder ();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                    String line;
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                }
                catch (IOException e){
                    //nada
                }
                return convertJSONToKeyUsuario(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            //nada
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    public static void alterarStatusAtivo(String keyUsuario, boolean status) {
        FirebaseDatabase.getInstance().getReference().child("Usuarios").child(keyUsuario).child("ativo").setValue(status);
    }

    public static void adicionarRoteiroUsuario(String idUsuario, String keyRoteiro) {
        Usuario usuario = consultarUsuario("idGoogle", idUsuario);
        List<String> listaRoteiros = usuario.getRoteiros();
        if(listaRoteiros == null){
            listaRoteiros = new ArrayList<>();
        }
        listaRoteiros.add(keyRoteiro);
        FirebaseDatabase.getInstance().getReference().child("Usuarios").child(buscarKeyUsuario(idUsuario)).child("roteiros").setValue(listaRoteiros);
    }
}
