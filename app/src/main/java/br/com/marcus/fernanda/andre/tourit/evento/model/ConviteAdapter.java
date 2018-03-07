package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.controler.CreateEventActivity;
import br.com.marcus.fernanda.andre.tourit.evento.controler.EventoDetailsActivity;

/**
 * Created by André on 14/02/2018.
 */

public class ConviteAdapter extends RecyclerView.Adapter {

    private List<Convite> convites;
    private Context context;
    private StorageReference storageReference;
//    private boolean excluir;

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
        conviteHolder.excluir = false;
        conviteHolder.excluirConviteImageView.setVisibility(View.VISIBLE);
        conviteHolder.excluirConviteImageView.setClickable(conviteHolder.excluir);
        storageReference = FirebaseStorage.getInstance().getReference().child("imagemUsuario/" + convite.getIdUsuarioGoogleConvidado() + ".jpeg");
        conviteHolder.convidadoImageView.setImageBitmap(convite.getFotoConvidado());
        conviteHolder.nomeConvidadoTextView.setText(convite.getUsuarioConvidado());
        conviteHolder.usernameConvidadoTextView.setText(convite.getUsernameUsuarioConvidado());
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
        if(!EventoDetailsActivity.isConsultando()) {
            conviteHolder.cardConviteRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!conviteHolder.excluir) {
                        conviteHolder.infoCardRelativeLayout.animate().x(80f).y(0f);
                        conviteHolder.excluirConviteImageView.setClickable(true);
                        conviteHolder.excluir = true;
                    } else {
                        conviteHolder.infoCardRelativeLayout.animate().x(0f).y(0f);
                        conviteHolder.infoCardRelativeLayout.setClickable(false);
                        conviteHolder.excluir = false;
                    }
                }
            });
            conviteHolder.excluirConviteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Colocar um if de caminho, pois será reutilizado no evento details
                    if (conviteHolder.excluir) {
                        CreateEventActivity.getListaConvidadosEvento().remove(convite);
                        conviteHolder.infoCardRelativeLayout.animate().x(0f).y(0f);
                        conviteHolder.excluirConviteImageView.setClickable(false);
                        conviteHolder.excluir = false;
                        Intent intent = new Intent("atualizarAdapter");
                        context.sendBroadcast(intent);
                    } else {
                        conviteHolder.infoCardRelativeLayout.animate().x(80f).y(0f);
                        conviteHolder.excluirConviteImageView.setClickable(true);
                        conviteHolder.excluir = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return convites.size();
    }
}
