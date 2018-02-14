package br.com.marcus.fernanda.andre.tourit.evento.controler;

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
import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;
import br.com.marcus.fernanda.andre.tourit.evento.model.ConviteAdapter;

/**
 * Created by André on 14/02/2018.
 */

public class ConviteListFragment extends Fragment {
    private static final String TAG = "conviteListFragment";

    private List<Convite> convites;
    private ConviteAdapter adapter;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        view.setTag(TAG);

        RecyclerView roteirosRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentListRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        roteirosRecyclerView.setLayoutManager(layout);

        convites = new ArrayList<>();

        adapter = new ConviteAdapter(convites, getActivity());
        roteirosRecyclerView.setAdapter(adapter);

        bundle = getArguments();
        if(bundle.getString("acao").equals("consulta")) {
//            carregarMeusRoteirosBanco();
        } else if(bundle.getString("acao").equals("criacao")){
//            new PesquisaRoteirosTask().execute(bundle.getString("pesquisa"));
        }

        return view;
    }
}
