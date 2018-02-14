package br.com.marcus.fernanda.andre.tourit.usuario.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.usuario.model.PesquisaUsuarioAdapter;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;

public class PesquisaUsuarioActivity extends AppCompatActivity {

    private List<Usuario> usuarios;
    PesquisaUsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_usuario);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listaUsuariosRecyclerViewPesquisaUsuarioActivity);

        usuarios = new ArrayList<>();

        adapter = new PesquisaUsuarioAdapter(this, usuarios);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        SearchView searchView = (SearchView) findViewById(R.id.pesquisaUsuarioSearchViewPesquisaUsuarioActivity);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //pesquisar usuario e atualizar adapter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
