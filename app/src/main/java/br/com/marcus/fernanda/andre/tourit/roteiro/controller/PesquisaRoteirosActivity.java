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

import br.com.marcus.fernanda.andre.tourit.R;

public class PesquisaRoteirosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_roteiros);

        final LinearLayout cardsLayout = (LinearLayout) findViewById(R.id.cardsPesquisaRoteiroActivityLinearLayout);
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
}
