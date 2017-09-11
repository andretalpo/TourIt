package br.com.marcus.fernanda.andre.tourit.usuario.controller;

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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.URL;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

public class CreateUserActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Bitmap imagemUsuarioGoogle;
    private ProgressDialog progressDialog;

    private boolean usernameCriado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        usernameCriado = false;

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
            username = username.toLowerCase();
            Usuario usuario = new Usuario();
            usuario.setIdGoogle(getIntent().getStringExtra("idGoogle"));
            usuario.setEmailUsuario(getIntent().getStringExtra("emailUsuario"));
            usuario.setNomeUsuario(getIntent().getStringExtra("nomeUsuario"));
            usuario.setUsername(username);
            usuario.setAdmnistrador(false);
            usuario.setAtivo(true);
            new CriarUsuarioTask().execute(usuario);
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

    private class CriarUsuarioTask extends AsyncTask<Usuario, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CreateUserActivity.this, "Criação", "Criando usuário", true, false);
        }

        @Override
        protected Boolean doInBackground(Usuario... usuario) {
            if(UsuarioDAO.consultarUsuario("username", usuario[0].getUsername()) == null){
                UsuarioDAO.salvarUsuario(usuario[0], converterImagem());
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            progressDialog.dismiss();
            if(sucesso){
                usernameCriado = true;
                irParaTelaPrincipal();
            }else{
                Toast.makeText(CreateUserActivity.this, "Username já utilizado.", Toast.LENGTH_SHORT).show();
                usernameEditText.setText("");
            }
        }
    }

    private byte[] converterImagem() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagemUsuarioGoogle.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

}