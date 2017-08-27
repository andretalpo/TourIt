package br.com.marcus.fernanda.andre.tourit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdmActivity extends AppCompatActivity {

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_adm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.usuariosAdmRecyclerView);

        List<Usuario> listaUsuarios = new ArrayList<>();//pegar de busca
        //-----------------------------------------------------------------
        Usuario usuario = new Usuario();
        usuario.setAtivo(false);
        usuario.setUsername("andre");
        usuario.setEmailUsuario("andre.talpo@gmail.com");
        usuario.setNomeUsuario("André");
        usuario.setFotoUsuario(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
        listaUsuarios.add(usuario);
        //----------------------------------------------------------------

        recyclerView.setAdapter(new UsuariosAtivosAdapter(listaUsuarios, this));

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
    }

}
