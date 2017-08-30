package br.com.marcus.fernanda.andre.tourit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference database;
    private Usuario usuario;
    private MenuItem configuracoesUsuarioAdmItem;
    private ImageView navHeaderUsuarioImageView;
    private TextView navHeaderNomeUsuarioTextView;
    private TextView navHeaderUsernameTextView;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance().getReference();

        registrarBroadcastReceiver();

        listenerAtivarUsuario(getIntent().getStringExtra("idGoogle"));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        configuracoesUsuarioAdmItem = navigationView.getMenu().findItem(R.id.configuracoesUsuarioAdm);
        View view = navigationView.getHeaderView(0);
        navHeaderUsuarioImageView = (ImageView) view.findViewById(R.id.navHeaderUsuarioImageView);
        navHeaderNomeUsuarioTextView = (TextView) view.findViewById(R.id.navHeaderNomeUsuarioTextView);
        navHeaderUsernameTextView = (TextView) view.findViewById(R.id.navHeaderUsernameTextView);

        listenerBuscaIdUsuario(getIntent().getStringExtra("idGoogle"));

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
        } else if(id == R.id.logoutMenu){;
            irParaTelaLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void irParaTelaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("idUsuarioDeslogado", getIntent().getStringExtra("idGoogle"));
        startActivity(intent);
        finish();
    }

    private void listenerBuscaIdUsuario(final String idGoogle){
        database.child("Usuarios").orderByChild("idGoogle").equalTo(idGoogle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    usuario = childSnapshot.getValue(Usuario.class);
                }
                if(usuario.isAdmnistrador()){
                    configuracoesUsuarioAdmItem.setVisible(true);
                }
                personalizarMenu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Sei la
            }
        });
    }

    private void personalizarMenu() {
        navHeaderUsernameTextView.setText(usuario.getUsername());
        navHeaderNomeUsuarioTextView.setText(usuario.getNomeUsuario());
    }

    private void irParaTelaConfiguracoes(String idGoogle){
        Intent intent = new Intent(this, ConfiguracoesActivity.class);
        intent.putExtra("idGoogle", idGoogle);
        startActivity(intent);
    }

    private void irParaTelaUsuarios(){
        Intent intent = new Intent(this, UsuarioAdmActivity.class);
        startActivity(intent);
    }

    public void listenerAtivarUsuario(final String idGoogle) {
        database.child("Usuarios").orderByChild("idGoogle").equalTo(idGoogle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String keyUsuario = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    keyUsuario = childSnapshot.getKey();
                }
                database.child("Usuarios").child(keyUsuario).child("ativo").setValue(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Vazio
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}