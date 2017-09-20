package br.com.marcus.fernanda.andre.tourit.local.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalDetailsActivity;

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
        final Local local = locais.get(position);
        localHolder.localImageView.setImageBitmap(local.getFoto());
        localHolder.nomeLocalTextView.setText(local.getNome());
        localHolder.localRatingBar.setRating(local.getNota());
        localHolder.localLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LocalDetailsActivity.class);
                intent.putExtra("idPlace", local.getIdPlaces());
                intent.putExtra("nomePlace", local.getNome());
                intent.putExtra("enderecoPlace", local.getEndereco());
                intent.putExtra("ratingPlace", local.getNota());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                local.getFoto().compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] arrayFoto = stream.toByteArray();
                intent.putExtra("arrayFoto", arrayFoto);
                //passar imagem
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locais.size();
    }
}
