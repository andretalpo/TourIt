package br.com.marcus.fernanda.andre.tourit.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by André on 20/09/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LOCAL = "local";
    public static final String COLUMN_ID_LOCAL = "_id_local";
    public static final String COLUMN_NOME_LOCAL = "nome";
    public static final String COLUMN_ENDERECO_LOCAL = "endereco";
    public static final String COLUMN_IDPLACES_LOCAL = "idplaces";
    public static final String COLUMN_LAT_LOCAL = "lat";
    public static final String COLUMN_LNG_LOCAL = "lng";
    //colocar horário de funcionamento no futuro
    public static final String COLUMN_NOTA_LOCAL = "nota";
    public static final String COLUMN_FOTO_LOCAL = "foto";

    public static final String TABLE_TIPO = "tipo";
    public static final String COLUMN_ID_TIPO = "_id_tipo";
    public static final String COLUMN_NOME_TIPO = "nome";

    public static final String TABLE_ROTEIRO = "roteiro";
    public static final String COLUMN_ID_ROTEIRO = "_id_roteiro";
    public static final String COLUMN_NOME_ROTEIRO = "nome";
    public static final String COLUMN_CRIADOR_ROTEIRO = "criador";
    public static final String COLUMN_TIPO_ROTEIRO = "tipo";
    public static final String COLUMN_NOTA_ROTEIRO = "nota";
    public static final String COLUMN_IMAGEM_ROTEIRO = "imagem";
    public static final String COLUMN_PUBLICADO = "publicado";
    public static final String COLUMN_SEGUIDO = "seguido";
    public static final String COLUMN_ID_ROTEIRO_FIREBASE = "id_roteiro_firebase";

    public static final String TABLE_USUARIO = "usuario";
    public static final String COLUMN_ID_USUARIO_SQLITE = "_id_usuario";
    public static final String COLUMN_ID_USUARIO_GOOGLE = "id_google";
    public static final String COLUMN_NOME_USUARIO = "nome";
    public static final String COLUMN_FOTO_USUARIO = "foto";
    public static final String COLUMN_USERNAME = "username";

    public DBHelper(Context context, String databaseName){
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase tourItDB) {

        String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s VARCHAR(100), " +
                "%s VARCHAR(100), " +
                "%s VARCHAR(20), " +
                "%s REAL, " +
                "%s BLOB, " +
                "%s VARCHAR(50), " +
                "%s INTEGER, " +
                "%s INTEGER);", TABLE_ROTEIRO, COLUMN_ID_ROTEIRO, COLUMN_NOME_ROTEIRO, COLUMN_CRIADOR_ROTEIRO, COLUMN_TIPO_ROTEIRO, COLUMN_NOTA_ROTEIRO, COLUMN_IMAGEM_ROTEIRO, COLUMN_ID_ROTEIRO_FIREBASE, COLUMN_PUBLICADO, COLUMN_SEGUIDO);
        tourItDB.execSQL(sql);

        sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s VARCHAR(100), " +
                "%s VARCHAR(300), " +
                "%s VARCHAR(50), " +
                "%s REAL, " +
                "%s BLOB, " +
                "%s REAL, " +
                "%s REAL, " +
                "%s INTEGER, " +
                "FOREIGN KEY(%s) REFERENCES roteiro(%s) ON DELETE CASCADE);", TABLE_LOCAL, COLUMN_ID_LOCAL, COLUMN_NOME_LOCAL, COLUMN_ENDERECO_LOCAL, COLUMN_IDPLACES_LOCAL, COLUMN_NOTA_LOCAL, COLUMN_FOTO_LOCAL, COLUMN_LAT_LOCAL, COLUMN_LNG_LOCAL, COLUMN_ID_ROTEIRO, COLUMN_ID_ROTEIRO, COLUMN_ID_ROTEIRO);
        tourItDB.execSQL(sql);

        sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s VARCHAR(30), " +
                "%s INTEGER, " +
                "FOREIGN KEY(%s) REFERENCES local(%s) ON DELETE CASCADE);", TABLE_TIPO, COLUMN_ID_TIPO, COLUMN_NOME_TIPO, COLUMN_ID_LOCAL, COLUMN_ID_LOCAL, COLUMN_ID_LOCAL);
        tourItDB.execSQL(sql);

        sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s VARCHAR(100), " +
                "%s VARCHAR(30), " +
                "%s VARCHAR(30), " +
                "%s BLOB);", TABLE_USUARIO, COLUMN_ID_USUARIO_SQLITE, COLUMN_ID_USUARIO_GOOGLE, COLUMN_NOME_USUARIO, COLUMN_USERNAME, COLUMN_FOTO_USUARIO);
        tourItDB.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase tourItDB, int i, int i1) {

        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_ROTEIRO);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(tourItDB);

    }
}
