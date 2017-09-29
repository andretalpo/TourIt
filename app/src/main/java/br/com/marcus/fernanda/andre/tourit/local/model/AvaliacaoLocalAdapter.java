package br.com.marcus.fernanda.andre.tourit.local.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 28/09/2017.
 */

public class AvaliacaoLocalAdapter extends RecyclerView.Adapter {
    private List<AvaliacaoLocal> avaliacoes;
    private Context context;

    public AvaliacaoLocalAdapter(List<AvaliacaoLocal> avaliacoes, Context context) {
        this.avaliacoes = avaliacoes;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_avaliacao_local, parent, false);
        return new AvaliacaoLocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AvaliacaoLocalViewHolder avaliacaoHolder = (AvaliacaoLocalViewHolder) holder;
        AvaliacaoLocal avaliacao = avaliacoes.get(position);
        avaliacaoHolder.nomeAvaliadorTextView.setText(avaliacao.getNomeAvaliador());
        avaliacaoHolder.notaRatingBar.setRating(avaliacao.getNota());
        avaliacaoHolder.comentarioTextView.setText(avaliacao.getComentario());
    }

    @Override
    public int getItemCount() {
        return avaliacoes.size();
    }
}
