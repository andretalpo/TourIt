package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
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

    public Roteiro salvarRoteiroSeguido(Roteiro roteiro, List<Local> locais) throws SQLException {
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);

        roteiro.setSeguido(true);
        Long id = roteiroDAO.salvarRoteiroSqlite(roteiro);
        if(id >= 0) {
            new LocalService(context, idUsuarioGoogle).salvarLocais(locais, id);
        }else{
            throw new SQLException("Erro no armazenamento do roteiro");
        }
        roteiro.setIdRoteiroSqlite(id);
        roteiroDAO.incrementarNumSeguirdores(roteiro);
        float nota = calcularNotaRoteiro(roteiro.getIdRoteiroFirebase());
        roteiro.setNotaRoteiro(nota);
        roteiroDAO.salvarNotaRoteiro(nota, roteiro.getIdRoteiroFirebase());
        roteiroDAO.atualizarNotaSqlite(roteiro.getIdRoteiroFirebase(), nota);

        return roteiro;
    }

    public Roteiro consultarRoteiroSQLite(String idRoteiro) {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarRoteiroSqlite(idRoteiro);
    }

    public List<Roteiro> consultarMeusRoteiros() {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarMeusRoteirosSqlite();
    }

    public List<Roteiro> consultarRoteirosSeguidos() {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarRoteirosSeguidos();
    }

    public void excluirRoteiro(Roteiro roteiro){
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        roteiroDAO.excluirRoteiroSqlite(roteiro.getIdRoteiroSqlite());
        if(!roteiro.isPublicado()){
            roteiroDAO.excluirRoteiroFirebase(roteiro.getIdRoteiroFirebase());
        }
        UsuarioDAO.excluirRoteiroUsuario(roteiro.getIdRoteiroFirebase());
    }

    public void excluirRoteiroSeguido(Roteiro roteiro) {
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        roteiroDAO.excluirRoteiroSqlite(roteiro.getIdRoteiroSqlite());
        roteiroDAO.decrementarNumSeguirdores(roteiro);
        float nota = calcularNotaRoteiro(roteiro.getIdRoteiroFirebase());
        roteiro.setNotaRoteiro(nota);
        roteiroDAO.atualizarNotaSqlite(roteiro.getIdRoteiroFirebase(), nota);
        roteiroDAO.salvarNotaRoteiro(nota, roteiro.getIdRoteiroFirebase());
        UsuarioDAO.excluirRoteiroSeguidoUsuario(roteiro.getIdRoteiroFirebase());
    }

    public void alterarRoteiro(Roteiro roteiro, List<Local> listaLocais) {
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        new LocalService(context, idUsuarioGoogle).alterarLocaisRoteiro(roteiro.getIdRoteiroSqlite(), listaLocais);
        roteiroDAO.alterarRoteiroSQLite(roteiro);
        roteiroDAO.alterarRoteiroFirebase(roteiro);
    }

    public float calcularNotaRoteiro(String idRoteiro) {
        float nota = 0;
        List<AvaliacaoRoteiro> avaliacaoRoteiroList = buscarAvalicoesRoteiro(idRoteiro);
        int cont = 0;
        for (AvaliacaoRoteiro avaliacao: avaliacaoRoteiroList) {
            nota += avaliacao.getNota();
            cont++;
        }
        if(cont != 0) {
            nota = (nota / cont) - 1;
            if(nota < 0){
                nota = 0;
            }
        }
        int seguidores = new RoteiroDAO(context, idUsuarioGoogle).consultarNumSeguidores(idRoteiro);
        if(seguidores <= 1){
            return nota;
        }else if(seguidores <= 2){
            return nota + 0.25f;
        }else if(seguidores <= 3){
            return nota + 0.5f;
        }else if(seguidores <= 4){
            return nota + 0.75f;
        }else {
            return nota + 1;
        }
    }

    public void sincronizarRoteirosUsuario() {
        List<String> roteirosId = new UsuarioService().buscarRoteirosUsuarioFirebase(idUsuarioGoogle);
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);
        if (roteirosId != null) {
            List<Roteiro> roteiros = roteiroDAO.buscarRoteirosUsuarioFirebase(roteirosId);
            LocalService localService = new LocalService(context, idUsuarioGoogle);
            for (Roteiro roteiro : roteiros) {
                if(roteiro != null) {
                    roteiro.setSeguido(false);
                    Long idRoteiro = roteiroDAO.salvarRoteiroSqlite(roteiro);
                    List<Local> locais = new ArrayList<>();
                    for (String local : roteiro.getIdLocaisRoteiro()) {
                        locais.add(new LocalService(context, idUsuarioGoogle).buscarLocalFirebase(local));
                    }
                    localService.salvarLocais(locais, idRoteiro);
                    roteiro.setIdRoteiroSqlite(idRoteiro);
                    roteiro.setImagemRoteiro(montarImagemRoteiro(locais));
                    roteiroDAO.alterarRoteiroSQLite(roteiro);
                }
            }
        }

        List<String> roteirosSeguidosId = new UsuarioService().buscarRoteirosSeguidosUsuarioFirebase(idUsuarioGoogle);
        if (roteirosSeguidosId != null) {
            List<Roteiro> roteiros = roteiroDAO.buscarRoteirosUsuarioFirebase(roteirosSeguidosId);
            LocalService localService = new LocalService(context, idUsuarioGoogle);
            for (Roteiro roteiro : roteiros) {
                roteiro.setSeguido(true);
                Long idRoteiro = roteiroDAO.salvarRoteiroSqlite(roteiro);
                List<Local> locais = new ArrayList<>();
                for (String local : roteiro.getIdLocaisRoteiro()) {
                    locais.add(new LocalService(context, idUsuarioGoogle).buscarLocalFirebase(local));
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
        if(locais.size() == 1) {
            Bitmap resized = Bitmap.createScaledBitmap(locais.get(0).getFoto(), 400, 300, true);
            canvas.drawBitmap(resized, 0, 0, paint);
        }else if(locais.size() == 2) {
            Bitmap resized = Bitmap.createScaledBitmap(locais.get(0).getFoto(), 400, 150, true);
            canvas.drawBitmap(resized, 0, 0, paint);
            resized = Bitmap.createScaledBitmap(locais.get(1).getFoto(), 400, 150, true);
            canvas.drawBitmap(resized, 0, resized.getHeight() + 10, paint);
        }else if(locais.size() >= 3) {
            Bitmap resized = Bitmap.createScaledBitmap(locais.get(0).getFoto(), 200, 150, true);
            canvas.drawBitmap(resized, 0, 0, paint);
            resized = Bitmap.createScaledBitmap(locais.get(1).getFoto(), 200, 150, true);
            canvas.drawBitmap(resized, resized.getWidth() + 10, 0, paint);
            resized = Bitmap.createScaledBitmap(locais.get(2).getFoto(), 400, 150, true);
            canvas.drawBitmap(resized, 0, resized.getHeight() + 10, paint);
        }
        return bitmap;
    }

    public void publicarRoteiro(String idRoteiroFirebase) {
        new RoteiroDAO(context, idUsuarioGoogle).publicarRoteiro(idRoteiroFirebase);
        new UsuarioService().adicionarRoteiroUsuario(idUsuarioGoogle, idRoteiroFirebase);
    }

    public List<Roteiro> consultarRoteirosPublicados(String pesquisa) {
        return new RoteiroDAO(context, idUsuarioGoogle).consultarRoteirosPublicados(pesquisa);
    }

    public void salvarAvaliacaoRoteiro(AvaliacaoRoteiro avaliacaoRoteiro) {
        new RoteiroDAO(context, idUsuarioGoogle).salvarAvaliacaoRoteiroFirebase(avaliacaoRoteiro);
        float nota = calcularNotaRoteiro(avaliacaoRoteiro.getIdRoteiro());
        salvarNotaRoteiros(nota , avaliacaoRoteiro.getIdRoteiro());
        new RoteiroDAO(context, idUsuarioGoogle).atualizarNotaSqlite(avaliacaoRoteiro.getIdRoteiro(), nota);
    }

    public void salvarNotaRoteiros(Float nota, String idRoteiro){
        new RoteiroDAO(context, idUsuarioGoogle).salvarNotaRoteiro(nota,idRoteiro);
    }

    public List<AvaliacaoRoteiro> buscarAvalicoesRoteiro(String idRoteiro) {
        return new RoteiroDAO(context, idUsuarioGoogle).buscarAvaliacoesRoteiro(idRoteiro);
    }

    public Roteiro salvarRoteiroSeguidoLocal(String idRoteiroFirebase) {
        LocalService localService = new LocalService(context, idUsuarioGoogle);
        RoteiroDAO roteiroDAO = new RoteiroDAO(context, idUsuarioGoogle);

        Roteiro roteiro = roteiroDAO.consultarRoteiroFirebase(idRoteiroFirebase);
        roteiro.setSeguido(true);
        Long idRoteiro = roteiroDAO.salvarRoteiroSqlite(roteiro);
        List<Local> locais = new ArrayList<>();
        for (String local : roteiro.getIdLocaisRoteiro()) {
            locais.add(new LocalService(context, idUsuarioGoogle).buscarLocalFirebase(local));
        }
        localService.salvarLocais(locais, idRoteiro);
        roteiro.setIdRoteiroSqlite(idRoteiro);
        roteiro.setImagemRoteiro(montarImagemRoteiro(locais));
        roteiroDAO.alterarRoteiroSQLite(roteiro);
        return roteiro;
    }
}