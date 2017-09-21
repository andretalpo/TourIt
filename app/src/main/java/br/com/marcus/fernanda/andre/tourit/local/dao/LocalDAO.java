package br.com.marcus.fernanda.andre.tourit.local.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.sqlite.DBHelper;

/**
 * Created by André on 20/09/2017.
 */

public class LocalDAO {
    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;

    public LocalDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    public void inserirLocalSQLite(Local local){

        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NOME_LOCAL, local.getNome());
        contentValues.put(DBHelper.COLUMN_ENDERECO_LOCAL, local.getEndereco());
        contentValues.put(DBHelper.COLUMN_IDPLACES_LOCAL, local.getIdPlaces());
        contentValues.put(DBHelper.COLUMN_NOTA_LOCAL, local.getNota());

        sqLiteDatabase.insert(DBHelper.TABLE_LOCAL, null, contentValues);
        sqLiteDatabase.close();
        inserirTipo(local);
    }

    private void inserirTipo(Local local){

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
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_LOCAL + " WHERE " + DBHelper.COLUMN_IDPLACES_LOCAL + " = ?", new String[] {local.getIdPlaces()});
        //Pega o id do local, pelo id do places para inserir no tipo
        if(cursor != null){
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
        if(cursor != null){
            Local local = new Local();
            cursor.moveToFirst();
            local.setNome(cursor.getString(1));
            local.setEndereco(cursor.getString(2));
            local.setIdPlaces(cursor.getString(3));
            local.setNota(cursor.getFloat(4));
            return local;
        }
        sqLiteDatabase.close();
        return null;
    }
}
