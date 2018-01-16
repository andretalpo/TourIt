package br.com.marcus.fernanda.andre.tourit.local.controler;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class LocalSearchFragment extends Fragment implements OnMapReadyCallback {

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
    private android.location.LocationManager locationManager;
    private Location currentLocation = null; //PODE DAR NULLPOINTER
    private static final int REQUEST_GPS = 1;
    private Marker marker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pesquisa_locais, container, false);
        view.setTag(TAG);
        localList = new ArrayList<>();
        distanciaBoolean = false;
        distanciaFiltro = 15;
        abertoAgora = false;

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
                if (distanciaSwitch.isChecked()) {
                    distanciaSeekBar.setEnabled(true);
                }

                distanciaKm.setText(distanciaFiltro + "Km");

                distanciaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            distanciaSeekBar.setEnabled(true);
                            distanciaBoolean = true;
                        } else {
                            distanciaSeekBar.setEnabled(false);
                            distanciaBoolean = false;
                        }
                    }
                });
                distanciaSeekBar.setProgress(distanciaFiltro);
                distanciaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        distanciaKm.setText(progress + " Km");
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
        EditText editTextSV = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editTextSV.setTextColor(getResources().getColor(R.color.secondary_text_material_light));
        editTextSV.setHintTextColor(getResources().getColor(R.color.secondary_text_material_light));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                map.clear();
                new CarregarLocaisApiTask().execute(query);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
            progressDialog = new ProgressDialog(LocalSearchFragment.this.getContext(), R.style.ProgressTheme);
            progressDialog.setTitle(getResources().getString(R.string.pesquisando_local));
            progressDialog.setMessage(getResources().getString(R.string.aguarde));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<Local> doInBackground(String... pesquisa) {

            if (abertoAgora) {
                if (distanciaBoolean) {
                    LatLng latLng;
                    if (CreateRoteiroActivity.getListaLocaisRoteiroAtual() != null){
                        if(!CreateRoteiroActivity.getListaLocaisRoteiroAtual().isEmpty()) {
                            Local local = CreateRoteiroActivity.getListaLocaisRoteiroAtual().get(CreateRoteiroActivity.getListaLocaisRoteiroAtual().size() - 1);
                            latLng = new LatLng(local.getLat(), local.getLng());
                        } else {
                            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        }
                    } else {
                        latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    }
                    return GooglePlacesServices.buscarLocais(pesquisa[0], distanciaFiltro * 1000, abertoAgora, latLng);
                }
                return GooglePlacesServices.buscarLocais(pesquisa[0], abertoAgora);
            } else if (distanciaBoolean) {
                LatLng latLng;
                if (CreateRoteiroActivity.getListaLocaisRoteiroAtual() != null){
                    if(!CreateRoteiroActivity.getListaLocaisRoteiroAtual().isEmpty()) {
                        Local local = CreateRoteiroActivity.getListaLocaisRoteiroAtual().get(CreateRoteiroActivity.getListaLocaisRoteiroAtual().size() - 1);
                        latLng = new LatLng(local.getLat(), local.getLng());
                    } else {
                        latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    }
                } else {
                    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                }
                return GooglePlacesServices.buscarLocais(pesquisa[0], distanciaFiltro * 1000, latLng);
            }
            return GooglePlacesServices.buscarLocais(pesquisa[0]);
        }

        @Override
        protected void onPostExecute(List<Local> locais) {
            progressDialog.dismiss();
            if (locais != null) {
                localList.clear();
                if (!locais.isEmpty()) {
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
                } else {
                    Toast.makeText(LocalSearchFragment.this.getContext(), getResources().getString(R.string.nenhum_resultado_pesquisa), Toast.LENGTH_SHORT).show();
                }
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                LocalListFragment localFragment = new LocalListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("acao", "pesquisaLocal");
                localFragment.setArguments(bundle);

                transaction.replace(R.id.localFragmentBuscaLocais, localFragment);
                transaction.commit();
            } else {
                Toast.makeText(LocalSearchFragment.this.getContext(), getResources().getString(R.string.nenhum_resultado_pesquisa), Toast.LENGTH_SHORT).show();
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
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        solicitarPermissaoGPS();
    }

    public static List<Local> getLocalList() {
        return localList;
    }

    private void solicitarPermissaoGPS() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            }
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        } else {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                map.moveCamera(cameraUpdate);
                if (marker != null) {
                    marker.remove();
                }
                marker = map.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
//            map.moveCamera(cameraUpdate);
            if(marker != null){
                marker.remove();
            }
            marker = map.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[ ] permissions, @NonNull int[ ] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        map.moveCamera(cameraUpdate);
                        if(marker != null){
                            marker.remove();
                        }
                        marker = map.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    }
                }
                break;
        }
    }

}

