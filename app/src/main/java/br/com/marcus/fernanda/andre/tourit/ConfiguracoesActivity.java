package br.com.marcus.fernanda.andre.tourit;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesActivity extends AppCompatActivity {

    DatabaseReference database;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        database = FirebaseDatabase.getInstance().getReference();

        Button desativarUsuarioButton = (Button) findViewById(R.id.desativarUsuarioButton);
        desativarUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //colocar dialog de confirmação
                progressDialog = ProgressDialog.show(ConfiguracoesActivity.this, "Desativando usuário", "", true, false);
                listenerBuscarKeyUsuario(getIntent().getStringExtra("idGoogle"));
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private void listenerBuscarKeyUsuario(final String idGoogle){
        database.child("Usuarios").orderByChild("idGoogle").equalTo(idGoogle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String keyUsuario = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    keyUsuario = childSnapshot.getKey();
                }
                database.child("Usuarios").child(keyUsuario).child("ativo").setValue(false);

                mAuth.signOut();
                //Achar forma de logout
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();

                progressDialog.dismiss();
                //Toast.makeText(ConfiguracoesActivity.this, "Usuário desativado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Sei la
            }
        });
    }
}