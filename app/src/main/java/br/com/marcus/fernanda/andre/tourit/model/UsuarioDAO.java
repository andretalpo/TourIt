package br.com.marcus.fernanda.andre.tourit.model;

import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by André on 03/09/2017.
 */

public class UsuarioDAO {

    public static void salvarUsuario(Usuario usuario) {
        FirebaseDatabase.getInstance().getReference().child("Usuarios").push().setValue(usuario);
    }

    public static Usuario consultarUsuarioUsername(String username){
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Usuarios.json?orderBy=%22username%22&equalTo=%22" + username +"%22");
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

    private static Usuario convertJSONToUsuario(JSONObject usuarios) {
        try {
            Iterator<String> iter = usuarios.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject usuario = usuarios.getJSONObject(key);//JSONArray list = usuarios.getJSONObject(key);
                Gson gson = new Gson();
                Usuario usuariofinal = gson.fromJson(usuario.toString(), Usuario.class);
                return usuariofinal;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
