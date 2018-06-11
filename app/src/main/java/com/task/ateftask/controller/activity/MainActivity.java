package com.task.ateftask.controller.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.task.ateftask.R;
import com.task.ateftask.model.FacebookUserData;
import com.task.ateftask.util.Constant;
import com.task.ateftask.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.name_TV)
    TextView nameTV;
    @BindView(R.id.locate_btn)
    Button locateBtn;
    @BindView(R.id.lat_ET)
    EditText latET;
    @BindView(R.id.lng_ET)
    EditText lngET;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    private double lat;
    private double lng;
    private LatLng currentUserLatLang;

    private List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        setUpMap(savedInstanceState);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    private void init() {
        FacebookUserData data = getIntent().getExtras().getParcelable(Constant.Extras.USER);

        Picasso.get()
                .load(data.getImageUrl())
                .into(img);

        nameTV.setText(data.getName());

        locateBtn.setOnClickListener(this);
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    private void setUpMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        if (Util.isLocationEnabled(this)) {
            //GET Current Location and set Pin!

//            LatLng sydney = new LatLng(-34, 151);
//            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        } else {
            Util.showGPSDisabledAlertToUser(this);
        }

    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onConnected(Bundle bundle) {
        CurrentLocation();
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    private void CurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (userCurrentLocation != null) {
                MarkerOptions currentUserLocation = new MarkerOptions();
                currentUserLatLang = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                currentUserLocation.position(currentUserLatLang);
                markers.add(mMap.addMarker(currentUserLocation));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 16));
//_______________________________________________________________________________________________________________________________________________________________________________

            }
        }
    }

    private void locateSecondPin() {
        lat = Double.parseDouble(latET.getText().toString().trim());
        lng = Double.parseDouble(lngET.getText().toString().trim());

        MarkerOptions secondLocation = new MarkerOptions();
        LatLng secondLocationLatLang = new LatLng(lat, lng);
        secondLocation.position(secondLocationLatLang);
        markers.add(mMap.addMarker(secondLocation));

        GoogleDirection.withServerKey(Constant.Key.API_KEY)
                .from(currentUserLatLang)
                .to(secondLocationLatLang)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(MainActivity.this, directionPositionList, 5, Color.RED);
                            mMap.addPolyline(polylineOptions);

                            displayAll();
                            Log.e("Direction :", "Direction Success");
                        } else {
                            Log.e("Direction :", direction.getErrorMessage());
                        }
                    }

//____________________________________________________________________________________________________________________________________________________________________________________

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.e("Direction :", "Failed :" + t.getLocalizedMessage());

                    }
                });


    }

    private void displayAll() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onConnectionSuspended(int i) {

    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onConnected(null);
        } else {
            Toast.makeText(MainActivity.this, "No Permitions Granted", Toast.LENGTH_SHORT).show();
        }
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mMap != null)
            mMap.clear();
        mMapView.invalidate();
        mMapView.getMapAsync(this);
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

//____________________________________________________________________________________________________________________________________________________________________________________

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locate_btn:
                locateSecondPin();
                break;
        }
    }


}
