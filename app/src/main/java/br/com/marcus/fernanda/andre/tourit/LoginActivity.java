package br.com.marcus.fernanda.andre.tourit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 1;
    private static GoogleApiClient mGoogleApiClient;
    private DatabaseReference database;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance().getReference();

        inicializarApiGoogleSignIn();

        //Autenticador do firebase
        mAuth = FirebaseAuth.getInstance();

        //Se o usuario foi inativado, efetuar logout
        if(getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("usuarioInativado")) {
                signOut();
            }
        }

        //Se já estiver logado, abre outra Activity e encerra a de login
        if(mAuth.getCurrentUser()!= null){
            irParaTelaPrincipal();//esta entrando errado
        }

        //Listener para chamar método login quando apertar botão
        SignInButton signInButton = (SignInButton) findViewById(R.id.loginButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

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

    private void irParaTelaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("idGoogle", mAuth.getCurrentUser().getUid());
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressDialog = ProgressDialog.show(this, "Aguarde", "Conectando", true, false);

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
                            listenerBuscaUsuario(mAuth.getCurrentUser().getUid());
                            listenerBuscaInativo(mAuth.getCurrentUser().getUid());
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

    private void listenerBuscaUsuario(String idUsuario){
        database.child("Usuarios").orderByChild("idGoogle").equalTo(idUsuario).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(dataSnapshot.getValue(Usuario.class) != null){
                    irParaTelaPrincipal();
                }else{
                    irParaCriacaoUsuario();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //vazio
            }
        });
    }

    private void listenerBuscaInativo(String idUsuario){
        database.child("Usuarios").orderByChild("idGoogle").equalTo(idUsuario).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String keyUsuario = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    keyUsuario = childSnapshot.getKey();
                    if(!childSnapshot.getValue(Usuario.class).isAtivo()){
                        database.child("Usuarios").child(keyUsuario).child("ativo").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //vazio
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

    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                //vazio
            }
        });
    }

}