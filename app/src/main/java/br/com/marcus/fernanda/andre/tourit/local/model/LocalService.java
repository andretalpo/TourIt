package br.com.marcus.fernanda.andre.tourit.local.model;

import android.content.Context;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.local.dao.LocalDAO;

/**
 * Created by André on 04/10/2017.
 */

public class LocalService {

    private Context context;
    private String idUsuarioGoogle;

    public LocalService(Context context, String idUsuarioGoogle){
        this.context = context;
        this.idUsuarioGoogle = idUsuarioGoogle;
    }

    public void salvarLocais(List<Local> locais, Long idRoteiro){
        LocalDAO localDAO = new LocalDAO(context, idUsuarioGoogle);
        localDAO.salvarLocaisSQLite(locais, idRoteiro);
        localDAO.salvarLocaisFireBase(locais);
    }

    public List<Local> buscarLocaisRoteiro(Long idRoteiro) {
        return new LocalDAO(context, idUsuarioGoogle).buscarLocaisDoRoteiro(idRoteiro);
    }

    public void alterarLocaisRoteiro(Long idRoteiroSqlite, List<Local> listaLocais) {
        LocalDAO localDAO = new LocalDAO(context, idUsuarioGoogle);
        localDAO.excluirLocais(idRoteiroSqlite);
        localDAO.salvarLocaisSQLite(listaLocais, idRoteiroSqlite);
    }
}
