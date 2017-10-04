package br.com.marcus.fernanda.andre.tourit.roteiro.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.FirebaseDatabase;

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

    public void salvarRoteiroFireBase(Roteiro roteiro) {
        String key = FirebaseDatabase.getInstance().getReference().child("Roteiros").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("roteiros").child(key).setValue(roteiro);
        new UsuarioService().adicionarRoteiroUsuario(idUsuarioGoogle, key);
    }
}
