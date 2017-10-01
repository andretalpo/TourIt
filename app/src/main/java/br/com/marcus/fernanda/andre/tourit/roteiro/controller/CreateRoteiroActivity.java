package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalSearchActivity;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;

public class CreateRoteiroActivity extends AppCompatActivity {

    private static List<Local> listaLocaisRoteiroAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roteiro);
        listaLocaisRoteiroAtual = new ArrayList<>();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocalListFragment localFragment = new LocalListFragment();

        transaction.replace(R.id.listaLocaisRoteiroActivityFrameLayout, localFragment);
        transaction.commit();

        Button adicionarLocalButton = (Button) findViewById(R.id.adicionarLocalRoteiroActivityButton);
        adicionarLocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irParaTelaBuscaLocais();
            }
        });
    }

    private void irParaTelaBuscaLocais() {
        Intent intent = new Intent(this, LocalSearchActivity.class);
        startActivity(intent);
    }

    public static List<Local> getListaLocaisRoteiroAtual() {
        return listaLocaisRoteiroAtual;
    }

    public static void setListaLocaisRoteiroAtual(List<Local> listaLocaisRoteiroAtual) {
        CreateRoteiroActivity.listaLocaisRoteiroAtual = listaLocaisRoteiroAtual;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listaLocaisRoteiroAtual = null;
    }
}
