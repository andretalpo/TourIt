package br.com.marcus.fernanda.andre.tourit.local.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 11/09/2017.
 */

public class LocalViewHolder extends RecyclerView.ViewHolder{
    public ImageView localImageView;
    public TextView nomeLocalTextView;
    public RatingBar localRatingBar;


    public LocalViewHolder(View itemView) {
        super(itemView);
        this.localImageView = (ImageView) itemView.findViewById(R.id.imagemLocalViewHolderImageView);
        this.nomeLocalTextView = (TextView) itemView.findViewById(R.id.nomeLocalViewHolderTextView);
        this.localRatingBar = (RatingBar) itemView.findViewById(R.id.avaliacaoLocalViewHolderRatingBar);
    }
}
