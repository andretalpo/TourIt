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
    public static final String COLUMN_HORARIO_FUNCIONAMENTO = "hrfuncionamento";
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
    public static final String COLUMN_ROTA = "rota";
    public static final String COLUMN_DURACAO = "duracao";
    public static final String COLUMN_PRECO = "preco";
    public static final String COLUMN_DICAS = "dicas";

    public static final String TABLE_USUARIO = "usuario";
    public static final String COLUMN_ID_USUARIO_SQLITE = "_id_usuario";
    public static final String COLUMN_ID_USUARIO_GOOGLE = "id_google";
    public static final String COLUMN_NOME_USUARIO = "nome";
    public static final String COLUMN_FOTO_USUARIO = "foto";
    public static final String COLUMN_USERNAME = "username";

    public static final String TABLE_EVENTO = "evento";
    public static final String COLUMN_ID_EVENTO = "_id_evento";
    public static final String COLUMN_NOME_EVENTO = "nomeEvento";
    public static final String COLUMN_DATA_EVENTO = "data";
    public static final String COLUMN_ID_EVENTO_FIREBASE = "id_evento_firebase";
    public static final String COLUMN_HORA_INICIO_EVENTO = "hora_inicio";
    public static final String COLUMN_HORA_FIM_EVENTO = "hora_fim";

    public static final String TABLE_CONVITE = "convite";
    public static final String COLUMN_ID_CONVITE = "_id_convite";
    public static final String COLUMN_RESPOSTA_CONVITE = "resposta";

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
                "%s INTEGER, " +
                "%s VARCHAR, " +
                "%s INTEGER, " +
                "%s INTEGER, " +
                "%s VARCHAR(500));", TABLE_ROTEIRO, COLUMN_ID_ROTEIRO, COLUMN_NOME_ROTEIRO, COLUMN_CRIADOR_ROTEIRO, COLUMN_TIPO_ROTEIRO, COLUMN_NOTA_ROTEIRO, COLUMN_IMAGEM_ROTEIRO, COLUMN_ID_ROTEIRO_FIREBASE, COLUMN_PUBLICADO, COLUMN_SEGUIDO, COLUMN_ROTA, COLUMN_DURACAO, COLUMN_PRECO, COLUMN_DICAS);
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
                "%s VARCHAR, " +
                "FOREIGN KEY(%s) REFERENCES roteiro(%s) ON DELETE CASCADE);", TABLE_LOCAL, COLUMN_ID_LOCAL, COLUMN_NOME_LOCAL, COLUMN_ENDERECO_LOCAL, COLUMN_IDPLACES_LOCAL, COLUMN_NOTA_LOCAL, COLUMN_FOTO_LOCAL, COLUMN_LAT_LOCAL, COLUMN_LNG_LOCAL, COLUMN_ID_ROTEIRO, COLUMN_HORARIO_FUNCIONAMENTO, COLUMN_ID_ROTEIRO, COLUMN_ID_ROTEIRO);
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

        sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s VARCHAR, " + //id evento firebase
                "%s VARCHAR, " + //id usuario google
                "%s VARCHAR, " + //id roteiro firebase
                "%s VARCHAR(30), " + //nome evento
                "%s VARCHAR, " +  //nome criador
                "%s VARCHAR(10), " + //data
                "%s VARCHAR(10), " + // hora inicio a de baixo é fim
                "%s VARCHAR(10));", TABLE_EVENTO, COLUMN_ID_EVENTO, COLUMN_ID_EVENTO_FIREBASE, COLUMN_ID_USUARIO_GOOGLE, COLUMN_ID_ROTEIRO_FIREBASE, COLUMN_NOME_EVENTO, COLUMN_NOME_USUARIO, COLUMN_DATA_EVENTO, COLUMN_HORA_INICIO_EVENTO, COLUMN_HORA_FIM_EVENTO);
        tourItDB.execSQL(sql);

        sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, " +
                "%s INTEGER, " +//id evento
                "%s VARCHAR, " +//id google
                "%s VARCHAR, " +//nome usuario
                "%s VARCHAR, " +//username
                "%s INTEGER, " +// resposta
                "%s BLOB, " + // foto
                "FOREIGN KEY(%s) REFERENCES evento(%s) ON DELETE CASCADE);", TABLE_CONVITE, COLUMN_ID_CONVITE, COLUMN_ID_EVENTO, COLUMN_ID_USUARIO_GOOGLE, COLUMN_NOME_USUARIO, COLUMN_USERNAME, COLUMN_RESPOSTA_CONVITE, COLUMN_FOTO_USUARIO, COLUMN_ID_EVENTO, COLUMN_ID_EVENTO);
        tourItDB.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase tourItDB, int i, int i1) {

        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_ROTEIRO);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTO);
        tourItDB.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVITE);
        onCreate(tourItDB);

    }
}
