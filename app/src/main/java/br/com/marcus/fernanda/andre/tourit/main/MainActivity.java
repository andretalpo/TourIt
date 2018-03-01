package br.com.marcus.fernanda.andre.tourit.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.controler.MeusEventosActivity;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalSearchFragment;
import br.com.marcus.fernanda.andre.tourit.login.controller.LoginActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.CreateRoteiroActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.MeusRoteirosActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.PesquisaRoteirosActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.controller.AdmUsuariosActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView navHeaderUsuarioImageView;
    private TextView navHeaderNomeUsuarioTextView;
    private TextView navHeaderUsernameTextView;
    private BroadcastReceiver broadcastReceiver;
    public static String idUsuarioGoogle;
    public static String nomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registrarBroadcastReceiver();

        idUsuarioGoogle = getIntent().getStringExtra("idGoogle");

        baixarImagemUsuario(idUsuarioGoogle);

        new ReativarUsuarioTask().execute(idUsuarioGoogle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        navHeaderUsuarioImageView = (ImageView) view.findViewById(R.id.navHeaderUsuarioImageView);
        navHeaderNomeUsuarioTextView = (TextView) view.findViewById(R.id.navHeaderNomeUsuarioTextView);
        navHeaderUsernameTextView = (TextView) view.findViewById(R.id.navHeaderUsernameTextView);

        personalizarMenu();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irParaTelaCriarRoteiro();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalSearchFragment localFragment = new LocalSearchFragment();

        transaction.replace(R.id.locaisMainActivityFrameLayout, localFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar configuracoesUsuarioAdmItem clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation_roteiro view configuracoesUsuarioAdmItem clicks here.
        int id = item.getItemId();

        if (id == R.id.drawerItemConfiguracoesUsuarioComum) {
            irParaTelaConfiguracoes(idUsuarioGoogle);
        } else if (id == R.id.drawerItemConfiguracoesUsuarioAdm) {
            irParaTelaUsuarios();
        } else if (id == R.id.drawerItemLogout) {
            irParaTelaLogin();
        } else if (id == R.id.drawerItemMeusRoteiros) {
            irParaTelaMeusRoteiros();
        } else if (id == R.id.drawerItemPesquisaRoteiros) {
            irParaTelaPesquisaRoteiros();
        } else if (id == R.id.drawerItemMeusEventos) {
            irParaTelaMeusEventos();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void baixarImagemUsuario(final String idGoogle) {
        StorageReference refImagemUsuario = FirebaseStorage.getInstance().getReference().child("imagemUsuario/" + idGoogle + ".jpeg");
        final long ONE_MEGABYTE = 200 * 200;
        refImagemUsuario.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                new UsuarioDAO(MainActivity.this, idGoogle).adicionarFotoUsuarioSqlite(idGoogle, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                navHeaderUsuarioImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Falha na imagem", exception.getMessage());
            }
        });
    }

    private void personalizarMenu() {
        Usuario usuario = new UsuarioDAO(this, idUsuarioGoogle).consultarUsuarioSqlite(idUsuarioGoogle);
        nomeUsuario = usuario.getNomeUsuario();
        navHeaderUsernameTextView.setText(usuario.getUsername());
        navHeaderNomeUsuarioTextView.setText(usuario.getNomeUsuario());
        if(usuario.getFotoUsuario() != null){
            navHeaderUsuarioImageView.setImageBitmap(usuario.getFotoUsuario());
        }
    }

    private void irParaTelaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("idUsuarioDeslogado", idUsuarioGoogle);
        startActivity(intent);
        finish();
    }

    private void irParaTelaConfiguracoes(String idGoogle) {
        Intent intent = new Intent(this, ConfiguracoesActivity.class);
        intent.putExtra("idGoogle", idGoogle);
        startActivity(intent);
    }

    private void irParaTelaMeusRoteiros() {
        Intent intent = new Intent(this, MeusRoteirosActivity.class);
        startActivity(intent);
    }

    private void irParaTelaUsuarios() {
        Intent intent = new Intent(this, AdmUsuariosActivity.class);
        startActivity(intent);
    }

    private void irParaTelaCriarRoteiro() {
        Intent intent = new Intent(this, CreateRoteiroActivity.class);
        startActivity(intent);
    }

    private void irParaTelaPesquisaRoteiros() {
        Intent intent = new Intent(this, PesquisaRoteirosActivity.class);
        startActivity(intent);
    }

    private void irParaTelaMeusEventos() {
        Intent intent = new Intent(this, MeusEventosActivity.class);
        startActivity(intent);
    }

    private void registrarBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finishActivity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finishActivity"));
    }

    private class ReativarUsuarioTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... idGoogle) {
            String keyUsuario = UsuarioDAO.buscarKeyUsuario(idGoogle[0]);
            if(keyUsuario != null) {
                UsuarioDAO.alterarStatusAtivo(keyUsuario, true);
            }
            return null;
        }
    }
}