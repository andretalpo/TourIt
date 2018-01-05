package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import br.com.marcus.fernanda.andre.tourit.R;

public class PesquisaRoteirosActivity extends AppCompatActivity {

    ScrollView cardsLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_roteiros);
        cardsLayout = (ScrollView) findViewById(R.id.cardsPesquisaRoteiroActivityScrollView);

        Button melhoresButton = (Button) findViewById(R.id.melhoresPesquisaRoteiroActivityButton);
        melhoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsLayout.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", "todos");
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
            }
        });

        Button culturalButton = (Button) findViewById(R.id.culturalPesquisaRoteiroActivityButton);
        culturalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsLayout.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", "cultural");
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
            }
        });

        Button aventuraButton = (Button) findViewById(R.id.aventuraPesquisaRoteiroActivityButton);
        aventuraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsLayout.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", "aventura");
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
            }
        });

        Button gastronomiaButton = (Button) findViewById(R.id.gastronomiaPesquisaRoteiroActivityButton);
        gastronomiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsLayout.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", "gastronomia");
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
            }
        });

        Button naturezaButton = (Button) findViewById(R.id.naturezaPesquisaRoteiroActivityButton);
        naturezaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsLayout.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", "natureza");
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
            }
        });

        Button romanticoButton = (Button) findViewById(R.id.romanticoPesquisaRoteiroActivityButton);
        romanticoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsLayout.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", "Romântico");
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
            }
        });

        Button diversosButton = (Button) findViewById(R.id.diversosPesquisaRoteiroActivityButton);
        diversosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsLayout.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", "diversos");
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
            }
        });

        final SearchView searchView = (SearchView) findViewById(R.id.pesquisaRoteirosSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cardsLayout.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.clearFocus();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RoteiroListFragment roteiroFragment = new RoteiroListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("pesquisa", query);
                bundle.putString("tipoRoteiro", "pesquisaRoteiros");
                roteiroFragment.setArguments(bundle);

                transaction.replace(R.id.pesquisaRoteirosFrameLayout, roteiroFragment);
                transaction.commit();
                searchView.requestFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if(cardsLayout != null && cardsLayout.getVisibility() == View.GONE){
            cardsLayout.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }
    }
}
