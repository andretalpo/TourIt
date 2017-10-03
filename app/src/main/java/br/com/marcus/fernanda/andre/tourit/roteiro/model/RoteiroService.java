package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;

import br.com.marcus.fernanda.andre.tourit.roteiro.dao.RoteiroDAO;

/**
 * Created by André on 03/10/2017.
 */

public class RoteiroService {

    public static void salvarRoteiro(Context context, String idUsuarioGoogle, Roteiro roteiro){
        new RoteiroDAO(context, idUsuarioGoogle).salvarRoteiro(roteiro);
    }
}
