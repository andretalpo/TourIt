package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalViewHolder;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.RoteiroDetailsActivity;

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
        roteiroHolder.roteiroLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RoteiroDetailsActivity.class);
                intent.putExtra("roteiro", roteiro);

//                byte[] arrayFoto = ImageConverter.convertBitmapToByte(local.getFoto());
//                intent.putExtra("arrayFoto", arrayFoto);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roteiros.size();
    }
}
