package br.com.marcus.fernanda.andre.tourit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.model.Usuario;

public class CreateUserActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private DatabaseReference database;
    private Bitmap imagemUsuarioGoogle;
    private ProgressDialog progressDialog;

    private boolean usernameCriado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        usernameCriado = false;

        database = FirebaseDatabase.getInstance().getReference();

        new BaixarImagemTask().execute(getIntent().getStringExtra("urlFotoUsuario"));

        usernameEditText = (EditText) findViewById(R.id.usernameEditTextCriarUsuario);
        usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);
                    criarUsuario();
                    handled = true;
                }
                return handled;
            }
        });

        Button button = (Button) findViewById(R.id.botaoCriarUsuario);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarUsuario();
            }
        });
    }

    private void criarUsuario() {
        String username = usernameEditText.getText().toString();
        if(username.length() >= 4) {
            progressDialog = ProgressDialog.show(CreateUserActivity.this, "Criação", "Criando usuário", true, false);
            username = username.toLowerCase();
            listenerCriacaoUsername(username);
        }else{
            Toast.makeText(CreateUserActivity.this, "Seu username deve conter no mínimo quatro dígitos", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!usernameCriado){
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void listenerCriacaoUsername(final String username){
        database.child("Usuarios").orderByChild("username").equalTo(username).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(dataSnapshot.getValue(Usuario.class) == null){
                    salvarUsuarioEmBanco(username);
                    irParaTelaPrincipal();
                }else{
                    Toast.makeText(CreateUserActivity.this, "Username já utilizado.", Toast.LENGTH_SHORT).show();
                    usernameEditText.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Vazio
            }
        });
    }

    private void salvarUsuarioEmBanco(String username){
        Usuario usuario = new Usuario();
        usuario.setIdGoogle(getIntent().getStringExtra("idGoogle"));
        usuario.setEmailUsuario(getIntent().getStringExtra("emailUsuario"));
        usuario.setNomeUsuario(getIntent().getStringExtra("nomeUsuario"));
        usuario.setUsername(username);
        usuario.setAdmnistrador(false);
        usuario.setAtivo(true);
        database.child("Usuarios").push().setValue(usuario);
        armazenarImagem(usuario.getIdGoogle());
        usernameCriado = true;
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

    private void irParaTelaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("idGoogle", getIntent().getStringExtra("idGoogle"));
        startActivity(intent);
        finish();
    }

}
