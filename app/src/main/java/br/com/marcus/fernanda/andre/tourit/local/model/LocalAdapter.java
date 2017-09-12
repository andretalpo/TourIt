package br.com.marcus.fernanda.andre.tourit.local.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 11/09/2017.
 */

public class LocalAdapter extends RecyclerView.Adapter {
    private List<Local> locais;
    private Context context;

    public LocalAdapter(List<Local> locais, Context context) {
        this.locais = locais;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_local, parent, false);
        LocalViewHolder viewHolder = new LocalViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder localHolder = (LocalViewHolder) holder;
        Local local = locais.get(position);
        localHolder.localImageView.setImageBitmap(local.getFoto());
        localHolder.nomeLocalTextView.setText(local.getNome());
        localHolder.localRatingBar.setRating(local.getNota());
    }

    @Override
    public int getItemCount() {
        return locais.size();
    }
}
