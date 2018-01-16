package br.com.marcus.fernanda.andre.tourit.local.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.sqlite.DBHelper;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

/**
 * Created by André on 20/09/2017.
 */

public class LocalDAO {
    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;

    public LocalDAO(Context context, String idUsuarioGoogle){
        dbHelper = new DBHelper(context, idUsuarioGoogle);
    }

    public void salvarLocaisSQLite(List<Local> locais, Long idRoteiro){

        for (Local local : locais) {
            while(local.getFoto() == null) {
                //gamb
            }

            byte[] fotoByte = ImageConverter.convertBitmapToByte(local.getFoto());
            sqLiteDatabase = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.COLUMN_NOME_LOCAL, local.getNome());
            contentValues.put(DBHelper.COLUMN_ENDERECO_LOCAL, local.getEndereco());
            contentValues.put(DBHelper.COLUMN_IDPLACES_LOCAL, local.getIdPlaces());
            contentValues.put(DBHelper.COLUMN_NOTA_LOCAL, local.getNota());
            contentValues.put(DBHelper.COLUMN_FOTO_LOCAL, fotoByte);
            contentValues.put(DBHelper.COLUMN_ID_ROTEIRO, idRoteiro);
            contentValues.put(DBHelper.COLUMN_LAT_LOCAL, local.getLat());
            contentValues.put(DBHelper.COLUMN_LNG_LOCAL, local.getLng());
            contentValues.put(DBHelper.COLUMN_HORARIO_FUNCIONAMENTO, local.getHorarioFuncionamento());

            int idLocal = (int) sqLiteDatabase.insert(DBHelper.TABLE_LOCAL, null, contentValues);
            sqLiteDatabase.close();
            inserirTipos(local, idLocal);
        }

    }

    private void inserirTipos(Local local, int idLocal){

        sqLiteDatabase = dbHelper.getWritableDatabase();
        for(int i = 0; i < local.getTipo().size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.COLUMN_NOME_TIPO, local.getTipo().get(i));
            contentValues.put(DBHelper.COLUMN_ID_LOCAL, idLocal);
            sqLiteDatabase.insert(DBHelper.TABLE_TIPO, null, contentValues);
        }
        sqLiteDatabase.close();
    }

    private Integer buscarIdLocal(Local local){
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.COLUMN_ID_LOCAL + " FROM " + DBHelper.TABLE_LOCAL + " WHERE " + DBHelper.COLUMN_IDPLACES_LOCAL + " = ?", new String[] {local.getIdPlaces()});
        //Pega o id do local, pelo id do places para inserir no tipo
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        cursor.close();
        sqLiteDatabase.close();
        return null;
    }

    public Local buscarLocal(String idPlaces){
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_LOCAL + " WHERE " + DBHelper.COLUMN_IDPLACES_LOCAL + " = ?", new String[] {idPlaces});
        //Pega o id do local, pelo id do places para inserir no tipo
        if(cursor != null && cursor.getCount() > 0){
            Local local = new Local();
            cursor.moveToFirst();
            local.setNome(cursor.getString(1));
            local.setEndereco(cursor.getString(2));
            local.setIdPlaces(cursor.getString(3));
            local.setNota(cursor.getFloat(4));
            local.setFoto(ImageConverter.convertByteToBitmap(cursor.getBlob(5)));
            local.setLat(cursor.getDouble(6));
            local.setLng(cursor.getDouble(7));
            local.setTipo(buscarTiposLocal(cursor.getInt(0)));
            return local;
        }
        cursor.close();
        sqLiteDatabase.close();
        return null;
    }

    private List<String> buscarTiposLocal(int idLocal) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.COLUMN_NOME_TIPO + " FROM " + DBHelper.TABLE_TIPO + " WHERE " + DBHelper.COLUMN_ID_LOCAL + " = ?", new String[]{String.valueOf(idLocal)});

        List<String> tipos = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                tipos.add(cursor.getString(0));
            }while (cursor.moveToNext());
            return tipos;
        }
        cursor.close();
        return null;
    }

    public void excluirLocais(Long idRoteiro){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(DBHelper.TABLE_LOCAL, DBHelper.COLUMN_ID_ROTEIRO + "=?", new String[] {String.valueOf(idRoteiro)});
    }

    public List<Local> buscarLocaisDoRoteiro(Long idRoteiro) {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_LOCAL + " WHERE " + DBHelper.COLUMN_ID_ROTEIRO + "=?",  new String[] {String.valueOf(idRoteiro)});
        List<Local> locais = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                Local local = new Local();
                local.setNome(cursor.getString(1));
                local.setEndereco(cursor.getString(2));
                local.setIdPlaces(cursor.getString(3));
                local.setNota(cursor.getFloat(4));
                local.setFoto(ImageConverter.convertByteToBitmap(cursor.getBlob(5)));
                local.setLat(cursor.getDouble(6));
                local.setLng(cursor.getDouble(7));
                local.setTipo(buscarTiposLocal(cursor.getInt(0)));
                local.setHorarioFuncionamento(cursor.getString(9));
                locais.add(local);
            }while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
            return locais;
        }
        sqLiteDatabase.close();
        return null;
    }

    public boolean salvarLocalFireBase(Local local) {
        if(consultarLocalFirebase("idPlaces", local.getIdPlaces()) == null) {
            FirebaseDatabase.getInstance().getReference().child("Locais").push().setValue(local);
            return true;
        }
        return false;
    }

    public void salvarLocaisFireBase(List<Local> locais) {
        for (Local local: locais) {
            salvarLocalFireBase(local);
        }
    }

    public static Local consultarLocalFirebase(String parametro, String valor){
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Locais.json?orderBy=%22" +
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

    private static Local convertJSONToLocal(JSONObject jsonLocais) {
        try {
            Iterator<String> iter = jsonLocais.keys();
            if (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonUusuario = jsonLocais.getJSONObject(key);
                Gson gson = new Gson();
                Local local = gson.fromJson(jsonUusuario.toString(), Local.class);
                local.setFoto(GooglePlacesServices.buscarFotoLocal(local.getPhotoReference()));
                return local;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
