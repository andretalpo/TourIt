package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

    public ConviteViewHolder(View itemView) {
        super(itemView);
        this.convidadoImageView = (ImageView) itemView.findViewById(R.id.imagemConvidadoViewHolderImageView);
        this.nomeConvidadoTextView = (TextView) itemView.findViewById(R.id.nomeConvidadoViewHolderTextView);
        this.usernameConvidadoTextView = (TextView) itemView.findViewById(R.id.usernameConvidadoViewHolderTextView);
        this.respostaConvidadoTextView = (TextView) itemView.findViewById(R.id.respostaConviteViewHolderTextView);
    }
}
