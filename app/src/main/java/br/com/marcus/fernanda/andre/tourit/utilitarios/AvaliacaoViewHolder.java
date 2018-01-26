package br.com.marcus.fernanda.andre.tourit.utilitarios;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 28/09/2017.
 */

public class AvaliacaoViewHolder extends RecyclerView.ViewHolder {
    public TextView nomeAvaliadorTextView;
    public RatingBar notaRatingBar;
    public TextView comentarioTextView;

    public AvaliacaoViewHolder(View itemView) {
        super(itemView);
        this.nomeAvaliadorTextView = (TextView) itemView.findViewById(R.id.nomeAvaliadorAvaliacaoViewHolderTextView);
        this.notaRatingBar = (RatingBar) itemView.findViewById(R.id.notaAvaliacaoViewHolderRatingBar);
        this.comentarioTextView = (TextView) itemView.findViewById(R.id.comentarioAvaliacaoViewHolderTextView);
    }
}
