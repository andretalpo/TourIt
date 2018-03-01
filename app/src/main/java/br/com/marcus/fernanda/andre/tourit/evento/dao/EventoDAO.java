package br.com.marcus.fernanda.andre.tourit.evento.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;
import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;
import br.com.marcus.fernanda.andre.tourit.sqlite.DBHelper;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

/**
 * Created by André on 24/02/2018.
 */

public class EventoDAO {

    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;
    private String idGoogle;

    public EventoDAO(Context context, String idGoogle) {
        dbHelper = new DBHelper(context, idGoogle);
        this.idGoogle = idGoogle;
    }

    public void salvarEventoFirebase(Evento evento) {
        String key = FirebaseDatabase.getInstance().getReference().child("Eventos").push().getKey();
        evento.setIdEventoFirebase(key);
        FirebaseDatabase.getInstance().getReference().child("Eventos").child(key).setValue(evento);
    }

    public void salvarEventoSQLite (Evento evento){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_ID_EVENTO_FIREBASE, evento.getIdEventoFirebase());
        contentValues.put(DBHelper.COLUMN_ID_USUARIO_GOOGLE, evento.getIdCriadorEvento());
        contentValues.put(DBHelper.COLUMN_ID_ROTEIRO_FIREBASE, evento.getIdRoteiroFirebase());
        contentValues.put(DBHelper.COLUMN_NOME_EVENTO, evento.getNomeEvento());
        contentValues.put(DBHelper.COLUMN_NOME_USUARIO, evento.getCriadorEvento());
        contentValues.put(DBHelper.COLUMN_DATA_EVENTO, evento.getDataEvento());
        contentValues.put(DBHelper.COLUMN_HORA_INICIO_EVENTO, evento.getHoraInicio());
        contentValues.put(DBHelper.COLUMN_HORA_FIM_EVENTO, evento.getHoraFim());

        Long id = sqLiteDatabase.insert(DBHelper.TABLE_EVENTO, null, contentValues);

        List<Convite> convites = evento.getConvidados();
        for (Convite convite: convites) {
            ContentValues contentValuesConvite = new ContentValues();
            contentValuesConvite.put(DBHelper.COLUMN_ID_EVENTO, id);
            contentValuesConvite.put(DBHelper.COLUMN_ID_USUARIO_GOOGLE, convite.getIdUsuarioGoogleConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_NOME_USUARIO, convite.getUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_USERNAME, convite.getUsernameUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_RESPOSTA_CONVITE, convite.getRespostaConvite());
            contentValuesConvite.put(DBHelper.COLUMN_FOTO_USUARIO, ImageConverter.convertBitmapToByte(convite.getFotoConvidado()));

            sqLiteDatabase.insert(DBHelper.TABLE_CONVITE, null, contentValuesConvite);
        }
        sqLiteDatabase.close();
    }


    public List<Evento> consultarMeusEventos() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_EVENTO + " WHERE " + DBHelper.COLUMN_ID_USUARIO_GOOGLE + " = ?", new String[]{idGoogle});
        if (cursor != null && cursor.getCount() > 0) {
            List<Evento> listaEventos = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Evento evento = new Evento();
                evento.setNomeEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_EVENTO)));
                evento.setIdRoteiroFirebase(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_ROTEIRO_FIREBASE)));
                evento.setCriadorEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_USUARIO)));
                evento.setHoraFim(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_HORA_FIM_EVENTO)));
                evento.setHoraInicio(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_HORA_INICIO_EVENTO)));
                evento.setIdCriadorEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_USUARIO_GOOGLE)));
                evento.setDataEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATA_EVENTO)));
                evento.setIdEventoFirebase(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO_FIREBASE)));
                evento.setConvidados(consultarConvidados(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO))));
                listaEventos.add(evento);
            }while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
            return listaEventos;
        }
        return null;
    }

        private List<Convite> consultarConvidados(long idEvento) {
            List<Convite> convites = new ArrayList<>();

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_CONVITE + " WHERE " + DBHelper.COLUMN_ID_EVENTO + " = ?", new String[]{String.valueOf(idEvento)});

            if(cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                do{
                    Convite convite = new Convite();
                    convite.setIdUsuarioGoogleConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_USUARIO_GOOGLE)));
                    convite.setUsernameUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME)));
                    convite.setUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_USUARIO)));
                    convite.setRespostaConvite(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_RESPOSTA_CONVITE)));
                    convite.setFotoConvidado(ImageConverter.convertByteToBitmap(cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_FOTO_USUARIO))));
                    convites.add(convite);
                }while (cursor.moveToNext());
                cursor.close();
                return convites;
            }
            return null;
        }
}
