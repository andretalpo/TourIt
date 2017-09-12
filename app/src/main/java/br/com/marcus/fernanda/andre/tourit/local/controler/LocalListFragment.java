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

/**
 * Created by André on 11/09/2017.
 */

public class LocalListFragment extends Fragment {

    private static final String TAG = "localListFragment";

    private RecyclerView locaisRecyclerView;
    private LocalAdapter adapter;
    public List<Local> listaLocais;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        String pesquisa = null;
        if(bundle != null){
            pesquisa = bundle.getString("pesquisa");
        }

        Local local = new Local();
        local.setNome(pesquisa);
        local.setNota(5f);
        listaLocais = new ArrayList<>();
        listaLocais.add(local);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_list, container, false);
        view.setTag(TAG);

        locaisRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentLocalRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        locaisRecyclerView.setLayoutManager(layout);

        adapter = new LocalAdapter(listaLocais, getActivity());
        locaisRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
