package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import br.com.marcus.fernanda.andre.tourit.R;

public class MeusRoteirosActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RoteiroListFragment roteiroFragment = new RoteiroListFragment();
            Bundle bundle = new Bundle();

            switch (item.getItemId()) {
                case R.id.navigation_roteiros_criados:
                    bundle.putString("tipoRoteiro", "meusRoteiros");
                    roteiroFragment.setArguments(bundle);
                    transaction.replace(R.id.fragmentMeusRoteirosFrameLayout, roteiroFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_roteiros_seguidos:
                    bundle.putString("tipoRoteiro", "roteirosSeguidos");
                    roteiroFragment.setArguments(bundle);
                    transaction.replace(R.id.fragmentMeusRoteirosFrameLayout, roteiroFragment);
                    transaction.commit();
                    return true;
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_roteiros);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RoteiroListFragment roteiroFragment = new RoteiroListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tipoRoteiro", "meusRoteiros");
        roteiroFragment.setArguments(bundle);
        transaction.replace(R.id.fragmentMeusRoteirosFrameLayout, roteiroFragment);
        transaction.commit();
    }

}
