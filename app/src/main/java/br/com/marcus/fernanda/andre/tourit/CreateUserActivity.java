package br.com.marcus.fernanda.andre.tourit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateUserActivity extends AppCompatActivity {

    EditText usernameEditText;
    DatabaseReference database;
    Bitmap imagemUsuarioGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        database = FirebaseDatabase.getInstance().getReference();

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        new BaixarImagemTask().execute(getIntent().getStringExtra("urlFotoUsuario"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                if(username.length() >= 4) {
                    username = username.toLowerCase();
                    listenerBuscaUsername(username);
                }else{
                    Toast.makeText(CreateUserActivity.this, "Seu username deve conter no mínimo quatro dígitos", Toast.LENGTH_LONG).show();
                }
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
        usuario.setUsername(username);
        usuario.setAdmnistrador(false);
        usuario.setAtivo(true);
        database.child("Usuarios").push().setValue(usuario);
        armazenarImagem(usuario.getIdGoogle());
    }

    private void armazenarImagem(String idGoogle) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagemUsuarioGoogle.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imagemBytes = stream.toByteArray();

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("imagemUsuario/" + idGoogle + ".jpeg").putBytes(imagemBytes);
    }

    private class BaixarImagemTask extends AsyncTask<String, Void, Bitmap> {
        public BaixarImagemTask (){
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            imagemUsuarioGoogle = null;
            HttpURLConnection connection = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                try(InputStream inputStream = connection.getInputStream()){
                    imagemUsuarioGoogle = BitmapFactory.decodeStream(inputStream);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally{
                connection.disconnect();
            }
            return imagemUsuarioGoogle;
        }
    }
}
