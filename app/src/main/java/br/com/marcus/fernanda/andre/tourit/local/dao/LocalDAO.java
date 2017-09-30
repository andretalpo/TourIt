package br.com.marcus.fernanda.andre.tourit.local.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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

    public void inserirLocalSQLite(Local local){

        byte[] fotoByte = ImageConverter.convertBitmapToByte(local.getFoto());

        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NOME_LOCAL, local.getNome());
        contentValues.put(DBHelper.COLUMN_ENDERECO_LOCAL, local.getEndereco());
        contentValues.put(DBHelper.COLUMN_IDPLACES_LOCAL, local.getIdPlaces());
        contentValues.put(DBHelper.COLUMN_NOTA_LOCAL, local.getNota());
        contentValues.put(DBHelper.COLUMN_FOTO_LOCAL, fotoByte);

        sqLiteDatabase.insert(DBHelper.TABLE_LOCAL, null, contentValues);
        sqLiteDatabase.close();
        inserirTipos(local);
    }

    private void inserirTipos(Local local){

        Integer idLocal = buscarIdLocal(local);

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
            local.setTipo(buscarTiposLocal(cursor.getInt(0)));
            return local;
        }
        sqLiteDatabase.close();
        return null;
    }

    public List<Local> buscarLocaisUsuario() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_LOCAL, null);
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
                local.setTipo(buscarTiposLocal(cursor.getInt(0)));
                locais.add(local);
            }while (cursor.moveToNext());

            sqLiteDatabase.close();
            return locais;
        }
        sqLiteDatabase.close();
        return null;
    }

    private List<String> buscarTiposLocal(int idLocal) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.COLUMN_NOME_TIPO + " FROM " + DBHelper.TABLE_TIPO + " WHERE " + DBHelper.COLUMN_ID_LOCAL + " = ?", new String[]{String.valueOf(idLocal)});

        List<String> tipos = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            while (cursor.moveToNext()){
                tipos.add(cursor.getString(0));
            }
            return tipos;
        }
        return null;
    }

    public void deleteLocal(String idGooglePlaces){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(DBHelper.TABLE_LOCAL, DBHelper.COLUMN_IDPLACES_LOCAL + "=?", new String[] {idGooglePlaces});
    }
}
