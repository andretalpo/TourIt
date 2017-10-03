package br.com.marcus.fernanda.andre.tourit.roteiro.dao;

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

    public void salvarRoteiro(Roteiro roteiro){
        salvarRoteiroFireBase(roteiro);
        //salvarRoteiroSqlite(roteiro);
    }

    private void salvarRoteiroFireBase(Roteiro roteiro) {
        String key = FirebaseDatabase.getInstance().getReference().child("Roteiros").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("roteiros").child(key).setValue(roteiro);
        new UsuarioService().adicionarRoteiroUsuario(idUsuarioGoogle, key);
    }
}
