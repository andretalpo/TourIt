package br.com.marcus.fernanda.andre.tourit.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.model.Local;

/**
 * Created by André on 13/09/2017.
 */

public class GooglePlacesServices {

    public static List<Local> buscarLocais(String pesquisa){
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&query="
                    + pesquisa.replaceAll(" ", ""));
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
                return convertJSONToListaLocais(new JSONObject(builder.toString()));
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

    private static List<Local> convertJSONToListaLocais(JSONObject json) {
        try{
            List<Local> listaLocais = new ArrayList<>();
            JSONArray jsonLocais = json.getJSONArray("results");
            for (int i = 0; i < jsonLocais.length(); i++) {
                JSONObject jsonLocal = jsonLocais.getJSONObject(i);
                Local local = new Local();
                local.setIdPlaces(jsonLocal.getString("place_id"));
                local.setNome(jsonLocal.getString("name"));
                local.setNota((float)jsonLocal.getDouble("rating"));
                listaLocais.add(local);
            }
            return listaLocais;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
