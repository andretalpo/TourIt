package br.com.marcus.fernanda.andre.tourit.local.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 28/09/2017.
 */

public class AvaliacaoLocalViewHolder extends RecyclerView.ViewHolder {
    public TextView nomeAvaliadorTextView;
    public RatingBar notaRatingBar;
    public TextView comentarioTextView;

    public AvaliacaoLocalViewHolder(View itemView) {
        super(itemView);
        this.nomeAvaliadorTextView = (TextView) itemView.findViewById(R.id.nomeAvaliadorAvaliacaoLocalViewHolderTextView);
        this.notaRatingBar = (RatingBar) itemView.findViewById(R.id.notaAvaliacaoLocalViewHolderRatingBar);
        this.comentarioTextView = (TextView) itemView.findViewById(R.id.comentarioAvaliacaoLocalViewHolderTextView);
    }
}
