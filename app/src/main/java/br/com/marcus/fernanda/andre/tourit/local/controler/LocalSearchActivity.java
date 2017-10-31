package br.com.marcus.fernanda.andre.tourit.local.controler;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.marcus.fernanda.andre.tourit.R;

public class LocalSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_search);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalSearchFragment localFragment = new LocalSearchFragment();

        transaction.replace(R.id.fragmentBuscaLocaisFrameLayout, localFragment);
        transaction.commit();

    }
}
