package com.example.mac.recipez;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location myLocation;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private Marker myLocationMarker;
    private final int permission_access_code = 10;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2 * 1000; /* 2 sec */

    private double latitude, longitude;

    private Button btnGetSupermarket;

    private final String API_KEY = "AIzaSyBg1Yh-rF4Ps09BBc_yMSwhcGiD2eJtytU";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_maps);
        btnGetSupermarket = (Button) findViewById(R.id.btnGetSupermarket);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "please allow gps service for us to get your current location", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Helloooo", Toast.LENGTH_LONG).show();
//        mMap.setMyLocationEnabled(true);
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String bestProvider = locationManager.getBestProvider(criteria, true);
//        myLocation = locationManager.getLastKnownLocation(bestProvider); //TODO:error
//
//        if(myLocation != null){
//            onLocationChanged(myLocation);
//        }
//        locationManager.requestLocationUpdates(bestProvider, 20*1000, 0, locationListener);
//
        btnGetSupermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                sb.append("?location="+latitude+","+longitude);
                sb.append("&radius=500");
                sb.append("&types=supermarket");
                sb.append("&sensor=true");
                sb.append("&key="+API_KEY);


                GetGooglePlaces googlePlaces = new GetGooglePlaces(mMap);
                Log.e("string builder", sb.toString());
                googlePlaces.execute(sb.toString());
            }
        });

    }


    protected void onStart() {
        super.onStart();
        // Connect the client.
        googleApiClient.connect();
    }

    protected void onStop() {
        // only stop if it's connected, otherwise we crash
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //         Add a marker in Huxley and move the camera
        LatLng huxley = new LatLng(51.498903, -0.178993);
        mMap.addMarker(new MarkerOptions().position(huxley).title("Marker in huxley building"));
        float zoom = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huxley, zoom ));
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
         myLocation = location;
        if(myLocationMarker != null){
            myLocationMarker.remove();
        }

        LatLng currentLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        myLocationMarker = mMap.addMarker(markerOptions);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, permission_access_code);
            }
            return;
        }
        mMap.setMyLocationEnabled(true);
        myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(myLocation != null){
            LatLng currentLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            myLocationMarker = mMap.addMarker(markerOptions);
        } else {
            locationRequest = locationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(UPDATE_INTERVAL)
                                .setFastestInterval(FASTEST_INTERVAL);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Play services connection suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Play services connection failed", Toast.LENGTH_LONG).show();

    }

}
