package br.com.marcus.fernanda.andre.tourit.roteiro.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

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

        Long id = (Long) sqLiteDatabase.insert(DBHelper.TABLE_ROTEIRO, null, contentValues);
        sqLiteDatabase.close();

        return id;
    }

    public Roteiro consultarRoteiroSqlite(int id){
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_ROTEIRO + " WHERE " + DBHelper.COLUMN_ID_ROTEIRO + " = ?", new String[] {String.valueOf(id)});
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
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_ROTEIRO, null);
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
                listaRoteiros.add(roteiro);
            }while(cursor.moveToNext());

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
        while (iter.hasNext()) {
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
}
