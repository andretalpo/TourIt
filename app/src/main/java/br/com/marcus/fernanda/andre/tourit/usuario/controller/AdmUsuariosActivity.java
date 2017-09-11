package br.com.marcus.fernanda.andre.tourit.usuario.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuariosAtivosAdapter;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

public class AdmUsuariosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Usuario> listaUsuarios;
    private UsuariosAtivosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_adm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.usuariosAdmRecyclerView);

        listaUsuarios = new ArrayList<>();

        adapter = new UsuariosAtivosAdapter(listaUsuarios, this);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        final SearchView searchView = (SearchView) findViewById(R.id.usuariosAdmSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new PopularListViewTask().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private class PopularListViewTask extends AsyncTask<String, Void, List<Usuario>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AdmUsuariosActivity.this, "Busca", "Listando usuários", true, false);
        }

        @Override
        protected List<Usuario> doInBackground(String... username) {
            List<Usuario> usuarios = UsuarioDAO.listarUsuariosUsername(username[0]);
            if(usuarios != null){
                return usuarios;
            }
            else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Usuario> usuarios) {
            listaUsuarios.clear();
            progressDialog.dismiss();
            if(usuarios != null){
                listaUsuarios.addAll(usuarios);
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(AdmUsuariosActivity.this, "A busca não retornou resultados.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
