package br.com.marcus.fernanda.andre.tourit.evento.controler;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.dao.EventoDAO;
import br.com.marcus.fernanda.andre.tourit.evento.model.Convite;
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
    private ProgressDialog progressDialog;
    private List<Evento> eventoLista;

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
        if (bundle.getString("tipoEvento").equals("meusEventos")) {
            new ConsultarEventoSqliteTask().execute();
        } else if (bundle.getString("tipoEvento").equals("convites")) {
            new ConsultarConvitesFirebaseTask().execute();
        }

        return view;
    }

    private void carregarMeusEventos() {
        List<Evento> listaMeusEventos = new EventoService(EventoListFragment.this.getContext(), MainActivity.idUsuarioGoogle).consultarMeusEventos();
        listEventos.clear();

        if (listaMeusEventos != null) {
            listEventos.addAll(listaMeusEventos);
        }
    }

    private class ConsultarEventoSqliteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext(), R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.buscando_evento));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            carregarMeusEventos();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        if(bundle.getString("tipoEvento").equals("meusEventos")) {
            new ConsultarEventoSqliteTask().execute();
        }
        adapter.notifyDataSetChanged();
    }

    private List<Evento> carregarMeusConvites() {
        List<Evento> listaConvites = new EventoService(EventoListFragment.this.getContext(), MainActivity.idUsuarioGoogle).consultarEventosConvidado(MainActivity.idUsuarioGoogle);
        listEventos.clear();

        if (listaConvites != null) {
            listEventos.addAll(listaConvites);
        }
        return listEventos;
    }

    private class ConsultarConvitesFirebaseTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext(), R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.buscando_convites));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            carregarMeusConvites();
            EventoService eventoService = new EventoService(getContext(), MainActivity.idUsuarioGoogle);
            eventoService.excluirEventosConvidadoSqlite(MainActivity.idUsuarioGoogle);
            eventoService.atualizarEventosConvidado(listEventos);
            for(Evento evento: listEventos) {
                for (Convite convite : evento.getConvidados()) {
                    armazenarImagem(convite, evento.getIdEventoFirebase());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listEventos.clear();
            listEventos.addAll(new EventoService(getContext(), MainActivity.idUsuarioGoogle).consultarEventosConvidadoSqlite());
            onResume();
        }
    }

    public void armazenarImagem(final Convite convite, final String idEvento){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imagemUsuario/" + convite.getIdUsuarioGoogleConvidado() + ".jpeg");

        final long ONE_MEGABYTE = 300 * 300;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] imagemConvidado) {
                new EventoService(getContext(), MainActivity.idUsuarioGoogle).salvarImagemConvite(imagemConvidado, convite.getIdUsuarioGoogleConvidado(), idEvento);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
