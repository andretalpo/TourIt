package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.RoteiroDetailsActivity;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

/**
 * Created by André on 06/10/2017.
 */

public class RoteiroAdapter extends RecyclerView.Adapter {
    private List<Roteiro> roteiros;
    private Context context;

    public RoteiroAdapter(List<Roteiro> roteiros, Context context) {
        this.roteiros = roteiros;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_roteiro, parent, false);
        RoteiroViewHolder viewHolder = new RoteiroViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RoteiroViewHolder roteiroHolder = (RoteiroViewHolder) holder;
        final Roteiro roteiro = roteiros.get(position);
        roteiroHolder.nomeTextView.setText(roteiro.getNomeRoteiro());
        roteiroHolder.tipoTextView.setText(roteiro.getTipoRoteiro());
        roteiroHolder.ratingBar.setRating(roteiro.getNotaRoteiro());
        roteiroHolder.imageView.setImageBitmap(roteiro.getImagemRoteiro());
        roteiroHolder.roteiroLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RoteiroDetailsActivity.class);
                intent.putExtra("roteiro", roteiro);

                byte[] arrayImagem = ImageConverter.convertBitmapToByte(roteiro.getImagemRoteiro());
                intent.putExtra("imagemRoteiro", arrayImagem);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roteiros.size();
    }
}
