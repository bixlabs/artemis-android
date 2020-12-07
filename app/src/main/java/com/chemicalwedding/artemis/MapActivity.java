package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bundle = this.getIntent().getExtras();
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");

        ((TextView) findViewById(R.id.txt_latitude)).setText(String.format("%.2f", latitude));
        ((TextView) findViewById(R.id.txt_longitude)).setText(String.format("%.2f", longitude));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.use_current_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(true);
                criteria.setBearingRequired(true);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_LOW);

                String locationProvider = locationManager.getBestProvider(criteria, true);
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                mMap.clear();
                LatLng currentPosition = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                ((TextView) findViewById(R.id.txt_latitude)).setText(String.format("%.2f", lastKnownLocation.getLatitude()));
                ((TextView) findViewById(R.id.txt_longitude)).setText(String.format("%.2f", lastKnownLocation.getLongitude()));
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
                mMap.addMarker(new MarkerOptions().position(currentPosition).title("Current location"));
                mMap.moveCamera(CameraUpdateFactory.zoomBy(7));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
            }
        });

        findViewById(R.id.map_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.save_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnLocation();
                finish();
            }
        });
    }

    private void returnLocation() {
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        ((TextView) findViewById(R.id.txt_latitude)).setText(String.format("%.2f", latLng.latitude));
        ((TextView) findViewById(R.id.txt_longitude)).setText(String.format("%.2f", latLng.longitude));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(7));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
