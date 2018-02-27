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
import br.com.marcus.fernanda.andre.tourit.evento.model.Evento;
import br.com.marcus.fernanda.andre.tourit.evento.model.EventoAdapter;
import br.com.marcus.fernanda.andre.tourit.evento.model.EventoService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;

/**
 * Created by André on 27/02/2018.
 */

public class EventoListFragment extends Fragment {
    private static final String TAG = "eventoListFragment";

    private List<Evento> listEventos;
    private Bundle bundle;
    private EventoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        view.setTag(TAG);

        RecyclerView eventosRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentListRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        eventosRecyclerView.setLayoutManager(layout);

        listEventos = new ArrayList<>();

        adapter = new EventoAdapter(listEventos, getActivity());
        eventosRecyclerView.setAdapter(adapter);

        bundle = getArguments();
        if(bundle.getString("tipoEvento").equals("meusEventos")){
            carregarMeusEventos();
        }else if(bundle.getString("tipoEvento").equals("convites")){

        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void carregarMeusEventos() {
        List<Evento> listaMeusEventos = new EventoService(EventoListFragment.this.getContext(), MainActivity.idUsuarioGoogle).consultarMeusEventos();
        listEventos.clear();

        if(listaMeusEventos != null){
            listEventos.addAll(listaMeusEventos);
        }
        adapter.notifyDataSetChanged();
    }
}
