package br.com.marcus.fernanda.andre.tourit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.adapter.UsuariosAtivosAdapter;

public class AdmUsuariosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference database;
    private List<Usuario> listaUsuarios;
    private UsuariosAtivosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_adm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance().getReference();

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
                listenerBuscaUsuarioUsername(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void listenerBuscaUsuarioUsername(final String username){
        database.child("Usuarios").orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaUsuarios.clear();
                listaUsuarios = buscarUsuariosFiltrado(dataSnapshot, username);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //vazio
            }
        });
    }

    //estático para ser chamado no adapter
    public static void listenerAtualizarUsuario(final String idGoogle, final boolean ativo) {
        FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("idGoogle").equalTo(idGoogle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String keyUsuario = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    keyUsuario = childSnapshot.getKey();
                }
                FirebaseDatabase.getInstance().getReference().child("Usuarios").child(keyUsuario).child("ativo").setValue(ativo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Vazio
            }
        });
    }

    private List<Usuario> buscarUsuariosFiltrado(DataSnapshot dataSnapshot, String filtro) {
        Usuario usuario = null;
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            usuario = childSnapshot.getValue(Usuario.class);
            if(usuario.getUsername().contains(filtro)){
                listaUsuarios.add(childSnapshot.getValue(Usuario.class));
            }
        }
        return listaUsuarios;
    }
}
