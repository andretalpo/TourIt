package br.com.marcus.fernanda.andre.tourit.roteiro.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.model.AvaliacaoLocal;
import br.com.marcus.fernanda.andre.tourit.utilitarios.AvaliacaoViewHolder;

/**
 * Created by André on 28/09/2017.
 */

public class AvaliacaoRoteiroAdapter extends RecyclerView.Adapter {
    private List<AvaliacaoRoteiro> avaliacoes;
    private Context context;

    public AvaliacaoRoteiroAdapter(List<AvaliacaoRoteiro> avaliacoes, Context context) {
        this.avaliacoes = avaliacoes;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_avaliacao, parent, false);
//        view.setBackground(context.getResources().getDrawable(R.color.cor_branco));
        return new AvaliacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AvaliacaoViewHolder avaliacaoHolder = (AvaliacaoViewHolder) holder;
        avaliacaoHolder.nomeAvaliadorTextView.setTextColor(context.getResources().getColor(R.color.cor_branco));
        avaliacaoHolder.comentarioTextView.setTextColor(context.getResources().getColor(R.color.cor_branco));
        AvaliacaoRoteiro avaliacao = avaliacoes.get(position);
        avaliacaoHolder.nomeAvaliadorTextView.setText(avaliacao.getNomeAvaliador());
        avaliacaoHolder.notaRatingBar.setRating(avaliacao.getNota());
        avaliacaoHolder.comentarioTextView.setText(avaliacao.getComentario());
    }

    @Override
    public int getItemCount() {
        return avaliacoes.size();
    }
}
