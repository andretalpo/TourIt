package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 14/02/2018.
 */

public class ConviteAdapter extends RecyclerView.Adapter {

    private List<Convite> convites;
    private Context context;

    public ConviteAdapter(List<Convite> convites, Context context) {
        this.convites = convites;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_convite, parent, false);
        ConviteViewHolder viewHolder = new ConviteViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConviteViewHolder conviteHolder = (ConviteViewHolder) holder;
        final Convite convite = convites.get(position);
        conviteHolder.convidadoImageView.setImageBitmap(convite.getFotoConvidado());
        conviteHolder.nomeConvidadoTextView.setText(convite.getUsuarioConvidado());
        conviteHolder.usernameConvidadoTextView.setText(convite.getUsernameUsuarioConvidado());
        if(convite.getRespostaConvite() == Convite.ACEITO){
            conviteHolder.respostaConvidadoTextView.setText("Confirmado");
            conviteHolder.respostaConvidadoTextView.setTextColor(context.getResources().getColor(R.color.cor_verde));
        }else if(convite.getRespostaConvite() == Convite.AGUARDANDO_RESPOSTA){
            conviteHolder.respostaConvidadoTextView.setText("Aguardando");
            conviteHolder.respostaConvidadoTextView.setTextColor(context.getResources().getColor(R.color.cor_amarelo));
        }else{
            conviteHolder.respostaConvidadoTextView.setText("Recusado");
            conviteHolder.respostaConvidadoTextView.setTextColor(context.getResources().getColor(R.color.cor_vermelho));
        }
    }

    @Override
    public int getItemCount() {
        return convites.size();
    }
}
