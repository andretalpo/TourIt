package br.com.marcus.fernanda.andre.tourit.usuario.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.controler.CreateEventActivity;
import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;

/**
 * Created by André on 14/02/2018.
 */

public class PesquisaUsuarioAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Usuario> usuarios;
    private StorageReference storageReference;

    public PesquisaUsuarioAdapter(Context context, List<Usuario> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_pesquisa_usuario, parent, false);
        PesquisaUsuarioViewHolder viewHolder = new PesquisaUsuarioViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PesquisaUsuarioViewHolder pesquisaUsuarioViewHolder = (PesquisaUsuarioViewHolder) holder;
        final Usuario usuario = usuarios.get(position);
        storageReference = FirebaseStorage.getInstance().getReference().child("imagemUsuario/" + usuario.getIdGoogle() + ".jpeg");
        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).into(pesquisaUsuarioViewHolder.usuarioImageView);
        pesquisaUsuarioViewHolder.nomeUsuarioTextView.setText(usuario.getNomeUsuario());
        pesquisaUsuarioViewHolder.usernameUsuarioTextView.setText(usuario.getUsername());
        for(Convite convite : CreateEventActivity.getListaConvidadosEvento()){
            if(convite.getIdUsuarioGoogleConvidado().equals(usuario.getIdGoogle())){
                pesquisaUsuarioViewHolder.adicionarImageView.setVisibility(View.GONE);
            }
        }
        pesquisaUsuarioViewHolder.adicionarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Convite convite = new Convite();
                convite.setUsuarioConvidado(usuario.getNomeUsuario());
                convite.setUsernameUsuarioConvidado(usuario.getUsername());
                convite.setIdUsuarioGoogleConvidado(usuario.getIdGoogle());
                CreateEventActivity.getListaConvidadosEvento().add(convite);
                v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }
}
