package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroAdapter;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;

/**
 * Created by André on 06/10/2017.
 */

public class RoteiroListFragment extends Fragment {
    private static final String TAG = "roteiroListFragment";

    private List<Roteiro> listaRoteiros;
    private RoteiroAdapter adapter;
    private Bundle bundle;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roteiro_list, container, false);
        view.setTag(TAG);

        RecyclerView roteirosRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentRoteiroRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        roteirosRecyclerView.setLayoutManager(layout);

        listaRoteiros = new ArrayList<>();

        adapter = new RoteiroAdapter(listaRoteiros, getActivity());
        roteirosRecyclerView.setAdapter(adapter);

        bundle = getArguments();
        if(bundle.getString("tipoRoteiro").equals("meusRoteiros")) {
            carregarMeusRoteirosBanco();
        } else if(bundle.getString("tipoRoteiro").equals("pesquisaRoteiros")){
            new PesquisaRoteirosTask().execute(bundle.getString("pesquisa"));
        } else if(bundle.getString("tipoRoteiro").equals("roteirosSeguidos")){
            carregarRoteirosSeguidos();
        }

        return view;
    }

    private void carregarRoteirosSeguidos() {
        List<Roteiro> listaRoteirosSeguidos = new RoteiroService(RoteiroListFragment.this.getContext(), MainActivity.idUsuarioGoogle).consultarRoteirosSeguidos();
        listaRoteiros.clear();

        if(listaRoteirosSeguidos != null){
            listaRoteiros.addAll(listaRoteirosSeguidos);
        }
        adapter.notifyDataSetChanged();
    }

    private void carregarMeusRoteirosBanco() {
        List<Roteiro> listaMeusRoteiros = new RoteiroService(RoteiroListFragment.this.getContext(), MainActivity.idUsuarioGoogle).consultarMeusRoteiros();
        listaRoteiros.clear();

        if(listaMeusRoteiros != null){
            listaRoteiros.addAll(listaMeusRoteiros);
        }
        adapter.notifyDataSetChanged();
    }

    private class PesquisaRoteirosTask extends AsyncTask<String, Void, List<Roteiro>> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RoteiroListFragment.this.getContext(), R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.buscando_roteiros));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<Roteiro> doInBackground(String... pesquisa) {
            List<Roteiro> roteiros = new RoteiroService(RoteiroListFragment.this.getContext(), MainActivity.idUsuarioGoogle).consultarRoteirosPublicados(pesquisa[0]);
            if(roteiros != null) {
                listaRoteiros.addAll(roteiros);
                List<Local> locais = new ArrayList<>();
                for (Roteiro roteiro : listaRoteiros) {
                    locais.clear();
                    for (String local : roteiro.getIdLocaisRoteiro()) {
                        locais.add(new LocalService(RoteiroListFragment.this.getContext(), MainActivity.idUsuarioGoogle).buscarLocalFirebase(local));
                    }
                    roteiro.setImagemRoteiro(new RoteiroService(RoteiroListFragment.this.getContext(), MainActivity.idUsuarioGoogle).montarImagemRoteiro(locais));
                }
                return listaRoteiros;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Roteiro> roteiros) {
            if(listaRoteiros.isEmpty()){
                Toast.makeText(RoteiroListFragment.this.getContext(), getResources().getString(R.string.nenhum_roteiro_encontrado), Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bundle.getString("tipoRoteiro").equals("meusRoteiros")) {
            carregarMeusRoteirosBanco();
        }else if(bundle.getString("tipoRoteiro").equals("roteirosSeguidos")){
            carregarRoteirosSeguidos();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}