package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import br.com.marcus.fernanda.andre.tourit.evento.model.EventoService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;

/**
 * Created by André on 14/02/2018.
 */

public class ConviteListFragment extends Fragment {
    private static final String TAG = "conviteListFragment";

    private BroadcastReceiver broadcastReceiver;
    private List<Convite> convites;
    private ConviteAdapter adapter;
    private Bundle bundle;
    private RecyclerView convitesRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        view.setTag(TAG);

        convitesRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentListRecyclerView);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        convitesRecyclerView.setLayoutManager(layout);

        convites = new ArrayList<>();

        adapter = new ConviteAdapter(convites, getActivity());
        convitesRecyclerView.setAdapter(adapter);

        registrarBroadcastReceiver();

        bundle = getArguments();
        if(bundle.getString("acao").equals("consulta")) {
            convites.addAll(new EventoService(getContext(), MainActivity.idUsuarioGoogle).consultarConvitesEvento(bundle.getString("idEvento")));
            adapter.notifyDataSetChanged();
        } else if(bundle.getString("acao").equals("criacao")){
            convites.addAll(CreateEventActivity.getListaConvidadosEvento());
            adapter.notifyDataSetChanged();
        }

        return view;
    }

    private void registrarBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                onResume();
            }
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("atualizarAdapter"));
    }

    @Override
    public void onResume() {
        super.onResume();
        convites.clear();
        if(bundle.getString("acao").equals("criacao")){
            convites.addAll(CreateEventActivity.getListaConvidadosEvento());
        }else if(bundle.getString("acao").equals("consulta")){
            convites.addAll(new EventoService(getContext(), MainActivity.idUsuarioGoogle).consultarConvitesEvento(bundle.getString("idEvento")));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
