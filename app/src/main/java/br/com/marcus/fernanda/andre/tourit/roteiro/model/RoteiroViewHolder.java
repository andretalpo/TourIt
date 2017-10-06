package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 06/10/2017.
 */

public class RoteiroViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView nomeTextView;
    public TextView tipoTextView;
    public RatingBar ratingBar;
    public RelativeLayout roteiroLayout;

    public RoteiroViewHolder(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView.findViewById(R.id.imagemRoteiroViewHolderImageView);
        this.nomeTextView = (TextView) itemView.findViewById(R.id.nomeRoteiroViewHolderTextView);
        this.tipoTextView = (TextView) itemView.findViewById(R.id.tipoRoteiroViewHolderTextView);
        this.ratingBar = (RatingBar) itemView.findViewById(R.id.roteiroViewHolderRatingBar);
        this.roteiroLayout = (RelativeLayout) itemView.findViewById(R.id.roteiroViewHolderLayout);
    }
}
