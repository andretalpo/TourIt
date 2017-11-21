package br.com.marcus.fernanda.andre.tourit.local.controler;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;
import br.com.marcus.fernanda.andre.tourit.roteiro.controller.CreateRoteiroActivity;

/**
 * Created by André on 30/09/2017.
 */

public class LocalSearchFragment extends Fragment implements OnMapReadyCallback{

    private View view;
    private static final String TAG = "fragmentPesquisaLocais";
    private SearchView searchView;
    private GoogleMap map;
    private static List<Local> localList;
    private ProgressDialog progressDialog;
    private ImageView filtroImageView;
    private int distanciaFiltro;
    private SeekBar distanciaSeekBar;
    private boolean abertoAgora;
    private boolean distanciaBoolean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pesquisa_locais, container, false);
        view.setTag(TAG);
        localList = new ArrayList<>();
        distanciaBoolean = false;
        distanciaFiltro = 15000;
        abertoAgora = false;


        filtroImageView = (ImageView) view.findViewById(R.id.filtroLocalSearchImageView);
        filtroImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View filtroLayout = inflater.inflate(R.layout.dialog_filtro_local, null);
                final TextView distanciaKm = (TextView) filtroLayout.findViewById(R.id.distanciaLabelTextView);

                distanciaSeekBar = (SeekBar) filtroLayout.findViewById(R.id.distanciaSeekBarDialogFiltro);
                Switch distanciaSwitch = (Switch) filtroLayout.findViewById(R.id.distanciaSwitchDialogFiltro);
                distanciaSwitch.setChecked(distanciaBoolean);
                distanciaSeekBar.setEnabled(false);
                if(distanciaSwitch.isChecked()){
                    distanciaSeekBar.setEnabled(true);
                }

                distanciaKm.setText(distanciaFiltro/1000 + "Km");

                distanciaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            distanciaSeekBar.setEnabled(true);
                            distanciaBoolean = true;
                        }else {
                            distanciaSeekBar.setEnabled(false);
                            distanciaBoolean = false;
                        }
                    }
                });
                distanciaSeekBar.setProgress(distanciaFiltro);
                distanciaSeekBar.incrementProgressBy(1000);
                distanciaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        distanciaKm.setText(progress/1000 + " Km");
                        distanciaFiltro = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                Switch abertoAgoraSwitch = (Switch) filtroLayout.findViewById(R.id.abertoSwitchDialogFiltro);
                abertoAgoraSwitch.setChecked(abertoAgora);
                abertoAgoraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        abertoAgora = isChecked;
                    }
                });
                Dialog dialog = new Dialog(getContext());
                dialog.setTitle("Filtro");
                dialog.setContentView(filtroLayout);
                dialog.show();
            }
        });

        searchView = (SearchView) view.findViewById(R.id.buscaLocaisSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                map.clear();
                new CarregarLocaisApiTask().execute(query);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    private class CarregarLocaisApiTask extends AsyncTask<String, Void, List<Local>> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LocalSearchFragment.this.getContext(), "Pesquisando local.", "Aguarde", true, true);
        }

        @Override
        protected List<Local> doInBackground(String... pesquisa) {

            if(abertoAgora){
                if(distanciaBoolean){
                    if(CreateRoteiroActivity.getListaLocaisRoteiroAtual() != null){
                        if(!CreateRoteiroActivity.getListaLocaisRoteiroAtual().isEmpty()){
                            Local local = CreateRoteiroActivity.getListaLocaisRoteiroAtual().get(CreateRoteiroActivity.getListaLocaisRoteiroAtual().size()-1);
                            LatLng latLng = new LatLng(local.getLat(), local.getLng());
                            return GooglePlacesServices.buscarLocais(pesquisa[0], distanciaFiltro, abertoAgora, latLng);
                        }
                    }
                }
                return GooglePlacesServices.buscarLocais(pesquisa[0], abertoAgora);
            }else if(distanciaBoolean){
                if(CreateRoteiroActivity.getListaLocaisRoteiroAtual() != null){
                    if(!CreateRoteiroActivity.getListaLocaisRoteiroAtual().isEmpty()){
                        Local local = CreateRoteiroActivity.getListaLocaisRoteiroAtual().get(CreateRoteiroActivity.getListaLocaisRoteiroAtual().size()-1);
                        LatLng latLng = new LatLng(local.getLat(), local.getLng());
                        return GooglePlacesServices.buscarLocais(pesquisa[0], distanciaFiltro, latLng);
                    }
                }
            }
            return GooglePlacesServices.buscarLocais(pesquisa[0]);
        }

        @Override
        protected void onPostExecute(List<Local> locais) {
            progressDialog.dismiss();
            if(locais != null){
                localList.clear();
                if(!locais.isEmpty()) {
                    localList.addAll(locais);
                    List<Marker> markers = new ArrayList<>();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_32);
                    for (Local local : localList) {
                        map.addMarker(new MarkerOptions()
                                .anchor(0.0f, 1.0f)
                                .position(new LatLng(local.getLat(), local.getLng()))
                                .title(local.getNome()).icon(BitmapDescriptorFactory.fromBitmap(icon)));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                        map.getUiSettings().setZoomControlsEnabled(false);
                    }
                    if (markers.size() > 1) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();


                        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 125);

                        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.mapFragmentContainer);
                        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                map.moveCamera(cameraUpdate);
                            }
                        });
                    } else {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(localList.get(0).getLat(), localList.get(0).getLng()), 10);
                        map.moveCamera(cameraUpdate);
                    }
                }else{
                    Toast.makeText(LocalSearchFragment.this.getContext(), "Nenhum resultado para a pesquisa", Toast.LENGTH_SHORT).show();
                }
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                LocalListFragment localFragment = new LocalListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("acao", "pesquisaLocal");
                localFragment.setArguments(bundle);

                transaction.replace(R.id.localFragmentBuscaLocais, localFragment);
                transaction.commit();
            }else{
                Toast.makeText(LocalSearchFragment.this.getContext(), "Nenhum resultado para a pesquisa", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapBuscaLocais);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchView.clearFocus();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public static List<Local> getLocalList() {
        return localList;
    }

}

