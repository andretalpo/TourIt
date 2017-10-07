package br.com.marcus.fernanda.andre.tourit.roteiro.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.sqlite.DBHelper;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuarioService;

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

    public int salvarRoteiroSqlite(Roteiro roteiro) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NOME_ROTEIRO, roteiro.getNomeRoteiro());
        contentValues.put(DBHelper.COLUMN_CRIADOR_ROTEIRO, roteiro.getCriadorRoteiro());
        contentValues.put(DBHelper.COLUMN_TIPO_ROTEIRO, roteiro.getTipoRoteiro());
        contentValues.put(DBHelper.COLUMN_NOTA_ROTEIRO, roteiro.getNotaRoteiro());

        int id = (int) sqLiteDatabase.insert(DBHelper.TABLE_ROTEIRO, null, contentValues);
        sqLiteDatabase.close();

        return id;
    }

    public Roteiro consultarRoteiroSqlite(int id){
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_ROTEIRO + " WHERE " + DBHelper.COLUMN_ID_ROTEIRO + " = ?", new String[] {String.valueOf(id)});
        if(cursor != null && cursor.getCount() > 0){
            Roteiro roteiro = new Roteiro();
            cursor.moveToFirst();
            roteiro.setIdRoteiro(cursor.getInt(0));
            roteiro.setNomeRoteiro(cursor.getString(1));
            roteiro.setCriadorRoteiro(cursor.getString(2));
            roteiro.setTipoRoteiro(cursor.getString(3));
            roteiro.setNotaRoteiro(cursor.getFloat(4));
            sqLiteDatabase.close();
            return roteiro;
        }
        sqLiteDatabase.close();
        return null;
    }

    public void salvarRoteiroFireBase(Roteiro roteiro) {
        String key = FirebaseDatabase.getInstance().getReference().child("Roteiros").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("roteiros").child(key).setValue(roteiro);
        new UsuarioService().adicionarRoteiroUsuario(idUsuarioGoogle, key);
    }

    public List<Roteiro> consultarMeusRoteirosSqlite() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_ROTEIRO, null);
        if(cursor != null && cursor.getCount() > 0){
            List<Roteiro> listaRoteiros = new ArrayList<>();
            cursor.moveToFirst();
            do{
                Roteiro roteiro = new Roteiro();
                cursor.moveToFirst();
                roteiro.setIdRoteiro(cursor.getInt(0));
                roteiro.setNomeRoteiro(cursor.getString(1));
                roteiro.setCriadorRoteiro(cursor.getString(2));
                roteiro.setTipoRoteiro(cursor.getString(3));
                roteiro.setNotaRoteiro(cursor.getFloat(4));
                listaRoteiros.add(roteiro);
            }while(cursor.moveToNext());

            sqLiteDatabase.close();
            return listaRoteiros;
        }
        return null;
    }
}
