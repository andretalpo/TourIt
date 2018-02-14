package br.com.marcus.fernanda.andre.tourit.usuario.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 14/02/2018.
 */

public class PesquisaUsuarioViewHolder extends RecyclerView.ViewHolder {

    public ImageView usuarioImageView;
    public TextView nomeUsuarioTextView;
    public TextView usernameUsuarioTextView;
    public ImageView adicionarImageView;

    public PesquisaUsuarioViewHolder(View itemView) {
        super(itemView);
        this.usuarioImageView = (ImageView) itemView.findViewById(R.id.pesquisaUsuarioViewHolderImageView);
        this.nomeUsuarioTextView = (TextView) itemView.findViewById(R.id.pesquisaUsuarioViewHolderTextView);
        this.usernameUsuarioTextView = (TextView) itemView.findViewById(R.id.pesquisaUsuarioUsernameViewHolderTextView);
        this.adicionarImageView = (ImageView) itemView.findViewById(R.id.pesquisaUsuarioAdicionarViewHolderButton);
    }
}
