package br.com.marcus.fernanda.andre.tourit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by André on 27/08/2017.
 */

class UsuariosAtivosAdapter extends RecyclerView.Adapter {
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
        Usuario usuario = usuarios.get(position);
        usuariosHolder.imageView.setImageBitmap(usuario.getFotoUsuario());
        usuariosHolder.usernameTextView.setText(usuario.getUsername());
        usuariosHolder.nomeTextView.setText(usuario.getNomeUsuario());
        usuariosHolder.emailTextView.setText(usuario.getEmailUsuario());
        usuariosHolder.ativoSwith.setChecked(usuario.isAtivo());
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }
}
