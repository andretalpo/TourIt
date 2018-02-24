package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.controler.CreateEventActivity;

/**
 * Created by André on 14/02/2018.
 */

public class ConviteAdapter extends RecyclerView.Adapter {

    private List<Convite> convites;
    private Context context;
    private StorageReference storageReference;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ConviteViewHolder conviteHolder = (ConviteViewHolder) holder;
        final Convite convite = convites.get(position);
        storageReference = FirebaseStorage.getInstance().getReference().child("imagemUsuario/" + convite.getIdUsuarioGoogleConvidado() + ".jpeg");
        conviteHolder.convidadoImageView.setImageBitmap(convite.getFotoConvidado());
        conviteHolder.nomeConvidadoTextView.setText(convite.getUsuarioConvidado());
        conviteHolder.usernameConvidadoTextView.setText(convite.getUsernameUsuarioConvidado());
        conviteHolder.excluirConviteImageView.setVisibility(View.GONE);
        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).into(conviteHolder.convidadoImageView);
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
        conviteHolder.cardConviteRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView botaoExcluir = (ImageView) v.findViewById(R.id.excluirUsuarioConviteViewHolderImageView);
                if(botaoExcluir.getVisibility() == View.GONE) {
                    botaoExcluir.setVisibility(View.VISIBLE);
                }else{
                    botaoExcluir.setVisibility(View.GONE);
                }
            }
        });
        conviteHolder.excluirConviteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventActivity.getListaConvidadosEvento().remove(convite);
                Intent intent = new Intent("atualizarAdapter");
                conviteHolder.excluirConviteImageView.setVisibility(View.GONE);
                context.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return convites.size();
    }
}
