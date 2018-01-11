package br.com.marcus.fernanda.andre.tourit.roteiro.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GoogleDirectionsServices;
import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalDetailsActivity;
import br.com.marcus.fernanda.andre.tourit.local.controler.LocalListFragment;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.local.model.LocalService;
import br.com.marcus.fernanda.andre.tourit.main.MainActivity;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.Roteiro;
import br.com.marcus.fernanda.andre.tourit.roteiro.model.RoteiroService;
import br.com.marcus.fernanda.andre.tourit.usuario.model.UsuarioService;
import br.com.marcus.fernanda.andre.tourit.utilitarios.ImageConverter;

import static br.com.marcus.fernanda.andre.tourit.R.id.alterarRoteiroDetailsActivityImageView;

public class RoteiroDetailsActivity extends AppCompatActivity implements OnMapReadyCallback{

    ProgressDialog progressDialog;
    private GoogleMap map;
    private Roteiro roteiro;
    private static List<Local> listaLocais;
    private List<Polyline> polylinePaths;
    private ImageView seguirRoteiroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roteiro_details);
        listaLocais = new ArrayList<>();

        TextView nomeRoteiroTextView = (TextView) findViewById(R.id.nomeRoteiroRoteiroDetailsTextView);
        TextView nomeCriadorTextView = (TextView) findViewById(R.id.nomeCriadorRoteiroDetailsTextView);
        TextView tipoRoteiroTextView = (TextView) findViewById(R.id.tipoRoteiroRoteiroDetailsTextView);
        RatingBar roteiroRatingBar = (RatingBar) findViewById(R.id.avaliacaoRoteiroRoteiroDetailsRatingBar);
        ImageView roteiroImageView = (ImageView) findViewById(R.id.imagemRoteiroDetailsActivity);
        TextView duracaoTextView = (TextView) findViewById(R.id.duracaoRoteiroRoteiroDetailsTextView);
        TextView precoTextView = (TextView) findViewById(R.id.precoRoteiroRoteiroDetailsTextView);
        TextView dicasTextView = (TextView) findViewById(R.id.dicasRoteiroRoteiroDetailsTextView);

        roteiro = (Roteiro) getIntent().getSerializableExtra("roteiro");

        roteiro.setImagemRoteiro(ImageConverter.convertByteToBitmap(getIntent().getByteArrayExtra("imagemRoteiro")));
        roteiroImageView.setImageBitmap(roteiro.getImagemRoteiro());

        ImageView excluirButton = (ImageView) findViewById(R.id.excluirRoteiroDetailsActivityImageView);
        excluirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RoteiroDetailsActivity.this, R.style.DialogTheme).setMessage(R.string.mensagem_excluir);

                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new ExcluirRoteiroTask().execute(roteiro);
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        ImageView alterarButton = (ImageView) findViewById(alterarRoteiroDetailsActivityImageView);
        alterarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterarRoteiro(roteiro);
            }
        });

        ImageView publicarRoteiroButton = (ImageView) findViewById(R.id.publicarRoteiroDetailsActivityImageView);
        if(roteiro.isPublicado()){
            publicarRoteiroButton.setVisibility(View.GONE);
        } else{
            publicarRoteiroButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RoteiroDetailsActivity.this, R.style.DialogTheme).setMessage(R.string.mensagem_publicar);

                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            roteiro.setPublicado(true);
                            new PublicarRoteiroTask().execute(roteiro);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            });
        }

        seguirRoteiroButton = (ImageView) findViewById(R.id.seguirRoteiroDetailsActivityImageView);
        seguirRoteiroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RoteiroDetailsActivity.this, R.style.DialogTheme).setMessage(R.string.mensagem_seguir);

                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new SeguirRoteiroTask().execute(roteiro);
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        if(roteiro.isSeguido()){
            alterarButton.setVisibility(View.GONE);
            excluirButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RoteiroDetailsActivity.this, R.style.DialogTheme).setMessage(R.string.mensagem_deixar_de_seguir);

                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new ExcluirRoteiroSeguidoTask().execute(roteiro);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            });
        }

        if(roteiro.getIdRoteiroSqlite() == null){//carrega locais da api - roteiros publicados
            alterarButton.setVisibility(View.GONE);
            excluirButton.setVisibility(View.GONE);
            new CarregarLocaisTask().execute(roteiro);
        }else{//carrega locais do sqlite - meus roteiros
            carregarLocaisRoteiroBanco(roteiro.getIdRoteiroSqlite());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            LocalListFragment localFragment = new LocalListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("acao", "consultaLocaisBanco");
            localFragment.setArguments(bundle);

            transaction.replace(R.id.listaLocaisRoteiroDetailsFrameLayout, localFragment);
            transaction.commit();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRoteiroDetails);
            mapFragment.getMapAsync(this);
        }

        nomeRoteiroTextView.setText(roteiro.getNomeRoteiro());
        nomeCriadorTextView.setText(roteiro.getCriadorRoteiro());
        tipoRoteiroTextView.setText(roteiro.getTipoRoteiro());
        roteiroRatingBar.setRating(roteiro.getNotaRoteiro());
        duracaoTextView.setText(String.valueOf(roteiro.getDuracao()) + " h");
        precoTextView.setText("Até R$ " + String.valueOf(roteiro.getPreco()));
        dicasTextView.setText(roteiro.getDicas());

        ImageView mapaToggle = (ImageView) findViewById(R.id.toggleMapRoteiroDetailsImageView);
        final FrameLayout mapaLayout = (FrameLayout) findViewById(R.id.mapaRotaRoteiroDetailsFrameLayout);
        mapaToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapaLayout.getVisibility()!= View.GONE) {
                    mapaLayout.setVisibility(View.GONE);
                }else{
                    mapaLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void alterarRoteiro(Roteiro roteiro){
        Intent intent = new Intent(this, CreateRoteiroActivity.class);
        intent.putExtra("roteiro", roteiro);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalDetailsActivity.setConsultando(false);
    }

    private class ExcluirRoteiroTask extends AsyncTask<Roteiro, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RoteiroDetailsActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.excluindo_roteiro));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).excluirRoteiro(roteiro [0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            Toast.makeText(RoteiroDetailsActivity.this, getResources().getString(R.string.roteiro_excluido_sucesso), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            RoteiroDetailsActivity.this.finish();
        }
    }

    private class ExcluirRoteiroSeguidoTask extends AsyncTask<Roteiro, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RoteiroDetailsActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.excluindo_roteiro));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).excluirRoteiroSeguido(roteiro [0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            Toast.makeText(RoteiroDetailsActivity.this, getResources().getString(R.string.roteiro_excluido_sucesso), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            RoteiroDetailsActivity.this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private class PublicarRoteiroTask extends AsyncTask<Roteiro, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RoteiroDetailsActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.plubicando_roteiro));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Roteiro... roteiro) {
            new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).publicarRoteiro(roteiro[0].getIdRoteiroFirebase());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            progressDialog.dismiss();
            if (sucesso){
                Toast.makeText(RoteiroDetailsActivity.this, getResources().getString(R.string.roteiro_publicado_sucesso), Toast.LENGTH_SHORT).show();
                finish();
            } else{
                Toast.makeText(RoteiroDetailsActivity.this, getResources().getString(R.string.falha_publicar_roteiro), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalDetailsActivity.setConsultando(true);
    }

    private void carregarLocaisRoteiroBanco(Long idRoteiro) {
        List<Local> locais = new LocalService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).buscarLocaisRoteiro(idRoteiro);
        listaLocais.clear();

        if(locais != null) {
            listaLocais.addAll(locais);
            LocalDetailsActivity.setConsultando(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        List<Marker> markers = new ArrayList<>();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_32);
        if(listaLocais != null && !listaLocais.isEmpty()) {
            for (Local local : listaLocais) {
                if(local != null) {
                    markers.add(map.addMarker(new MarkerOptions()
                            .position(new LatLng(local.getLat(), local.getLng()))
                            .title(local.getNome()).icon(BitmapDescriptorFactory.fromBitmap(icon))));
                }
            }

            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(true);
            if (markers.size() > 1) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                buscarRota(roteiro.getRota());

                final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 125);

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.roteiroDetailsLinearLayout);
                linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        map.moveCamera(cameraUpdate);
                    }
                });
            } else {
                if(!markers.isEmpty()) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15);
                    map.moveCamera(cameraUpdate);
                }
            }
        }
    }

    public static List<Local> getListaLocais() {
        return listaLocais;
    }

    private void buscarRota(String rota){
        polylinePaths = new ArrayList<>();
        List<LatLng> listaPontos = GoogleDirectionsServices.decodePolyLine(rota);
        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(getResources().getColor(R.color.colorAccent));

        polylineOptions.addAll(listaPontos);
        polylinePaths.add(map.addPolyline(polylineOptions));
    }

    private class CarregarLocaisTask extends AsyncTask<Roteiro, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RoteiroDetailsActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.carregando_locais));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Roteiro... roteiro) {
            for (String local : roteiro[0].getIdLocaisRoteiro()) {
                listaLocais.add(new LocalService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).buscarLocalFirebase(local));
            }
            LocalDetailsActivity.setConsultando(true);
            return null;
        }

        @Override
        protected void onPostExecute(Void nada) {
            progressDialog.dismiss();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            LocalListFragment localFragment = new LocalListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("acao", "consultaLocaisBanco");
            localFragment.setArguments(bundle);

            transaction.replace(R.id.listaLocaisRoteiroDetailsFrameLayout, localFragment);
            transaction.commit();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRoteiroDetails);
            mapFragment.getMapAsync(RoteiroDetailsActivity.this);

            if(listaLocais != null || !listaLocais.isEmpty()) {
                if(listaLocais.get(0) != null) {
                    new InicializarBotaoSeguirTask().execute(roteiro);
                }
            }
        }
    }

    private class InicializarBotaoSeguirTask extends AsyncTask<Roteiro, Void, Roteiro> {
        @Override
        protected Roteiro doInBackground(Roteiro... roteiro) {
            return new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).consultarRoteiro(roteiro[0].getIdRoteiroFirebase());
        }

        @Override
        protected void onPostExecute(Roteiro roteiro) {
            if(roteiro == null){
                seguirRoteiroButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private class SeguirRoteiroTask extends AsyncTask<Roteiro, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RoteiroDetailsActivity.this, R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.seguindo_roteiro));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Roteiro... roteiro) {
            new UsuarioService().adicionarRoteiroSeguidoUsuario(MainActivity.idUsuarioGoogle, roteiro[0].getIdRoteiroFirebase());
            try {
                new RoteiroService(RoteiroDetailsActivity.this, MainActivity.idUsuarioGoogle).salvarRoteiroSeguido(roteiro[0], listaLocais);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nada) {
            progressDialog.dismiss();
            Toast.makeText(RoteiroDetailsActivity.this, getResources().getString(R.string.roteiro_seguido_sucesso), Toast.LENGTH_SHORT).show();
            RoteiroDetailsActivity.this.finish();
        }
    }
}