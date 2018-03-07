package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 14/02/2018.
 */

public class ConviteViewHolder extends RecyclerView.ViewHolder{

    public ImageView convidadoImageView;
    public TextView nomeConvidadoTextView;
    public TextView usernameConvidadoTextView;
    public TextView respostaConvidadoTextView;
    public RelativeLayout cardConviteRelativeLayout;
    public ImageView excluirConviteImageView;
    public RelativeLayout infoCardRelativeLayout;
    public boolean excluir;

    public ConviteViewHolder(View itemView) {
        super(itemView);
        this.convidadoImageView = (ImageView) itemView.findViewById(R.id.imagemConvidadoViewHolderImageView);
        this.nomeConvidadoTextView = (TextView) itemView.findViewById(R.id.nomeConvidadoViewHolderTextView);
        this.usernameConvidadoTextView = (TextView) itemView.findViewById(R.id.usernameConvidadoViewHolderTextView);
        this.respostaConvidadoTextView = (TextView) itemView.findViewById(R.id.respostaConviteViewHolderTextView);
        this.cardConviteRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.cardViewHolderConviteRelativeLayout);
        this.excluirConviteImageView = (ImageView) itemView.findViewById(R.id.excluirUsuarioConviteViewHolderImageView);
        this.infoCardRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.informacoesConviteViewHolderRelativeLayout);
        this.excluir = false;
    }
}
