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
import br.com.marcus.fernanda.andre.tourit.local.dao.LocalDAO;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalAdapter;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;

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

        Bundle bundle = getArguments();
        String pesquisa;
        if(bundle != null){
            pesquisa = bundle.getString("pesquisa");
            new CarregarLocaisApiTask().execute(pesquisa);
        }else {
            carregarLocaisBanco();
        }

        return view;
    }

    private void carregarLocaisBanco() {
        List<Local> locais = new LocalDAO(getContext(), MainActivity.idUsuarioGoogle).buscarLocaisUsuario();

        if(locais != null){
            listaLocais.addAll(locais);
            adapter.notifyDataSetChanged();
            container.setVisibility(View.VISIBLE);
        }else{
            container.setVisibility(View.GONE);
            Toast.makeText(LocalListFragment.this.getContext(), "Nenhum resultado para a pesquisa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private class CarregarLocaisApiTask extends AsyncTask<String, Void, List<Local>> {

        @Override
        protected List<Local> doInBackground(String... pesquisa) {
            List<Local> locais = GooglePlacesServices.buscarLocais(pesquisa[0]);
            return locais;
        }

        @Override
        protected void onPostExecute(List<Local> locais) {
            if(locais != null){
                listaLocais.addAll(locais);
                adapter.notifyDataSetChanged();
                container.setVisibility(View.VISIBLE);
            }else{
                container.setVisibility(View.GONE);
                Toast.makeText(LocalListFragment.this.getContext(), "Nenhum resultado para a pesquisa", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
