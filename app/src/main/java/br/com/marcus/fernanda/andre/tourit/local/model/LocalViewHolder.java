package br.com.marcus.fernanda.andre.tourit.local.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 11/09/2017.
 */

public class LocalViewHolder extends RecyclerView.ViewHolder{
    public ImageView localImageView;
    public TextView nomeLocalTextView;
    public TextView enderecoLocalTextView;
    public TextView notaLocalTextView;
    public RatingBar localRatingBar;
    public RelativeLayout localLayout;

    public LocalViewHolder(View itemView) {
        super(itemView);
        this.localImageView = (ImageView) itemView.findViewById(R.id.imagemLocalViewHolderImageView);
        this.nomeLocalTextView = (TextView) itemView.findViewById(R.id.nomeLocalViewHolderTextView);
        this.enderecoLocalTextView = (TextView) itemView.findViewById(R.id.enderecoLocalViewHolderTextView);
        this.notaLocalTextView = (TextView) itemView.findViewById(R.id.avaliacaoLocalViewHolderTextView);
        this.localRatingBar = (RatingBar) itemView.findViewById(R.id.avaliacaoLocalViewHolderRatingBar);
        this.localLayout = (RelativeLayout) itemView.findViewById(R.id.localViewHolderLayout);
    }
}
