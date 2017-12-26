package br.com.marcus.fernanda.andre.tourit.login.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.usuario.controller.CreateUserActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarApiGoogleSignIn();

        //Autenticador do firebase
        mAuth = FirebaseAuth.getInstance();

        //Se tiver sido sinalizado como deslogado, desloga
        if(getIntent().getStringExtra("idUsuarioDeslogado") != null){
            signOut();
        }

        //Se já estiver logado, abre outra Activity e encerra a de login
        if(mAuth.getCurrentUser()!= null){
            irParaTelaPrincipal();
        }

        //Listener para chamar método login quando apertar botão
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //Metodo necessário para Firebase
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Método chamado ao apertar botão do Google, criando intent de login e aguardando resultado
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.aguarde), getResources().getString(R.string.conectando), true, false);

        // Resultado da intent de login
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //Login do Google foi realizado com sucesso, passa a conta logada para logar no Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //Login do Google falhou
                Toast.makeText(this, "Falha no login", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
    }

    //Método de autenticação no Firebase utilizando conta Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Pega credencial e utiliza para login no Firebase, adicionando um listener para quando completar o login
        //Ao completar, analisa-se o resultado para realizar uma ação no caso de sucesso ou falha
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            new ConsultarUsuarioExistenteTask().execute(mAuth.getCurrentUser().getUid());
                        }else {
                            Toast.makeText(LoginActivity.this, "Erro no login", Toast.LENGTH_SHORT).show();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient.connect();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void irParaCriacaoUsuario(){
        Intent intent = new Intent(this, CreateUserActivity.class);
        FirebaseUser user = mAuth.getCurrentUser();
        intent.putExtra("idGoogle", user.getUid());
        intent.putExtra("nomeUsuario", user.getDisplayName());
        intent.putExtra("emailUsuario", user.getEmail());
        intent.putExtra("urlFotoUsuario", user.getPhotoUrl().toString());
        startActivity(intent);
        finish();
    }

    private void irParaTelaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("idGoogle", mAuth.getCurrentUser().getUid());
        startActivity(intent);
        finish();
    }

    private void signOut(){
        final ProgressDialog dialog = ProgressDialog.show(this, getResources().getString(R.string.deslogaando_usuario), getResources().getString(R.string.aguarde), true, false);
        mAuth.signOut();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()) {
                                dialog.dismiss();
                            }
                        }
                    });
                }

            }

            @Override
            public void onConnectionSuspended(int i) {
                //vazio
            }

        });
    }

    private void inicializarApiGoogleSignIn() {
        //Configura opções de login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Configuração e criação do client da API de login
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Erro de conexão", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private class ConsultarUsuarioExistenteTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... idGoogle) {
            Usuario usuario = UsuarioDAO.consultarUsuario("idGoogle", idGoogle[0]);
            if(usuario != null){
                RoteiroService roteiroService = new RoteiroService(LoginActivity.this, idGoogle[0]);
                if(roteiroService.consultarMeusRoteiros() == null) {
                    new UsuarioDAO(LoginActivity.this, idGoogle[0]).salvarUsuarioSqlite(usuario);
                    roteiroService.sincronizarRoteirosUsuario();
                }
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean usuarioExistente) {
            progressDialog.dismiss();
            if(usuarioExistente){
                irParaTelaPrincipal();
            }else{
                irParaCriacaoUsuario();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}