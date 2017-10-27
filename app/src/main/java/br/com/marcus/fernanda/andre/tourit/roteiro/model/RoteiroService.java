package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.roteiro.dao.RoteiroDAO;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuarioService;

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

        roteiro = roteiroDAO.salvarRoteiroFireBase(roteiro);

        Long id = roteiroDAO.salvarRoteiroSqlite(roteiro);
        if(id >= 0) {
            new LocalService(context, idUsuarioGoogle).salvarLocais(locais, id);
        }else{
            throw new SQLException("Erro no armazenamento do roteiro");
        }
        roteiro.setIdRoteiroSqlite(id);

        return roteiro;
    }

    public Roteiro consultarRoteiro(int idRoteiro) {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarRoteiroSqlite(idRoteiro);
    }

    public List<Roteiro> consultarMeusRoteiros() {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarMeusRoteirosSqlite();
    }

    public void excluirRoteiro(Roteiro roteiro){
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        roteiroDAO.excluirRoteiroSqlite(roteiro.getIdRoteiroSqlite());
        if(!roteiro.isPublicado()){
            roteiroDAO.excluirRoteiroFirebase(roteiro.getIdRoteiroFirebase());
        }
        UsuarioDAO.excluirRoteiroUsuario(roteiro.getIdRoteiroFirebase());
    }

    public void alterarRoteiro(Roteiro roteiro, List<Local> listaLocais) {
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        new LocalService(context, idUsuarioGoogle).alterarLocaisRoteiro(roteiro.getIdRoteiroSqlite(), listaLocais);
        roteiroDAO.alterarRoteiroSQLite(roteiro);
        roteiroDAO.alterarRoteiroFirebase(roteiro);
    }

    public float calcularNotaRoteiro(List<Local> listaLocais) {
        float nota = 0;
        for (Local local : listaLocais) {
            nota += local.getNota();
        }
        return nota/listaLocais.size();
    }

    public void sincronizarRoteirosUsuario() {
        List<String> roteirosId = new UsuarioService().buscarRoteirosUsuarioFirebase(idUsuarioGoogle);
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        if (roteirosId != null) {
            List<Roteiro> roteiros = roteiroDAO.buscarRoteirosUsuarioFirebase(roteirosId);
            LocalService localService = new LocalService(context, idUsuarioGoogle);
            for (Roteiro roteiro : roteiros) {
                Long idRoteiro = roteiroDAO.salvarRoteiroSqlite(roteiro);
                List<Local> locais = new ArrayList<>();
                for (String local : roteiro.getLocaisRoteiro()) {
                    locais.add(GooglePlacesServices.buscarLocalIdPlaces(local));
                }
                localService.salvarLocais(locais, idRoteiro);
                roteiro.setIdRoteiroSqlite(idRoteiro);
                roteiro.setImagemRoteiro(montarImagemRoteiro(locais));
                roteiroDAO.alterarRoteiroSQLite(roteiro);
            }
        }
    }

    public Bitmap montarImagemRoteiro(List<Local> locais) {
        Bitmap bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        if(locais.size() > 0) {
            Bitmap resized = Bitmap.createScaledBitmap(locais.get(0).getFoto(), 200, 150, true);
            canvas.drawBitmap(resized, 0, 0, paint);
            if(locais.size() > 1) {
                resized = Bitmap.createScaledBitmap(locais.get(1).getFoto(), 200, 150, true);
                canvas.drawBitmap(resized, resized.getWidth() + 10, 0, paint);
                if(locais.size() > 2) {
                    resized = Bitmap.createScaledBitmap(locais.get(2).getFoto(), 400, 150, true);
                    canvas.drawBitmap(resized, 0, resized.getHeight() + 10, paint);
                }
            }
        }
        return bitmap;
    }

    public void publicarRoteiro(String idRoteiroFirebase) {
        new RoteiroDAO(context, idUsuarioGoogle).publicarRoteiro(idRoteiroFirebase);
    }

    public List<Roteiro> consultarRoteirosPublicados(String pesquisa) {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarRoteirosPublicados(pesquisa);
    }
}