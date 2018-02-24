package br.com.marcus.fernanda.andre.tourit.usuario.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.usuario.model.PesquisaUsuarioAdapter;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuarioService;

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
                new PesquisarUsuarioTask().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private class PesquisarUsuarioTask extends AsyncTask<String, Void, List<Usuario>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PesquisaUsuarioActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.busca));
            progressDialog.setMessage(getResources().getString(R.string.listando_usuarios));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<Usuario> doInBackground(String... pesquisa) {
            List<Usuario> listaUsuarios = new UsuarioService().buscarUsuarioConvite(pesquisa[0]);
            return listaUsuarios;
        }

        @Override
        protected void onPostExecute(List<Usuario> listaUsuarios) {
            usuarios.clear();
            progressDialog.dismiss();
            if(listaUsuarios != null){
                usuarios.addAll(listaUsuarios);
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(PesquisaUsuarioActivity.this, getResources().getString(R.string.busca_sem_resultado), Toast.LENGTH_LONG).show();
            }
        }
    }

}
