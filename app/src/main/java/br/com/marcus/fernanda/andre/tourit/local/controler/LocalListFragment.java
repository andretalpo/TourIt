package br.com.marcus.fernanda.andre.tourit.local.controler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalAdapter;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.CreateRoteiroActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.RoteiroDetailsActivity;

/**
 * Created by André on 11/09/2017.
 */

public class LocalListFragment extends Fragment {

    private static final String TAG = "localListFragment";

    private LocalAdapter adapter;
    public List<Local> listaLocais;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_list, container, false);
        view.setTag(TAG);

        RecyclerView locaisRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentLocalRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        locaisRecyclerView.setLayoutManager(layout);

        container.setVisibility(View.GONE);
        listaLocais = new ArrayList<>();
        adapter = new LocalAdapter(listaLocais, getActivity());
        locaisRecyclerView.setAdapter(adapter);

        bundle = getArguments();
        if(bundle.getString("acao").equals("pesquisaLocal")){
            List<Local> locais = LocalSearchFragment.getLocalList();
            adapter = new LocalAdapter(locais, getActivity());
            locaisRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if(!locais.isEmpty()){
                container.setVisibility(View.VISIBLE);
            }
        }else if(bundle.getString("acao").equals("consultaLocaisRoteiroAtual")){
            LocalDetailsActivity.setConsultando(false);
            container.setVisibility(View.VISIBLE);
            listaLocais.clear();
            if(CreateRoteiroActivity.getListaLocaisRoteiroAtual() != null) {
                listaLocais.addAll(CreateRoteiroActivity.getListaLocaisRoteiroAtual());
                adapter.notifyDataSetChanged();
            }
        }else if(bundle.getString("acao").equals("consultaLocaisBanco")){
            adapter = new LocalAdapter(RoteiroDetailsActivity.getListaLocais(), getActivity());
            locaisRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            container.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bundle.getString("acao").equals("consultaLocaisRoteiroAtual")) {
            listaLocais.clear();
            if(CreateRoteiroActivity.getListaLocaisRoteiroAtual() != null) {
                listaLocais.addAll(CreateRoteiroActivity.getListaLocaisRoteiroAtual());
                adapter.notifyDataSetChanged();
            }
        }
    }
}