package br.com.marcus.fernanda.andre.tourit.roteiro.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.AvaliacaoRoteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.sqlite.DBHelper;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuarioService;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

/**
 * Created by André on 03/10/2017.
 */

public class RoteiroDAO {
    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;
    private String idUsuarioGoogle;

    public RoteiroDAO(Context context, String idUsuarioGoogle){
        this.idUsuarioGoogle = idUsuarioGoogle;
        dbHelper = new DBHelper(context, idUsuarioGoogle);
    }

    public Long salvarRoteiroSqlite(Roteiro roteiro) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NOME_ROTEIRO, roteiro.getNomeRoteiro());
        contentValues.put(DBHelper.COLUMN_CRIADOR_ROTEIRO, roteiro.getCriadorRoteiro());
        contentValues.put(DBHelper.COLUMN_TIPO_ROTEIRO, roteiro.getTipoRoteiro());
        contentValues.put(DBHelper.COLUMN_NOTA_ROTEIRO, roteiro.getNotaRoteiro());
        contentValues.put(DBHelper.COLUMN_IMAGEM_ROTEIRO, ImageConverter.convertBitmapToByte(roteiro.getImagemRoteiro()));
        contentValues.put(DBHelper.COLUMN_ID_ROTEIRO_FIREBASE, roteiro.getIdRoteiroFirebase());
        int publicado;
        if(roteiro.isPublicado()){
            publicado = 1;
        }else{
            publicado = 0;
        }
        contentValues.put(DBHelper.COLUMN_PUBLICADO, publicado);
        int seguido;
        if(roteiro.isSeguido()){
            seguido = 1;
        }else{
            seguido = 0;
        }
        contentValues.put(DBHelper.COLUMN_SEGUIDO, seguido);
        contentValues.put(DBHelper.COLUMN_ROTA, roteiro.getRota());
        contentValues.put(DBHelper.COLUMN_DURACAO, roteiro.getDuracao());
        contentValues.put(DBHelper.COLUMN_PRECO, roteiro.getPreco());
        contentValues.put(DBHelper.COLUMN_DICAS, roteiro.getDicas());

        Long id = sqLiteDatabase.insert(DBHelper.TABLE_ROTEIRO, null, contentValues);
        sqLiteDatabase.close();

        return id;
    }

    public Roteiro consultarRoteiroSqlite(String id){
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_ROTEIRO + " WHERE " + DBHelper.COLUMN_ID_ROTEIRO_FIREBASE + " = ?", new String[] {id});
        if(cursor != null && cursor.getCount() > 0){
            Roteiro roteiro = new Roteiro();
            cursor.moveToFirst();
            roteiro.setIdRoteiroSqlite(cursor.getLong(0));
            roteiro.setNomeRoteiro(cursor.getString(1));
            roteiro.setCriadorRoteiro(cursor.getString(2));
            roteiro.setTipoRoteiro(cursor.getString(3));
            roteiro.setNotaRoteiro(cursor.getFloat(4));
            roteiro.setImagemRoteiro(ImageConverter.convertByteToBitmap(cursor.getBlob(5)));
            roteiro.setIdRoteiroFirebase(cursor.getString(6));

            if(cursor.getInt(7) > 0){
                roteiro.setPublicado(true);
            }else{
                roteiro.setPublicado(false);
            }

            if(cursor.getInt(8) > 0){
                roteiro.setSeguido(true);
            }else{
                roteiro.setSeguido(false);
            }

            roteiro.setRota(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ROTA)));
            roteiro.setDuracao(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_DURACAO)));
            roteiro.setPreco(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_PRECO)));
            roteiro.setDicas(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DICAS)));

            cursor.close();
            sqLiteDatabase.close();
            return roteiro;
        }
        sqLiteDatabase.close();
        return null;
    }

    public Roteiro salvarRoteiroFireBase(Roteiro roteiro) {
        String key = FirebaseDatabase.getInstance().getReference().child("Roteiros").push().getKey();
        roteiro.setIdRoteiroFirebase(key);
        FirebaseDatabase.getInstance().getReference().child("Roteiros").child(key).setValue(roteiro);
        new UsuarioService().adicionarRoteiroUsuario(idUsuarioGoogle, key);

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("imagemRoteiro/" + roteiro.getIdRoteiroFirebase() + ".jpeg").putBytes(ImageConverter.convertBitmapToByte(roteiro.getImagemRoteiro()));

        return roteiro;
    }

    public List<Roteiro> consultarMeusRoteirosSqlite() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_ROTEIRO + " WHERE " + DBHelper.COLUMN_SEGUIDO + "=?", new String[]{"0"});
        if(cursor != null && cursor.getCount() > 0){
            List<Roteiro> listaRoteiros = new ArrayList<>();
            cursor.moveToFirst();
            do{
                Roteiro roteiro = new Roteiro();
                roteiro.setIdRoteiroSqlite(cursor.getLong(0));
                roteiro.setNomeRoteiro(cursor.getString(1));
                roteiro.setCriadorRoteiro(cursor.getString(2));
                roteiro.setTipoRoteiro(cursor.getString(3));
                roteiro.setNotaRoteiro(cursor.getFloat(4));
                roteiro.setImagemRoteiro(ImageConverter.convertByteToBitmap(cursor.getBlob(5)));
                roteiro.setIdRoteiroFirebase(cursor.getString(6));

                if (cursor.getInt(7) > 0){
                    roteiro.setPublicado(true);
                }else{
                    roteiro.setPublicado(false);
                }

                roteiro.setSeguido(false);
                roteiro.setRota(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ROTA)));
                roteiro.setDuracao(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_DURACAO)));
                roteiro.setPreco(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_PRECO)));
                roteiro.setDicas(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DICAS)));
                listaRoteiros.add(roteiro);
            }while(cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
            return listaRoteiros;
        }
        return null;
    }

    public List<Roteiro> consultarRoteirosSeguidos() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_ROTEIRO + " WHERE " + DBHelper.COLUMN_SEGUIDO + "=?", new String[]{"1"});
        if(cursor != null && cursor.getCount() > 0){
            List<Roteiro> listaRoteiros = new ArrayList<>();
            cursor.moveToFirst();
            do{
                Roteiro roteiro = new Roteiro();
                roteiro.setIdRoteiroSqlite(cursor.getLong(0));
                roteiro.setNomeRoteiro(cursor.getString(1));
                roteiro.setCriadorRoteiro(cursor.getString(2));
                roteiro.setTipoRoteiro(cursor.getString(3));
                roteiro.setNotaRoteiro(cursor.getFloat(4));
                roteiro.setImagemRoteiro(ImageConverter.convertByteToBitmap(cursor.getBlob(5)));
                roteiro.setIdRoteiroFirebase(cursor.getString(6));

                if (cursor.getInt(7) > 0){
                    roteiro.setPublicado(true);
                }else{
                    roteiro.setPublicado(false);
                }

                roteiro.setSeguido(true);
                roteiro.setRota(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ROTA)));
                roteiro.setDuracao(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_DURACAO)));
                roteiro.setPreco(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_PRECO)));
                roteiro.setDicas(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DICAS)));
                listaRoteiros.add(roteiro);
            }while(cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
            return listaRoteiros;
        }
        return null;
    }

    public void excluirRoteiroSqlite(Long idRoteiro){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(DBHelper.TABLE_ROTEIRO, DBHelper.COLUMN_ID_ROTEIRO + "=?", new String[] {String.valueOf(idRoteiro)});
    }

    public void excluirRoteiroFirebase(String keyRoteiro){
        FirebaseDatabase.getInstance().getReference().child("Roteiros").child(keyRoteiro).removeValue();
    }

    public String buscarKeyRoteiro(String idRoteiroFirebase) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Roteiros.json?orderBy=%22idRoteiroFirebase%22&equalTo=%22" + idRoteiroFirebase + "%22");
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
                return convertJSONToKeyRoteiro(new JSONObject(builder.toString()));
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

    private static String convertJSONToKeyRoteiro(JSONObject jsonRoteiro) {
        Iterator<String> iter = jsonRoteiro.keys();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public void alterarRoteiroFirebase(Roteiro roteiro) {
        FirebaseDatabase.getInstance().getReference().child("Roteiros").child(roteiro.getIdRoteiroFirebase()).setValue(roteiro);

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("imagemRoteiro/" + roteiro.getIdRoteiroFirebase() + ".jpeg").putBytes(ImageConverter.convertBitmapToByte(roteiro.getImagemRoteiro()));
    }

    public void alterarRoteiroSQLite(Roteiro roteiro) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NOME_ROTEIRO, roteiro.getNomeRoteiro());
        contentValues.put(DBHelper.COLUMN_TIPO_ROTEIRO, roteiro.getTipoRoteiro());
        contentValues.put(DBHelper.COLUMN_NOTA_ROTEIRO, roteiro.getNotaRoteiro());
        contentValues.put(DBHelper.COLUMN_IMAGEM_ROTEIRO, ImageConverter.convertBitmapToByte(roteiro.getImagemRoteiro()));
        int publicado;
        if(roteiro.isPublicado()){
            publicado = 1;
        }else {
            publicado = 0;
        }
        contentValues.put(DBHelper.COLUMN_PUBLICADO, publicado);

        int seguido;
        if(roteiro.isSeguido()){
            seguido = 1;
        }else{
            seguido = 0;
        }
        contentValues.put(DBHelper.COLUMN_SEGUIDO, seguido);
        contentValues.put(DBHelper.COLUMN_ROTA, roteiro.getRota());
        contentValues.put(DBHelper.COLUMN_DURACAO, roteiro.getDuracao());
        contentValues.put(DBHelper.COLUMN_PRECO, roteiro.getPreco());
        contentValues.put(DBHelper.COLUMN_DICAS, roteiro.getDicas());

        sqLiteDatabase.update(DBHelper.TABLE_ROTEIRO, contentValues, DBHelper.COLUMN_ID_ROTEIRO + "=?", new String[]{String.valueOf(roteiro.getIdRoteiroSqlite())});
    }

    public List<Roteiro> buscarRoteirosUsuarioFirebase(List<String> roteirosId) {
        List<Roteiro> roteiros = new ArrayList<>();
        for (String idRoteiro : roteirosId) {
            roteiros.add(buscarRoteiroFirebasePorKey(idRoteiro));
        }
        return roteiros;
    }

    private Roteiro buscarRoteiroFirebasePorKey(String idRoteiro) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Roteiros/" + idRoteiro + ".json");
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
                return convertJSONToRoteiro(new JSONObject(builder.toString()));
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

    private Roteiro convertJSONToRoteiro(JSONObject jsonRoteiro) {
        Gson gson = new Gson();
        return gson.fromJson(jsonRoteiro.toString(), Roteiro.class);
    }

    public void publicarRoteiro(String idRoteiroFirebase) {
        FirebaseDatabase.getInstance().getReference().child("Roteiros").child(idRoteiroFirebase).child("publicado").setValue(true);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_PUBLICADO, 1);
        sqLiteDatabase.update(DBHelper.TABLE_ROTEIRO, contentValues, DBHelper.COLUMN_ID_ROTEIRO_FIREBASE + "=?", new String[]{idRoteiroFirebase});
    }

    public List<Roteiro> consultarRoteirosPublicados(String pesquisa) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Roteiros.json?orderBy=%22publicado%22&equalTo=true");
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
                return filtrarResultados(convertJSONToListRoteiro(new JSONObject(builder.toString())), pesquisa);
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

    private List<Roteiro> convertJSONToListRoteiro(JSONObject jsonRoteiros) {
        try {
            List<Roteiro> listaRoteiros = new ArrayList<>();
            Iterator<String> iter = jsonRoteiros.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonUsuario = jsonRoteiros.getJSONObject(key);
                Gson gson = new Gson();
                Roteiro roteiro = gson.fromJson(jsonUsuario.toString(), Roteiro.class);
                listaRoteiros.add(roteiro);
            }
            return listaRoteiros;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Roteiro> filtrarResultados(List<Roteiro> roteiros, String filtro){
        List<Roteiro> roteirosFiltrado = new ArrayList<>();
        int cont = 0;
        for (Roteiro roteiro : roteiros) {
            if(filtro.equals("todos") && cont < 5){
                roteirosFiltrado.add(roteiro);
                cont++;
            } else if(roteiro.getTipoRoteiro().toLowerCase().trim().contains(filtro.toLowerCase())){
                roteirosFiltrado.add(roteiro);
            } else if(roteiro.getNomeRoteiro().toLowerCase().trim().contains(filtro.toLowerCase())){
                roteirosFiltrado.add(roteiro);
            } else if(roteiro.getCriadorRoteiro().toLowerCase().trim().contains(filtro.toLowerCase())){
                roteirosFiltrado.add(roteiro);
            } else {
                for (String local : roteiro.getNomeLocaisRoteiro()) {
                    if(local.toLowerCase().trim().contains(filtro.toLowerCase().trim())){
                        roteirosFiltrado.add(roteiro);
                    }
                }
            }
        }
        Collections.sort(roteirosFiltrado, Collections.<Roteiro>reverseOrder());
        return roteirosFiltrado;
    }

    public void salvarAvaliacaoRoteiroFirebase(AvaliacaoRoteiro avaliacaoRoteiro) {
        FirebaseDatabase.getInstance().getReference().child("AvaliacoesRoteiro").push().setValue(avaliacaoRoteiro);
    }

    public List<AvaliacaoRoteiro> buscarAvaliacoesRoteiro(String idRoteiro) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/AvaliacoesRoteiro.json?orderBy=%22idRoteiro%22&equalTo=%22" + idRoteiro + "%22");
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
                return convertJSONToListAvaliacoes(new JSONObject(builder.toString()));
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

    private List<AvaliacaoRoteiro> convertJSONToListAvaliacoes(JSONObject jsonAvaliacoes) {
        try {
            List<AvaliacaoRoteiro> listaAvaliacaoRoteiro = new ArrayList<>();
            Iterator<String> iter = jsonAvaliacoes.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonAvaliacao = jsonAvaliacoes.getJSONObject(key);
                Gson gson = new Gson();
                AvaliacaoRoteiro avaliacaoRoteiro = gson.fromJson(jsonAvaliacao.toString(), AvaliacaoRoteiro.class);
                listaAvaliacaoRoteiro.add(avaliacaoRoteiro);
            }
            return listaAvaliacaoRoteiro;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}