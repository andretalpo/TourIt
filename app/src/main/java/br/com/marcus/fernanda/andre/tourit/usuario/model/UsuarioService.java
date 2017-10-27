package br.com.marcus.fernanda.andre.tourit.usuario.model;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

/**
 * Created by André on 03/10/2017.
 */

public class UsuarioService {
    public void adicionarRoteiroUsuario(String idUsuario, String keyRoteiro) {
        UsuarioDAO.adicionarRoteiroUsuario(idUsuario, keyRoteiro);
    }

    public void adicionarRoteiroSeguidoUsuario(String idUsuario, String keyRoteiro){
        UsuarioDAO.adicionarRoteiroSeguidoUsuario(idUsuario, keyRoteiro);
    }

    public List<String> buscarRoteirosUsuarioFirebase(String idUsuarioGoogle) {
        return UsuarioDAO.buscarRoteirosUsuarioFirebase(idUsuarioGoogle);
    }
}
