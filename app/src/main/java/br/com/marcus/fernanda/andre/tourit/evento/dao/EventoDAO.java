package br.com.marcus.fernanda.andre.tourit.evento.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;
import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;
import br.com.marcus.fernanda.andre.tourit.sqlite.DBHelper;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

/**
 * Created by André on 24/02/2018.
 */

public class EventoDAO {

    private DBHelper dbHelper;
    private String idGoogle;
    private SQLiteDatabase sqLiteDatabase;

    public EventoDAO(Context context, String idGoogle) {
        dbHelper = new DBHelper(context, idGoogle);
        this.idGoogle = idGoogle;
    }

    public void salvarEventoFirebase(Evento evento) {
        String key = FirebaseDatabase.getInstance().getReference().child("Eventos").push().getKey();
        evento.setIdEventoFirebase(key);
        FirebaseDatabase.getInstance().getReference().child("Eventos").child(key).setValue(evento);
    }

    public String salvarEventoSQLite (Evento evento){
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
            contentValuesConvite.put(DBHelper.COLUMN_ID_EVENTO_FIREBASE, evento.getIdEventoFirebase());
            contentValuesConvite.put(DBHelper.COLUMN_ID_USUARIO_GOOGLE, convite.getIdUsuarioGoogleConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_NOME_USUARIO, convite.getUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_USERNAME, convite.getUsernameUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_RESPOSTA_CONVITE, convite.getRespostaConvite());
            contentValuesConvite.put(DBHelper.COLUMN_EMAIL_CONVIDADO, convite.getEmailUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_FOTO_USUARIO, ImageConverter.convertBitmapToByte(convite.getFotoConvidado()));

            sqLiteDatabase.insert(DBHelper.TABLE_CONVITE, null, contentValuesConvite);
        }
        sqLiteDatabase.close();

        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM convite WHERE id_google = ?", new String[]{idGoogle});
        if (cursor != null && cursor.getCount() > 0) {
            List<Convite> listaConvites = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Convite convite = new Convite();
                convite.setUsernameUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME)));
                listaConvites.add(convite);
            } while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
        }
        return evento.getIdEventoFirebase();
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
                evento.setIdEventoSqlite(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO)));
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

            return excluirEventosVencidos(listaEventos);
        }
        sqLiteDatabase.close();
        return null;
    }

    private List<Convite> consultarConvidados(long idEvento) {
        List<Convite> convites = new ArrayList<>();

        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_CONVITE + " WHERE " + DBHelper.COLUMN_ID_EVENTO + "=?", new String[]{String.valueOf(idEvento)});

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                Convite convite = new Convite();
                convite.setIdUsuarioGoogleConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_USUARIO_GOOGLE)));
                convite.setUsernameUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME)));
                convite.setUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_USUARIO)));
                convite.setRespostaConvite(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_RESPOSTA_CONVITE)));
//                    while(cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_FOTO_USUARIO)) == null){
//                        //gamb
//                    }
                convite.setEmailUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL_CONVIDADO)));
                byte[] foto = cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_FOTO_USUARIO));
                if(foto != null && foto.length > 0) {
                    convite.setFotoConvidado(ImageConverter.convertByteToBitmap(foto));
                }
                convites.add(convite);
            }while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
            return convites;
        }
        sqLiteDatabase.close();
        return null;
    }

    public void salvarImagemConvite(byte[] imagemConvidado, String idUsuarioGoogleConvidado, String idEvento) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_FOTO_USUARIO, imagemConvidado);
        sqLiteDatabase.update(DBHelper.TABLE_CONVITE, contentValues, DBHelper.COLUMN_ID_EVENTO_FIREBASE + "=? AND " + DBHelper.COLUMN_ID_USUARIO_GOOGLE + "=?" , new String[]{idEvento, idUsuarioGoogleConvidado});
        sqLiteDatabase.close();
    }

    public void atualizarEventoSqlite(Evento evento){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NOME_EVENTO, evento.getNomeEvento());
        contentValues.put(DBHelper.COLUMN_DATA_EVENTO, evento.getDataEvento());
        contentValues.put(DBHelper.COLUMN_HORA_INICIO_EVENTO, evento.getHoraInicio());
        contentValues.put(DBHelper.COLUMN_HORA_FIM_EVENTO, evento.getHoraFim());

        sqLiteDatabase.update(DBHelper.TABLE_EVENTO, contentValues, DBHelper.COLUMN_ID_EVENTO_FIREBASE + "=?", new String[] {evento.getIdEventoFirebase()});

        List<Convite> convites = evento.getConvidados();
        sqLiteDatabase.delete(DBHelper.TABLE_CONVITE, DBHelper.COLUMN_ID_EVENTO_FIREBASE + "=?", new String[] {evento.getIdEventoFirebase()});
        for (Convite convite: convites) {
            ContentValues contentValuesConvite = new ContentValues();
            contentValuesConvite.put(DBHelper.COLUMN_ID_EVENTO, evento.getIdEventoSqlite());
            contentValuesConvite.put(DBHelper.COLUMN_ID_EVENTO_FIREBASE, evento.getIdEventoFirebase());
            contentValuesConvite.put(DBHelper.COLUMN_ID_USUARIO_GOOGLE, convite.getIdUsuarioGoogleConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_NOME_USUARIO, convite.getUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_USERNAME, convite.getUsernameUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_RESPOSTA_CONVITE, convite.getRespostaConvite());
            contentValuesConvite.put(DBHelper.COLUMN_EMAIL_CONVIDADO, convite.getEmailUsuarioConvidado());
            contentValuesConvite.put(DBHelper.COLUMN_FOTO_USUARIO, ImageConverter.convertBitmapToByte(convite.getFotoConvidado()));

            sqLiteDatabase.insert(DBHelper.TABLE_CONVITE, null, contentValuesConvite);
        }
        sqLiteDatabase.close();
    }

    public void atualizarEventoFirebase(Evento evento){
        FirebaseDatabase.getInstance().getReference().child("Eventos").child(evento.getIdEventoFirebase()).setValue(evento);
    }

    public void excluirEventoSqlite(String idEvento){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(DBHelper.TABLE_EVENTO, DBHelper.COLUMN_ID_EVENTO_FIREBASE + "=?", new String[] {idEvento});
        sqLiteDatabase.close();
    }

    public void excluirEventoFirebase(String idEvento){
        FirebaseDatabase.getInstance().getReference().child("Eventos").child(idEvento).removeValue();
    }

    public Evento consultarEventoPorIdFirebase(String idEvento) {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_EVENTO + " WHERE " + DBHelper.COLUMN_ID_EVENTO_FIREBASE + " = ?", new String[]{idEvento});
        Evento evento = new Evento();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            evento.setNomeEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_EVENTO)));
            evento.setIdEventoSqlite(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO)));
            evento.setIdRoteiroFirebase(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_ROTEIRO_FIREBASE)));
            evento.setCriadorEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_USUARIO)));
            evento.setHoraFim(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_HORA_FIM_EVENTO)));
            evento.setHoraInicio(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_HORA_INICIO_EVENTO)));
            evento.setIdCriadorEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_USUARIO_GOOGLE)));
            evento.setDataEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATA_EVENTO)));
            evento.setIdEventoFirebase(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO_FIREBASE)));
            Long idEventoSqlite = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO));
            evento.setConvidados(consultarConvidados(idEventoSqlite));
        }
        cursor.close();
        sqLiteDatabase.close();
        return evento;
    }

    public List<Convite> consultarConvitesEvento(String idEvento) {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        List<Convite> convites = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_CONVITE + " WHERE " + DBHelper.COLUMN_ID_EVENTO_FIREBASE + " = ?", new String[]{String.valueOf(idEvento)});

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                Convite convite = new Convite();
                convite.setIdUsuarioGoogleConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_USUARIO_GOOGLE)));
                convite.setUsernameUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME)));
                convite.setUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_USUARIO)));
                convite.setRespostaConvite(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_RESPOSTA_CONVITE)));
//                while(cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_FOTO_USUARIO)) == null){
//                    //gamb
//                }
                convite.setEmailUsuarioConvidado(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL_CONVIDADO)));
                convite.setFotoConvidado(ImageConverter.convertByteToBitmap(cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_FOTO_USUARIO))));
                convites.add(convite);
            }while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
            return convites;
        }
        sqLiteDatabase.close();
        return null;
    }

    public List<Evento> consultarEventosConvidado(String idUsuarioGoogle) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Eventos.json?");
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
                //Fazer filtro de data para excluir eventos encerrados
                List<Evento> listaEventos = filtrarEventosConvidado(convertJSONToListEventos(new JSONObject(builder.toString())));

                excluirEventosVencidos(listaEventos);

                return listaEventos;
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

    private List<Evento> excluirEventosVencidos(List<Evento> listaEventos) {
        for(Evento evento: listaEventos){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            try {
                calendar.setTime(dateFormat.parse(evento.getDataEvento() + " " + evento.getHoraFim()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(calendar.getTime().before(Calendar.getInstance().getTime())){
                excluirEventoSqlite(evento.getIdEventoFirebase());
                excluirEventoFirebase(evento.getIdEventoFirebase());
                listaEventos.remove(evento);
            }
        }
        return listaEventos;
    }

    private List<Evento> filtrarEventosConvidado(List<Evento> eventos) {
        List<Evento> eventosConvidado = new ArrayList<>();
        for (Evento evento: eventos) {
            for(Convite convite: evento.getConvidados()){
                if(convite.getIdUsuarioGoogleConvidado().equals(idGoogle)){
                    eventosConvidado.add(evento);
                }
            }
        }
        return eventosConvidado;
    }

    private List<Evento> convertJSONToListEventos(JSONObject jsonEventos) {
        try {
            List<Evento> listaEventos = new ArrayList<>();
            Iterator<String> iter = jsonEventos.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject jsonEvento = jsonEventos.getJSONObject(key);
                Gson gson = new Gson();
                Evento evento = gson.fromJson(jsonEvento.toString(), Evento.class);
                listaEventos.add(evento);
            }
            return listaEventos;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void excluirConvitesSQLite(String idGoogle) {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT c." + DBHelper.COLUMN_ID_EVENTO_FIREBASE +
                " FROM " + DBHelper.TABLE_EVENTO + " e JOIN " + DBHelper.TABLE_CONVITE + " c ON (e." + DBHelper.COLUMN_ID_EVENTO +
                        "=c." + DBHelper.COLUMN_ID_EVENTO + ") WHERE c." + DBHelper.COLUMN_ID_USUARIO_GOOGLE + "=?", new String[]{idGoogle});
        List<String> listaEventos = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listaEventos.add(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO_FIREBASE)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        sqLiteDatabase = dbHelper.getWritableDatabase();
        for(String idEvento: listaEventos) {
            sqLiteDatabase.delete(DBHelper.TABLE_EVENTO, DBHelper.COLUMN_ID_EVENTO_FIREBASE + "=?", new String[]{idEvento});
            sqLiteDatabase.delete(DBHelper.TABLE_CONVITE, DBHelper.COLUMN_ID_EVENTO_FIREBASE + "=?", new String[]{idEvento});
        }
        sqLiteDatabase.close();
    }

    public List<Evento> consultarEventosConvidadoSqlite() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        List<Evento> eventos = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_EVENTO + " e JOIN " + DBHelper.TABLE_CONVITE +
                " c ON(e." + DBHelper.COLUMN_ID_EVENTO + "=c." + DBHelper.COLUMN_ID_EVENTO + ")" +
                " WHERE c." + DBHelper.COLUMN_ID_USUARIO_GOOGLE + " = ?", new String[]{idGoogle});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Evento evento = new Evento();
                evento.setNomeEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_EVENTO)));
                evento.setIdEventoSqlite(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO)));
                evento.setIdRoteiroFirebase(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_ROTEIRO_FIREBASE)));
                evento.setCriadorEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME_USUARIO)));
                evento.setHoraFim(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_HORA_FIM_EVENTO)));
                evento.setHoraInicio(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_HORA_INICIO_EVENTO)));
                evento.setIdCriadorEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_USUARIO_GOOGLE)));
                evento.setDataEvento(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATA_EVENTO)));
                evento.setIdEventoFirebase(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO_FIREBASE)));
                Long idEventoSqlite = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID_EVENTO));
                evento.setConvidados(consultarConvidados(idEventoSqlite));
                eventos.add(evento);

            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return eventos;
    }

    public void responderConviteFirebase(String idEvento, List<Convite> convites) {
        FirebaseDatabase.getInstance().getReference().child("Eventos").child(idEvento).child("convidados").setValue(convites);
    }

    public List<Convite> consultarConvitesEventoFirebase(String idEvento) {
        URL url = null;
        try {
            url = new URL("https://tourit-176321.firebaseio.com/Eventos/" + idEvento + "/convidados.json?");
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
                return convertJSONToListConvites(new JSONArray(builder.toString()));
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

    private List<Convite> convertJSONToListConvites(JSONArray jsonConvites) {
        List<Convite> listaConvites = new ArrayList<>();
        for(int i = 0; i < jsonConvites.length(); i++){
            try {
                JSONObject jsonConvite = jsonConvites.getJSONObject(i);
                Gson gson = new Gson();
                Convite convite = gson.fromJson(jsonConvite.toString(), Convite.class);
                listaConvites.add(convite);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listaConvites;
    }

    public void atualizarConviteSqlite(String idEvento, Convite convite) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_RESPOSTA_CONVITE, convite.getRespostaConvite());
        sqLiteDatabase.update(DBHelper.TABLE_CONVITE, contentValues, DBHelper.COLUMN_ID_EVENTO_FIREBASE + "=? AND " + DBHelper.COLUMN_ID_USUARIO_GOOGLE + "=?", new String[]{idEvento, convite.getIdUsuarioGoogleConvidado()});
        sqLiteDatabase.close();
    }
}