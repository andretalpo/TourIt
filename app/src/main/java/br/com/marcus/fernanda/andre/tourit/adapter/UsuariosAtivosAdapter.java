package br.com.marcus.fernanda.andre.tourit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.activities.AdmUsuariosActivity;
import br.com.marcus.fernanda.andre.tourit.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.viewholder.UsuariosAtivosViewHolder;

/**
 * Created by André on 27/08/2017.
 */

public class UsuariosAtivosAdapter extends RecyclerView.Adapter {
    private List<Usuario> usuarios;
    private Context context;

    public UsuariosAtivosAdapter(List<Usuario> usuarios, Context context){
        this.usuarios = usuarios;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_usuarios, parent, false);
        UsuariosAtivosViewHolder viewHolder = new UsuariosAtivosViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UsuariosAtivosViewHolder usuariosHolder = (UsuariosAtivosViewHolder) holder;
        final Usuario usuario = usuarios.get(position);
        usuariosHolder.usernameTextView.setText(usuario.getUsername());
        usuariosHolder.nomeTextView.setText(usuario.getNomeUsuario());
        usuariosHolder.emailTextView.setText(usuario.getEmailUsuario());
        usuariosHolder.ativoSwith.setChecked(usuario.isAtivo());
        usuariosHolder.ativoSwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch ativoSwich = (Switch) view;
                AdmUsuariosActivity.listenerAtualizarUsuario(usuario.getIdGoogle(), ativoSwich.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }
}
