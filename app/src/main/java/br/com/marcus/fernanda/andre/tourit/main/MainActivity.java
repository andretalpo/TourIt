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
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.login.controller.LoginActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.controller.AdmUsuariosActivity;
import br.com.marcus.fernanda.andre.tourit.usuario.model.Usuario;
import br.com.marcus.fernanda.andre.tourit.usuario.dao.UsuarioDAO;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem configuracoesUsuarioAdmItem;
    private ImageView navHeaderUsuarioImageView;
    private TextView navHeaderNomeUsuarioTextView;
    private TextView navHeaderUsernameTextView;
    private BroadcastReceiver broadcastReceiver;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storage = FirebaseStorage.getInstance().getReference();

        registrarBroadcastReceiver();

        baixarImagemUsuario(getIntent().getStringExtra("idGoogle"));

        new ReativarUsuarioTask().execute(getIntent().getStringExtra("idGoogle"));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        configuracoesUsuarioAdmItem = navigationView.getMenu().findItem(R.id.configuracoesUsuarioAdm);
        View view = navigationView.getHeaderView(0);
        navHeaderUsuarioImageView = (ImageView) view.findViewById(R.id.navHeaderUsuarioImageView);
        navHeaderNomeUsuarioTextView = (TextView) view.findViewById(R.id.navHeaderNomeUsuarioTextView);
        navHeaderUsernameTextView = (TextView) view.findViewById(R.id.navHeaderUsernameTextView);

        new CarregarDadosUsuarioTask().execute(getIntent().getStringExtra("idGoogle"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final SearchView searchView = (SearchView) findViewById(R.id.mainSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                LocalListFragment localFragment = new LocalListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", query);
                localFragment.setArguments(bundle);

                transaction.replace(R.id.localFragmentContentMainActivity, localFragment);
                transaction.commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void baixarImagemUsuario(String idGoogle) {
        StorageReference refImagemUsuario = storage.child("imagemUsuario/" + idGoogle + ".jpeg");
        final long ONE_MEGABYTE = 200 * 200;
        refImagemUsuario.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                navHeaderUsuarioImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, "Erro na imagem", Toast.LENGTH_SHORT).show();
            }
        });
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view configuracoesUsuarioAdmItem clicks here.
        int id = item.getItemId();

        if (id == R.id.configuracoesUsuarioComum) {
            irParaTelaConfiguracoes(getIntent().getStringExtra("idGoogle"));
        } else if (id == R.id.configuracoesUsuarioAdm) {
            irParaTelaUsuarios();
        } else if (id == R.id.logoutMenu) {
            irParaTelaLogin();
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

    private void personalizarMenu(Usuario usuario) {
        navHeaderUsernameTextView.setText(usuario.getUsername());
        navHeaderNomeUsuarioTextView.setText(usuario.getNomeUsuario());
    }

    private void irParaTelaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("idUsuarioDeslogado", getIntent().getStringExtra("idGoogle"));
        startActivity(intent);
        finish();
    }

    private void irParaTelaConfiguracoes(String idGoogle) {
        Intent intent = new Intent(this, ConfiguracoesActivity.class);
        intent.putExtra("idGoogle", idGoogle);
        startActivity(intent);
    }

    private void irParaTelaUsuarios() {
        Intent intent = new Intent(this, AdmUsuariosActivity.class);
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

    private class CarregarDadosUsuarioTask extends AsyncTask<String, Void, Usuario> {

        @Override
        protected Usuario doInBackground(String... idGoogle) {
            Usuario usuario = UsuarioDAO.consultarUsuario("idGoogle", idGoogle[0]);
            return usuario;
        }

        @Override
        protected void onPostExecute(Usuario usuario) {
            if(usuario.isAdmnistrador()){
                configuracoesUsuarioAdmItem.setVisible(true);
            }
            personalizarMenu(usuario);
        }
    }

    private class ReativarUsuarioTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... idGoogle) {
            String keyUsuario = UsuarioDAO.buscarKeyUsuario(idGoogle[0]);
            UsuarioDAO.alterarStatusAtivo(keyUsuario, true);
            return null;
        }
    }
}