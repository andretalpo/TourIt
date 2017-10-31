package br.com.marcus.fernanda.andre.tourit.local.controler;


import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import br.com.marcus.fernanda.andre.tourit.R;
import br.com.marcus.fernanda.andre.tourit.api.GooglePlacesServices;
import br.com.marcus.fernanda.andre.tourit.local.model.Local;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pesquisa_locais, container, false);
        view.setTag(TAG);
        localList = new ArrayList<>();

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
            progressDialog = ProgressDialog.show(LocalSearchFragment.this.getContext(), "Pesquisando local.", "Aguarde", true, false);
        }

        @Override
        protected List<Local> doInBackground(String... pesquisa) {
            return GooglePlacesServices.buscarLocais(pesquisa[0]);
        }

        @Override
        protected void onPostExecute(List<Local> locais) {
            progressDialog.dismiss();
            if(locais != null){
                if(!locais.isEmpty()) {
                    localList.clear();
                    localList.addAll(locais);
                    List<Marker> markers = new ArrayList<>();
                    for (Local local : localList) {
                        markers.add(map.addMarker(new MarkerOptions()
                                .anchor(0.0f, 1.0f)
                                .position(new LatLng(local.getLat(), local.getLng()))
                                .title(local.getNome())));
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public static List<Local> getLocalList() {
        return localList;
    }

}

