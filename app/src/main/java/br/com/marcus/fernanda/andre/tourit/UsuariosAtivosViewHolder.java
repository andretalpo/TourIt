package br.com.marcus.fernanda.andre.tourit;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by André on 27/08/2017.
 */

public class UsuariosAtivosViewHolder extends RecyclerView.ViewHolder {
    TextView usernameTextView;
    TextView emailTextView;
    TextView nomeTextView;
    Switch ativoSwith;

    public UsuariosAtivosViewHolder(View itemView) {
        super(itemView);
        usernameTextView = (TextView) itemView.findViewById(R.id.usuariosAdmUsernameTextView);
        emailTextView = (TextView) itemView.findViewById(R.id.usuariosAdmEmailTextView);
        nomeTextView = (TextView) itemView.findViewById(R.id.usuariosAdmNomeTextView);
        ativoSwith = (Switch) itemView.findViewById(R.id.usuariosAdmAtivoSwitch );
    }

}
