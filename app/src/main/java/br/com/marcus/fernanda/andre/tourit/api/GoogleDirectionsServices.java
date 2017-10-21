package br.com.marcus.fernanda.andre.tourit.api;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by André on 21/10/2017.
 */

public class GoogleDirectionsServices {

    public static List<LatLng> criarRota(List<LatLng> listaLocais) {
        URL url = null;
        String waypoints = "";
        if(listaLocais.size() > 2) {
            waypoints += "waypoints=optimize:true|" + listaLocais.get(1).latitude + "," + listaLocais.get(1).longitude;
            for (int i = 2; i < listaLocais.size() - 1; i++) {
                waypoints += "|" + listaLocais.get(i).latitude + "," + listaLocais.get(i).longitude;
            }
            waypoints += "&";
        }
        try {
            url = new URL("https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyAZdDBDb_NfnoqH2Q2SnyL_wE5Ns7YMmr4&"
                    + "origin=" + listaLocais.get(0).latitude + "," + listaLocais.get(0).longitude + "&"
                    + waypoints + "destination=" + listaLocais.get(listaLocais.size()-1).latitude + "," + listaLocais.get(listaLocais.size()-1).longitude);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK){
                StringBuilder builder = new StringBuilder ();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                    String line;
                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                return convertJSONToRota(new JSONObject(builder.toString()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    private static List<LatLng> convertJSONToRota(JSONObject jsonObject) {
        try {
            JSONArray jsonRoutes = jsonObject.getJSONArray("routes");
            String polyline = "";
            for(int i = 0; i < jsonRoutes.length(); i++){
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                JSONObject overview_polyline = jsonRoute.getJSONObject("overview_polyline");
                JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                JSONObject jsonLeg = jsonLegs.getJSONObject(0);

                polyline = overview_polyline.getString("points");
            }

            return decodePolyLine(polyline);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
