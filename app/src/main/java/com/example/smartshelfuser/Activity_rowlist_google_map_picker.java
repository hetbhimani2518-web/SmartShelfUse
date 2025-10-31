package com.example.smartshelfuser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@UnstableApi
public class Activity_rowlist_google_map_picker extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapPickerActivity";

    public static final String EXTRA_PICKED_LATITUDE = "EXTRA_PICKED_LATITUDE";
    public static final String EXTRA_PICKED_LONGITUDE = "EXTRA_PICKED_LONGITUDE";
    public static final String EXTRA_PICKED_ADDRESS_NAME = "EXTRA_PICKED_ADDRESS_NAME";
    public static final String EXTRA_INITIAL_SEARCH_TERM = "EXTRA_INITIAL_SEARCH_TERM";

    private GoogleMap mMap;
    private Marker currentMarker;
    private LatLng selectedLatLng;
    private String selectedAddress;

    private Button buttonConfirmLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rowlist_google_map_picker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        buttonConfirmLocation= findViewById(R.id.buttonConfirmLocation);
        buttonConfirmLocation.setOnClickListener(v -> returnSelectedLocation());

        // You can add a search button/icon that calls this:
        // startPlaceAutocomplete(); // Or trigger this based on user input
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Default location (India Gate, Delhi)
        LatLng defaultLocation = new LatLng(28.6129, 77.2295);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14));

        // Allow user to tap to select location
        mMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;

            if (currentMarker != null) currentMarker.remove();

            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Try to fetch address
            selectedAddress = getAddressFromLatLng(latLng);
            if (selectedAddress != null) {
                Toast.makeText(this, "Selected: " + selectedAddress, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnSelectedLocation() {
        if (selectedLatLng == null) {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_PICKED_LATITUDE, selectedLatLng.latitude);
        result.putExtra(EXTRA_PICKED_LONGITUDE, selectedLatLng.longitude);
        result.putExtra(EXTRA_PICKED_ADDRESS_NAME, selectedAddress != null ? selectedAddress : "");
        setResult(RESULT_OK, result);
        finish();
    }

    private String getAddressFromLatLng(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}