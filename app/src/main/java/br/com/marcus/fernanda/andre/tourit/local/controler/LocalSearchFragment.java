package br.com.marcus.fernanda.andre.tourit.local.controler;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.marcus.fernanda.andre.tourit.R;

/**
 * Created by André on 30/09/2017.
 */

public class LocalSearchFragment extends Fragment implements OnMapReadyCallback{

    private View view;
    private static final String TAG = "fragmentPesquisaLocais";
    private SearchView searchView;
    private GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pesquisa_locais, container, false);
        view.setTag(TAG);

        searchView = (SearchView) view.findViewById(R.id.buscaLocaisSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                LocalListFragment localFragment = new LocalListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("acao", "pesquisaLocal");
                bundle.putString("pesquisa", query);
                localFragment.setArguments(bundle);

                transaction.replace(R.id.localFragmentBuscaLocais, localFragment);
                transaction.commit();

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
        map.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f)
                .position(new LatLng(0,0))
                .title("local"));
        map.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), 1);
        map.moveCamera(cameraUpdate);
    }
}

