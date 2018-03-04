package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 27/02/2018.
 */

public class EventoViewHolder extends RecyclerView.ViewHolder {

    public ImageView fotoRoteiroEventoImageView;
    public TextView nomeEventoTextView;
    public TextView nomeRoteiroTextView;
    public TextView dataHoraEventoTextView;
    public RelativeLayout cardEventoRelativeLayout;

    public EventoViewHolder(View itemView) {
        super(itemView);
        this.fotoRoteiroEventoImageView = (ImageView) itemView.findViewById(R.id.imagemRoteiroEventoViewHolderImageView);
        this.nomeEventoTextView = (TextView) itemView.findViewById(R.id.nomeEventoViewHolderTextView);
        this.nomeRoteiroTextView = (TextView) itemView.findViewById(R.id.nomeRoteiroEventoViewHolderTextView);
        this.dataHoraEventoTextView = (TextView) itemView.findViewById(R.id.dataEventoViewHolder);
        this.cardEventoRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.cardViewHolderEventoRelativeLayout);
    }
}
