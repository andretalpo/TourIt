package br.com.marcus.fernanda.andre.tourit.evento.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.evento.controler.EventoDetailsActivity;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

/**
 * Created by André on 27/02/2018.
 */

public class EventoAdapter extends RecyclerView.Adapter {

    private List<Evento> eventos;
    private Context context;

    public EventoAdapter(List<Evento> eventos, Context context) {
        this.eventos = eventos;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_evento, parent, false);
        EventoViewHolder viewHolder = new EventoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final EventoViewHolder eventoHolder = (EventoViewHolder) holder;
        final Evento evento = eventos.get(position);
        Roteiro roteiro = new RoteiroService(context, MainActivity.idUsuarioGoogle).consultarRoteiro(evento.getIdRoteiroFirebase());
        eventoHolder.fotoRoteiroEventoImageView.setImageBitmap(roteiro.getImagemRoteiro());
        eventoHolder.nomeEventoTextView.setText(evento.getNomeEvento());
        eventoHolder.nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        eventoHolder.dataHoraEventoTextView.setText(evento.getDataEvento() + " - " + evento.getHoraInicio() + " a " + evento.getHoraFim());
        eventoHolder.cardEventoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventoDetailsActivity.class);
                intent.putExtra("idEvento", evento.getIdEventoFirebase());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
