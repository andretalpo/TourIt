package br.com.marcus.fernanda.andre.tourit.usuario.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

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
import java.util.Iterator;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.sqlite.DBHelper;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

/**
 * Created by André on 03/09/2017.
 */

public class UsuarioDAO {

    private Context context;
    private String idUsuarioGoogle;
    private DBHelper dbHelper;

    public UsuarioDAO(Context context, String idUsuarioGoogle){
        this.context = context;
        this.dbHelper = new DBHelper(context, idUsuarioGoogle);
    }

    public static void salvarUsuario(Usuario usuario, byte[] imagemBytes) {
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("imagemUsuario/" + usuario.getIdGoogle() + ".jpeg").putBytes(imagemBytes);
        FirebaseDatabase.getInstance().getReference().child("Usuarios").push().setValue(usuario);
    }

    public void salvarUsuarioSqlite(Usuario usuario) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_ID_USUARIO_GOOGLE, usuario.getIdGoogle());
        contentValues.put(DBHelper.COLUMN_NOME_USUARIO, usuario.getNomeUsuario());
        contentValues.put(DBHelper.COLUMN_USERNAME, usuario.getUsername());
        contentValues.put(DBHelper.COLUMN_FOTO_USUARIO, ImageConverter.convertBitmapToByte(usuario.getFotoUsuario()));

        sqLiteDatabase.insert(DBHelper.TABLE_USUARIO, null, contentValues);
        sqLiteDatabase.close();
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
                    e.printStackTrace();
                }
                return convertJSONToUsuario(new JSONObject(builder.toString()));
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

    private static Usuario convertJSONToUsuario(JSONObject jsonUsuarios) {
        try {
            Iterator<String> iter = jsonUsuarios.keys();
            if (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonUsuario = jsonUsuarios.getJSONObject(key);
                Gson gson = new Gson();
                Usuario usuario = gson.fromJson(jsonUsuario.toString(), Usuario.class);
//                Usuario usuario = new Usuario();
//                usuario.setIdGoogle(jsonUsuario.getString("idGoogle"));
//                usuario.setUsername(jsonUsuario.getString("username"));
//                usuario.setNomeUsuario(jsonUsuario.getString("nomeUsuario"));
//                usuario.setEmailUsuario(jsonUsuario.getString("emailUsuario"));
//                usuario.setAdmnistrador(jsonUsuario.getBoolean("administrador"));
//                usuario.setAtivo(jsonUsuario.getBoolean("ativo"));
//                if(jsonUsuario.getJSONObject("meusRoteiros") != null){
//                    Iterator<String> iterM = jsonUsuario.getJSONObject("meusRoteiros").keys();
//                    if (iter.hasNext()) {
//                        String keyM = iter.next();
//                        JSONObject jsonStringRoteiro = jsonUsuario.ge
//                        usuario.getMeusRoteiros().add();
//                    }
//                }
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
            if(usuario.getUsername().contains(filtro.toLowerCase())){
                usuariosFiltrado.add(usuario);
            }else if(usuario.getNomeUsuario().toLowerCase().contains(filtro.toLowerCase())){
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
                    e.printStackTrace();
                }
                return convertJSONToKeyUsuario(new JSONObject(builder.toString()));
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

    public static void alterarStatusAtivo(String keyUsuario, boolean status) {
        FirebaseDatabase.getInstance().getReference().child("Usuarios").child(keyUsuario).child("ativo").setValue(status);
    }

    public static void adicionarRoteiroUsuario(String idUsuario, String keyRoteiro) {
        Usuario usuario = consultarUsuario("idGoogle", idUsuario);
        if(usuario != null) {
            List<String> listaRoteiros = usuario.getMeusRoteiros();

            if (listaRoteiros == null) {
                listaRoteiros = new ArrayList<>();
            }
            if(!listaRoteiros.contains(keyRoteiro)) {
                listaRoteiros.add(keyRoteiro);
                FirebaseDatabase.getInstance().getReference().child("Usuarios").child(buscarKeyUsuario(idUsuario)).child("meusRoteiros").setValue(listaRoteiros);
            }
        }
    }

    public static void adicionarRoteiroSeguidoUsuario(String idUsuario, String keyRoteiro) {
        Usuario usuario = consultarUsuario("idGoogle", idUsuario);
        if(usuario != null) {
            List<String> listaRoteiros = usuario.getRoteirosSeguidos();
            if (listaRoteiros == null) {
                listaRoteiros = new ArrayList<>();
            }
            listaRoteiros.add(keyRoteiro);
            FirebaseDatabase.getInstance().getReference().child("Usuarios").child(buscarKeyUsuario(idUsuario)).child("roteirosSeguidos").setValue(listaRoteiros);
        }
    }

    public static void excluirRoteiroUsuario(String keyRoteiro){
        Usuario usuario = consultarUsuario("idGoogle", MainActivity.idUsuarioGoogle);
        if(usuario != null) {
            List<String> listaRoteiros = usuario.getMeusRoteiros();
            for (int i = 0; i < listaRoteiros.size(); i++) {
                if (listaRoteiros.get(i).equals(keyRoteiro)) {
                    listaRoteiros.remove(i);
                }
                FirebaseDatabase.getInstance().getReference().child("Usuarios").child(buscarKeyUsuario(MainActivity.idUsuarioGoogle)).child("meusRoteiros").setValue(listaRoteiros);
            }
        }
    }

    public static void excluirRoteiroSeguidoUsuario(String idRoteiroFirebase) {
        Usuario usuario = consultarUsuario("idGoogle", MainActivity.idUsuarioGoogle);
        if(usuario != null) {
            List<String> listaRoteiros = usuario.getRoteirosSeguidos();
            if(listaRoteiros != null) {
                for (int i = 0; i < listaRoteiros.size(); i++) {
                    if (listaRoteiros.get(i).equals(idRoteiroFirebase)) {
                        listaRoteiros.remove(i);
                    }
                    FirebaseDatabase.getInstance().getReference().child("Usuarios").child(buscarKeyUsuario(MainActivity.idUsuarioGoogle)).child("roteirosSeguidos").setValue(listaRoteiros);
                }
            }
        }
    }

    public static List<String> buscarRoteirosUsuarioFirebase(String idUsuarioGoogle) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Usuarios.json?orderBy=%22idGoogle%22&equalTo=%22" + idUsuarioGoogle +"%22");
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
                return convertJSONToListRoteiro(new JSONObject(builder.toString()));
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

    public static List<String> buscarRoteirosSeguidosUsuarioFirebase(String idUsuarioGoogle) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Usuarios.json?orderBy=%22idGoogle%22&equalTo=%22" + idUsuarioGoogle +"%22");
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
                return convertJSONToListRoteiroSeguido(new JSONObject(builder.toString()));
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

    private static List<String> convertJSONToListRoteiroSeguido(JSONObject jsonUsuarios) {
        try {
            List<String> listaRoteiros = new ArrayList<>();
            Iterator<String> iter = jsonUsuarios.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonUsuario = jsonUsuarios.getJSONObject(key);
                JSONArray jsonRoteiros = jsonUsuario.getJSONArray("roteirosSeguidos");
                for (int i = 0; i < jsonRoteiros.length(); i++){
                    listaRoteiros.add(jsonRoteiros.getString(i));
                }
            }
            return listaRoteiros;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> convertJSONToListRoteiro(JSONObject jsonUsuarios) {
        try {
            List<String> listaRoteiros = new ArrayList<>();
            Iterator<String> iter = jsonUsuarios.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonUsuario = jsonUsuarios.getJSONObject(key);
                JSONArray jsonRoteiros = jsonUsuario.getJSONArray("meusRoteiros");
                for (int i = 0; i < jsonRoteiros.length(); i++){
                    listaRoteiros.add(jsonRoteiros.getString(i));
                }
            }
            return listaRoteiros;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void adicionarFotoUsuarioSqlite(String idGoogle, Bitmap bitmap) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_FOTO_USUARIO, ImageConverter.convertBitmapToByte(bitmap));
        sqLiteDatabase.update(DBHelper.TABLE_USUARIO, contentValues, DBHelper.COLUMN_ID_USUARIO_GOOGLE + "=?", new String[]{String.valueOf(idGoogle)});
    }

    public Usuario consultarUsuarioSqlite(String idUsuarioGoogle) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_USUARIO + " WHERE " + DBHelper.COLUMN_ID_USUARIO_GOOGLE + " = ?", new String[] {idUsuarioGoogle});
        if(cursor != null && cursor.getCount() > 0){
            Usuario usuario = new Usuario();
            cursor.moveToFirst();
            usuario.setIdGoogle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_USUARIO_GOOGLE)));
            usuario.setNomeUsuario(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_USUARIO)));
            usuario.setUsername(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME)));

            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_FOTO_USUARIO));
            if(bytes != null) {
                usuario.setFotoUsuario(ImageConverter.convertByteToBitmap(bytes));
            }

            cursor.close();
            sqLiteDatabase.close();
            return usuario;
        }
        sqLiteDatabase.close();
        return null;
    }

    public static List<Usuario> buscarUsuarios(String pesquisa) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Usuarios.json?");
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
                return filtrarBuscaUsuarios(convertJSONToListaUsuarios(new JSONObject(builder.toString())), pesquisa);
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
}