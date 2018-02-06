package br.com.marcus.fernanda.andre.tourit.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.model.AvaliacaoLocal;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;

/**
 * Created by André on 13/09/2017.
 */

public class GooglePlacesServices {

    public static List<Local> buscarLocais(String pesquisa){
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&language=pt-BR&query="
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
                    e.printStackTrace();
                }
                return convertJSONToListaLocaisText(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    public static List<Local> buscarLocais(String pesquisa, int distancia, boolean abertoAgora, LatLng latLng){
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&language=pt-BR&keyword="
                    + pesquisa.replaceAll(" ", "") + "&location=" + latLng.latitude + "," + latLng.longitude + "&radius=" + distancia + "&opennow");
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
                    e.printStackTrace();
                }
                return convertJSONToListaLocaisNearby(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    public static List<Local> buscarLocais(String pesquisa, boolean abertoAgora){
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&language=pt-BR&query="
                    + pesquisa.replaceAll(" ", "") + "&opennow");
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
                    e.printStackTrace();
                }
                return convertJSONToListaLocaisText(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    public static List<Local> buscarLocais(String pesquisa, int distancia, LatLng latLng){
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&language=pt-BR&keyword="
                    + pesquisa.replaceAll(" ", "")+ "&location=" + latLng.latitude + "," + latLng.longitude + "&radius=" + distancia);
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
                    e.printStackTrace();
                }
                return convertJSONToListaLocaisNearby(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    private static List<Local> convertJSONToListaLocaisText(JSONObject json) {
        List<Local> listaLocais = new ArrayList<>();
        Local local = null;
        try{
            JSONArray jsonLocais = json.getJSONArray("results");
            for (int i = 0; i < jsonLocais.length(); i++) {
                JSONObject jsonLocal = jsonLocais.getJSONObject(i);
                local = new Local();
                local.setIdPlaces(jsonLocal.getString("place_id"));
                local.setNome(jsonLocal.getString("name"));
                local.setNota(-1);
                local.setNota((float)jsonLocal.getDouble("rating"));
                local.setEndereco(jsonLocal.getString("formatted_address"));

                JSONArray jsonFotos = jsonLocal.getJSONArray("photos");
                JSONObject jsonFoto = jsonFotos.getJSONObject(0);
                local.setPhotoReference(jsonFoto.getString("photo_reference"));
                local.setFoto(buscarFotoLocal(local.getPhotoReference()));

                List<String> tipos = new ArrayList<>();
                JSONArray jsonTipos = jsonLocal.getJSONArray("types");
                ConstantesTipoLocal constantesTipoLocal = new ConstantesTipoLocal();
                for (int j = 0; j < jsonTipos.length(); j++) {
                    String tipo = constantesTipoLocal.getTipos().get(jsonTipos.getString(j));
                    if(tipo != null){
                        tipos.add(tipo);
                    }
                }
                local.setTipo(tipos);

                JSONObject jsonGeometry = jsonLocal.getJSONObject("geometry");
                JSONObject jsonLatLng = jsonGeometry.getJSONObject("location");
                local.setLat(jsonLatLng.getDouble("lat"));
                local.setLng(jsonLatLng.getDouble("lng"));

                JSONObject jsonHorario = jsonLocal.getJSONObject("opening_hours");
                if(jsonHorario != null){
                    JSONArray jsonDiaFuncionamento = jsonHorario.getJSONArray("weekday_text");
                    if(jsonDiaFuncionamento.length() > 0){
                        String horario = "";
                        for(int j = 0; j < jsonDiaFuncionamento.length(); j++){
                            if(j == 0){
                                horario = jsonDiaFuncionamento.getString(j);
                            }else{
                                horario = horario + "\n" + jsonDiaFuncionamento.getString(j);
                            }
                        }
                        local.setHorarioFuncionamento(horario);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(local != null){
                if(local.getNota() > -1) {
                    if(local.getEndereco() != null) {
                        listaLocais.add(local);
                    }
                }
            }
            return listaLocais;
        }
    }

    private static List<Local> convertJSONToListaLocaisNearby(JSONObject json) {
        List<Local> listaLocais = new ArrayList<>();
        Local local = null;
        try{
            JSONArray jsonLocais = json.getJSONArray("results");
            for (int i = 0; i < jsonLocais.length(); i++) {
                JSONObject jsonLocal = jsonLocais.getJSONObject(i);
                local = new Local();
                local.setIdPlaces(jsonLocal.getString("place_id"));
                local.setNome(jsonLocal.getString("name"));
                local.setNota((float)jsonLocal.getDouble("rating"));
                local.setEndereco(jsonLocal.getString("vicinity"));

                JSONArray jsonFotos = jsonLocal.getJSONArray("photos");
                JSONObject jsonFoto = jsonFotos.getJSONObject(0);
                local.setPhotoReference(jsonFoto.getString("photo_reference"));
                local.setFoto(buscarFotoLocal(local.getPhotoReference()));

                List<String> tipos = new ArrayList<>();
                JSONArray jsonTipos = jsonLocal.getJSONArray("types");
                ConstantesTipoLocal constantesTipoLocal = new ConstantesTipoLocal();
                for (int j = 0; j < jsonTipos.length(); j++) {
                    String tipo = constantesTipoLocal.getTipos().get(jsonTipos.getString(j));
                    if(tipo != null){
                        tipos.add(tipo);
                    }
                }
                local.setTipo(tipos);

                JSONObject jsonGeometry = jsonLocal.getJSONObject("geometry");
                JSONObject jsonLatLng = jsonGeometry.getJSONObject("location");
                local.setLat(jsonLatLng.getDouble("lat"));
                local.setLng(jsonLatLng.getDouble("lng"));

                JSONObject jsonHorario = jsonLocal.getJSONObject("opening_hours");
                if(jsonHorario != null){
                    JSONArray jsonDiaFuncionamento = jsonHorario.getJSONArray("weekday_text");
                    String horario = "";
                    for(int j = 0; j < jsonDiaFuncionamento.length(); j++){
                        if(j == 0){
                            horario = jsonDiaFuncionamento.getString(j);
                        }else{
                            horario = horario + "\n" + jsonDiaFuncionamento.getString(j);
                        }
                    }
                    local.setHorarioFuncionamento(horario);
                }
            }
            return listaLocais;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(local != null){
                listaLocais.add(local);
            }
            return listaLocais;
        }
    }

    public static Bitmap buscarFotoLocal(String referencia) {
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&photoreference="
                    + referencia);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK){
                Bitmap imagem = null;
                try {
                    InputStream in = url.openStream();
                    imagem = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return imagem;
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

    public static List<AvaliacaoLocal> buscarAvaliacoesLocal(String idPlaces) {
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&language=pt-BR&placeid=" + idPlaces);
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
                    e.printStackTrace();
                }
                return convertJSONToListaAvaliacoes(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    private static List<AvaliacaoLocal> convertJSONToListaAvaliacoes(JSONObject jsonObject) {
        List<AvaliacaoLocal> listaAvaliacoes = new ArrayList<>();
        try{
            JSONObject jsonLocal = jsonObject.getJSONObject("result");
            JSONArray jsonAvalicoes = jsonLocal.getJSONArray("reviews");
            for (int i=0; i < jsonAvalicoes.length(); i++) {
                JSONObject jsonAvaliacao = jsonAvalicoes.getJSONObject(i);
                AvaliacaoLocal avaliacao = new AvaliacaoLocal();
                avaliacao.setNomeAvaliador(jsonAvaliacao.getString("author_name"));
                avaliacao.setNota((float) jsonAvaliacao.getDouble("rating"));
                avaliacao.setComentario(jsonAvaliacao.getString("text"));
                listaAvaliacoes.add(avaliacao);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return listaAvaliacoes;
    }

    public static Local buscarLocalIdPlaces(String idPlaces) {
        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&language=pt-BR&placeid=" + idPlaces);
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
                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                return convertJSONToLocal(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    private static Local convertJSONToLocal(JSONObject jsonObject) {
        Local local = new Local();
        try {
            JSONObject jsonLocal = jsonObject.getJSONObject("result");
            local.setIdPlaces(jsonLocal.getString("place_id"));
            local.setEndereco(jsonLocal.getString("formatted_address"));
            local.setNota((float)jsonLocal.getDouble("rating"));
            local.setNome(jsonLocal.getString("name"));

            JSONArray jsonFotos = jsonLocal.getJSONArray("photos");
            JSONObject jsonFoto = jsonFotos.getJSONObject(0);
            local.setPhotoReference(jsonFoto.getString("photo_reference"));
            local.setFoto(buscarFotoLocal(local.getPhotoReference()));

            List<String> tipos = new ArrayList<>();
            JSONArray jsonTipos = jsonLocal.getJSONArray("types");
            ConstantesTipoLocal constantesTipoLocal = new ConstantesTipoLocal();
            for (int j = 0; j < jsonTipos.length(); j++) {
                String tipo = constantesTipoLocal.getTipos().get(jsonTipos.getString(j));
                if(tipo != null) {
                    tipos.add(tipo);
                }
            }
            local.setTipo(tipos);

            JSONObject jsonGeometry = jsonLocal.getJSONObject("geometry");
            JSONObject jsonLatLng = jsonGeometry.getJSONObject("location");
            local.setLat(jsonLatLng.getDouble("lat"));
            local.setLng(jsonLatLng.getDouble("lng"));

            JSONObject jsonHorario = jsonLocal.getJSONObject("opening_hours");
            if(jsonHorario != null){
                JSONArray jsonDiaFuncionamento = jsonHorario.getJSONArray("weekday_text");
                String horario = "";
                for(int j = 0; j < jsonDiaFuncionamento.length(); j++){
                    if(j == 0){
                        horario = jsonDiaFuncionamento.getString(j);
                    }else{
                        horario = horario + "\n" + jsonDiaFuncionamento.getString(j);
                    }
                }
                local.setHorarioFuncionamento(horario);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return local;
        }
    }
}
