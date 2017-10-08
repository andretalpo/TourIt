package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.dao.RoteiroDAO;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

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

    public Roteiro salvarRoteiro(Roteiro roteiro, List<Local> locais) throws SQLException {
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);

        int id = roteiroDAO.salvarRoteiroSqlite(roteiro);
        if(id >= 0) {
            new LocalService(context, idUsuarioGoogle).salvarLocais(locais, id);
        }else{
            throw new SQLException("Erro no armazenamento do roteiro");
        }
        roteiro.setIdRoteiroSqlite(id);
        roteiro.setIdRoteiroFirebase(MainActivity.idUsuarioGoogle + id);
        roteiroDAO.salvarRoteiroFireBase(roteiro);
        return roteiro;
    }

    public Roteiro consultarRoteiro(int idRoteiro) {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarRoteiroSqlite(idRoteiro);
    }

    public List<Roteiro> consultarMeusRoteiros() {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarMeusRoteirosSqlite();
    }

    public void excluirRoteiro(int idRoteiro){
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, MainActivity.idUsuarioGoogle);
        roteiroDAO.excluirRoteiroSqlite(idRoteiro);
        String keyRoteiro = roteiroDAO.buscarKeyUsuario(idRoteiro);
        roteiroDAO.excluirRoteiroFirebase(keyRoteiro);
        UsuarioDAO.excluirRoteiroUsuario(keyRoteiro);
    }
}