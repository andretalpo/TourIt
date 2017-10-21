package br.com.marcus.fernanda.andre.tourit.local.controler;

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
import br.com.marcus.fernanda.andre.tourit.local.model.LocalAdapter;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.CreateRoteiroActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.RoteiroDetailsActivity;

/**
 * Created by André on 11/09/2017.
 */

public class LocalListFragment extends Fragment {

    private static final String TAG = "localListFragment";

    private View view;
    private RecyclerView locaisRecyclerView;
    private LocalAdapter adapter;
    public List<Local> listaLocais;
    private View container;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_local_list, container, false);
        view.setTag(TAG);
        this.container = container;

        locaisRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentLocalRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        locaisRecyclerView.setLayoutManager(layout);

        container.setVisibility(View.GONE);
        listaLocais = new ArrayList<>();
        adapter = new LocalAdapter(listaLocais, getActivity());
        locaisRecyclerView.setAdapter(adapter);

        bundle = getArguments();
        String pesquisa;
        if(bundle.getString("acao").equals("pesquisaLocal")){
            adapter = new LocalAdapter(LocalSearchFragment.getLocalList(), getActivity());
            locaisRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            container.setVisibility(View.VISIBLE);
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
//            carregarLocaisRoteiroBanco(bundle.getLong("idRoteiro"));
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
