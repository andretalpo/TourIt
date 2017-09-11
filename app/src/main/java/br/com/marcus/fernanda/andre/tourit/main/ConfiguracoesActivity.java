package br.com.marcus.fernanda.andre.tourit.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.login.controller.LoginActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

public class ConfiguracoesActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        Button desativarUsuarioButton = (Button) findViewById(R.id.desativarUsuarioButton);
        desativarUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarConfirmacaoInativacao();
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private void solicitarConfirmacaoInativacao() {
        new AlertDialog.Builder(this)
                .setTitle("Bloqueio de conta")
                .setMessage("Você realmente deseja bloquear sua conta?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int qualBotao) {
                        progressDialog = ProgressDialog.show(ConfiguracoesActivity.this, "Desativação", "Desativando usuário", true, false);
                        new DesativarUsuarioTask().execute(getIntent().getStringExtra("idGoogle"));
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void irParaTelaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("idUsuarioDeslogado", getIntent().getStringExtra("idGoogle"));
        startActivity(intent);
        finish();
    }

    private void finishMainActivity(){
        Intent intent = new Intent("finishActivity");
        sendBroadcast(intent);
    }

    private class DesativarUsuarioTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... idGoogle) {
            String keyUsuario = UsuarioDAO.buscarKeyUsuario(idGoogle[0]);
            UsuarioDAO.alterarStatusAtivo(keyUsuario, false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Toast.makeText(ConfiguracoesActivity.this, "Usuário desativado", Toast.LENGTH_LONG).show();
            irParaTelaLogin();
            finishMainActivity();
        }
    }
}