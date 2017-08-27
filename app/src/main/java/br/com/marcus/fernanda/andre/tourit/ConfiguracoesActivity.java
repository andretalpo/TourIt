package br.com.marcus.fernanda.andre.tourit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
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
                confirmarBloqueio();
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
                progressDialog.dismiss();
                Toast.makeText(ConfiguracoesActivity.this, "Usuário desativado", Toast.LENGTH_LONG).show();
                irParaTelaLogin();
                finishMainActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Sei la
            }
        });
    }

    private void irParaTelaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("usuarioInativado", true);
        startActivity(intent);
        finish();
    }

    private void finishMainActivity(){
        Intent intent = new Intent("finishActivity");
        sendBroadcast(intent);
    }

    private void confirmarBloqueio() {
        new AlertDialog.Builder(this)
                .setTitle("Bloqueio de conta")
                .setMessage("Você realmente deseja bloquear sua conta?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int qualBotao) {
                        progressDialog = ProgressDialog.show(ConfiguracoesActivity.this, "Desativando usuário", "", true, false);
                        listenerBuscarKeyUsuario(getIntent().getStringExtra("idGoogle"));
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}