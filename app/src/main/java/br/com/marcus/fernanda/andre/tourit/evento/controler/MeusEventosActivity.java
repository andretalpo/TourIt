package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import br.com.marcus.fernanda.andre.tourit.R;

public class MeusEventosActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            EventoListFragment eventoFragment = new EventoListFragment();
            Bundle bundle = new Bundle();

            switch (item.getItemId()) {
                case R.id.navigation_meus_eventos:
                    bundle.putString("tipoEvento", "meusEventos");
                    eventoFragment.setArguments(bundle);
                    transaction.replace(R.id.fragmentMeusEventosFrameLayout, eventoFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_convites:
                    bundle.putString("tipoEvento", "convites");
                    eventoFragment.setArguments(bundle);
                    transaction.replace(R.id.fragmentMeusEventosFrameLayout, eventoFragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationEvento);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        EventoListFragment eventoFragment = new EventoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tipoEvento", "meusEventos");
        eventoFragment.setArguments(bundle);
        transaction.replace(R.id.fragmentMeusEventosFrameLayout, eventoFragment);
        transaction.commit();
    }

}
