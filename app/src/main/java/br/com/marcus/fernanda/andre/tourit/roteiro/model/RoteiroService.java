package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.roteiro.dao.RoteiroDAO;

/**
 * Created by André on 03/10/2017.
 */

public class RoteiroService {

    private Context context;
    private String idUsuarioGoogle;

    public RoteiroService(Context context, String idUsuarioGoogle) {
        this.context = context;
        this.idUsuarioGoogle = idUsuarioGoogle;
    }

    public int salvarRoteiro(Roteiro roteiro, List<Local> locais) throws SQLException {
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        roteiroDAO.salvarRoteiroFireBase(roteiro);
        int id = roteiroDAO.salvarRoteiroSqlite(roteiro);
        if(id >= 0) {
            new LocalService(context, idUsuarioGoogle).salvarLocais(locais, id);
        }else{
            throw new SQLException("Erro no armazenamento do roteiro");
        }
        return id;
    }

    public Roteiro consultarRoteiro(int idRoteiro) {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarRoteiroSqlite(idRoteiro);
    }
}