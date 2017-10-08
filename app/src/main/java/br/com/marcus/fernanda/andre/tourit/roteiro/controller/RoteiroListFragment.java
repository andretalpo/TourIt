package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

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
    private RecyclerView roteirosRecyclerView;
    private RoteiroAdapter adapter;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roteiro_list, container, false);
        view.setTag(TAG);

        roteirosRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentRoteiroRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        roteirosRecyclerView.setLayoutManager(layout);

        listaRoteiros = new ArrayList<>();

        adapter = new RoteiroAdapter(listaRoteiros, getActivity());
        roteirosRecyclerView.setAdapter(adapter);

        bundle = getArguments();
        if(bundle.getString("tipoRoteiro").equals("meusRoteiros")) {
            carregarMeusRoteirosBanco();
        }

        return view;
    }

    private void carregarMeusRoteirosBanco() {
        List<Roteiro> listaMeusRoteiros = new RoteiroService(RoteiroListFragment.this.getContext(), MainActivity.idUsuarioGoogle).consultarMeusRoteiros();
        listaRoteiros.clear();

        if(listaMeusRoteiros != null){
            listaRoteiros.addAll(listaMeusRoteiros);
        }
        adapter.notifyDataSetChanged();
    }

    public static Fragment newInstance(String tipoRoteiro){
        Fragment fragment = new RoteiroListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tipoRoteiro", tipoRoteiro);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarMeusRoteirosBanco();
    }
}
