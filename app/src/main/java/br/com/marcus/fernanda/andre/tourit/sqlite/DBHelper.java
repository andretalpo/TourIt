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
    public static final String COLUMN_NOTA_LOCAL = "nota";

    public static final String TABLE_TIPO = "tipo";
    public static final String COLUMN_ID_TIPO = "_id_tipo";
    public static final String COLUMN_NOME_TIPO = "nome";

    public DBHelper(Context context, String databaseName){
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase tourItDB) {

        String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s VARCHAR(100), " +
                "%s VARCHAR(300), " +
                "%s VARCHAR(50), " +
                "%s REAL, " +
                "UNIQUE(%s));", TABLE_LOCAL, COLUMN_ID_LOCAL, COLUMN_NOME_LOCAL, COLUMN_ENDERECO_LOCAL, COLUMN_IDPLACES_LOCAL, COLUMN_NOTA_LOCAL, COLUMN_IDPLACES_LOCAL);
        tourItDB.execSQL(sql);

        sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s VARCHAR(30), " +
                "%s INTEGER, " +
                "FOREIGN KEY(%s) REFERENCES local(%s));", TABLE_TIPO, COLUMN_ID_TIPO, COLUMN_NOME_TIPO, COLUMN_ID_LOCAL, COLUMN_ID_LOCAL, COLUMN_ID_LOCAL);
        tourItDB.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase tourItDB, int i, int i1) {

        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO);
        onCreate(tourItDB);

    }
}
