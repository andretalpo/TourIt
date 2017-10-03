package br.com.marcus.fernanda.andre.tourit.usuario.model;

import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

/**
 * Created by André on 03/10/2017.
 */

public class UsuarioService {
    public void adicionarRoteiroUsuario(String idUsuario, String keyRoteiro) {
        UsuarioDAO.adicionarRoteiroUsuario(idUsuario, keyRoteiro);
    }
}
