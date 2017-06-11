package com.example.mac.recipez.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.recipez.MapsActivity;
import com.example.mac.recipez.R;
import com.example.mac.recipez.UserRecipe;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by julia on 07-Jun-17.
 */

public class SupermarketMap extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private Button btnFindRest;
    private TextView tvInro;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private GoogleMap myGoogleMap;
    private GoogleApiClient googleApiClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurants_finder, viewGroup, false);
        btnFindRest = (Button) view.findViewById(R.id.buttonFindRest);
        tvInro = (TextView) view.findViewById(R.id.textViewRestIntro);
        getMap();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myGoogleMap = googleMap;
    }

    private void getMap() {
        btnFindRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }


    //Parsing the json result from google
    public List<HashMap<String, String>> parseJSONResult(JSONObject jsonResult){
        Log.w("inside parseJson", "top parseJSON");
        JSONArray places = null;
        try {
            places = jsonResult.getJSONArray("results");
        } catch (JSONException e){
            e.printStackTrace();
        }
        Log.e("JSONARRAY", places.toString());
        return getSupermarkets(places);
    }

    //Getting all the supermarkets from the Json array
    private List<HashMap<String,String>> getSupermarkets(JSONArray places) {
        int numPlacesFound = places.length();
        List<HashMap<String, String>> supermarkets = new ArrayList<>();
        HashMap<String, String> supermarket = null;

        for (int i = 0; i < numPlacesFound; i++) {
            try {
                supermarket = getSupermarket((JSONObject) places.get(i));
                supermarkets.add(supermarket);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return supermarkets;
    }

    //Getting the information of each supermarket
    private HashMap<String, String> getSupermarket(JSONObject jSupermarket){
        String lat = "";
        String lng = "" ;
        String name = "";

        HashMap<String, String> supMrk = new HashMap<>();

        try{
            if(!jSupermarket.isNull("name")){
                name = jSupermarket.getString("name");
            }

            lat = jSupermarket.getJSONObject("geometry").getJSONObject("location").getString("lat");
            lng = jSupermarket.getJSONObject("geometry").getJSONObject("location").getString("lng");

            supMrk.put("name", name);
            supMrk.put("lat", lat);
            supMrk.put("lng", lng);

        } catch (JSONException e){
            e.printStackTrace();
        }
        Log.e("supermarket: ", supMrk.toString());
        return supMrk;
    }


//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.restaurants_finder, viewGroup, false);
//        mapView = (MapView) view.findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//
//        mapView.onResume();
//
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mapView.getMapAsync(this);
////        map.setMyLocationEnabled(true);
////        location = map.getMyLocation();
//
//

//
//        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                //Called whenever location is updated
//                final double latitude = location.getLatitude();
//                final double longitude = location.getLongitude();
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//        //(gps, time to refresh in ms, float minDistance, LocationListener
////        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
////            return TODO;
////        } else {
////            changeButton();
//
////        }
//        getMap();
//        return view;
//    }
//
//    private void changeButton() {
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            map.setMyLocationEnabled(true);
//        } else {
//            Toast.makeText(getContext(), "Please allow Location Service in order to use this feature", Toast.LENGTH_LONG).show();
//        }
//        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//        setUpMap();
//    }
//
//    private void setUpMap() {
//        map.getUiSettings().setZoomControlsEnabled(true);
//    }
//
//
}
