package br.com.marcus.fernanda.andre.tourit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateUserActivity extends AppCompatActivity {

    EditText usernameEditText;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        database = FirebaseDatabase.getInstance().getReference();

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                listenerBuscaUsername(username);
            }
        });
    }

    private void listenerBuscaUsername(final String username){
        database.child("Usuarios").orderByChild("username").equalTo(username).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Usuario.class) == null){
                    criarUsuario(username);
                    irParaTelaPrincipal();
                }else{
                    Toast.makeText(CreateUserActivity.this, "Username já utilizado.", Toast.LENGTH_SHORT).show();
                    usernameEditText.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Sei la
            }
        });
    }
    private void irParaTelaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("idUsuario", getIntent().getStringExtra("idGoogle"));
        startActivity(intent);
        finish();
    }

    private void criarUsuario(String username){
        Usuario usuario = new Usuario();
        usuario.setIdGoogle(getIntent().getStringExtra("idGoogle"));
        usuario.setEmailUsuario(getIntent().getStringExtra("emailUsuario"));
        usuario.setNomeUsuario(getIntent().getStringExtra("nomeUsuario"));
        usuario.setFotoUsuario(getIntent().getStringExtra("urlFotoUsuario"));
        usuario.setUsername(username);
        database.child("Usuarios").push().setValue(usuario);
    }
}
