package br.com.marcus.fernanda.andre.tourit.usuario.model;

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

public class PesquisaUsuarioAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Usuario> usuarios;

    public PesquisaUsuarioAdapter(Context context, List<Usuario> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_convite, parent, false);
        PesquisaUsuarioViewHolder viewHolder = new PesquisaUsuarioViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PesquisaUsuarioViewHolder pesquisaUsuarioViewHolder = (PesquisaUsuarioViewHolder) holder;
        final Usuario usuario = usuarios.get(position);
        pesquisaUsuarioViewHolder.usuarioImageView.setImageBitmap(usuario.getFotoUsuario());
        pesquisaUsuarioViewHolder.nomeUsuarioTextView.setText(usuario.getNomeUsuario());
        pesquisaUsuarioViewHolder.usernameUsuarioTextView.setText(usuario.getUsername());
        pesquisaUsuarioViewHolder.adicionarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                adicionarUsuarioNaListaDeConvites(); .e di
                v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }
}
