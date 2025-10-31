package com.example.smartshelfuser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFrg extends Fragment {

    private static final String TAG = "HomeFrg";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView textViewWelcome, tvUserLocation;
    private MaterialCardView cardCreateList, cardSearchLocal, cardCategorizedItems, cardDeletedItems;

    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;

    public HomeFrg() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_frg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        textViewWelcome = view.findViewById(R.id.tvWelcome);
        cardCreateList = view.findViewById(R.id.cardCreateList);
        cardSearchLocal = view.findViewById(R.id.cardSearchLocal);
        cardCategorizedItems = view.findViewById(R.id.cardCategorizedItems);
        cardDeletedItems = view.findViewById(R.id.cardDeletedItems);

        tvUserLocation = view.findViewById(R.id.tvUserLocation);

        if (tvUserLocation != null) {
            tvUserLocation.setVisibility(View.VISIBLE);
        }
        checkAndRequestLocationPermission();

        setWelcomeMessage();
        setupCardClickListeners();
    }

    private void setWelcomeMessage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            if (userName != null && !userName.isEmpty()) {
                textViewWelcome.setText("Welcome, " + userName + "!");
            }
//          else {
//                textViewWelcome.setText("Welcome, " + currentUser.getEmail() + "!");
//          }
        } else {
            textViewWelcome.setText("Welcome, Guest!");
        }
    }

    private void setupCardClickListeners() {
        cardCreateList.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ShoppingListMainActivity.class);
            startActivity(intent);
        });

        cardSearchLocal.setOnClickListener(v ->
                Toast.makeText(getContext(), "Search Locally Clicked", Toast.LENGTH_SHORT).show());

        cardCategorizedItems.setOnClickListener(v ->
                Toast.makeText(getContext(), "Categorized Items Clicked", Toast.LENGTH_SHORT).show());

        cardDeletedItems.setOnClickListener(v ->
                Toast.makeText(getContext(), "Deleted Items Clicked", Toast.LENGTH_SHORT).show());
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (tvUserLocation != null) {
                tvUserLocation.setText(R.string.location_permission_needed);
            }

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (tvUserLocation != null) {
                tvUserLocation.setText(R.string.detecting_location_placeholder);
            }
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (tvUserLocation != null) {
                    tvUserLocation.setText(R.string.detecting_location_placeholder);
                }
                getCurrentLocation();
            } else {
                if (tvUserLocation != null) {
                    tvUserLocation.setText(R.string.location_permission_denied);
                }
                Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (tvUserLocation != null) {
                tvUserLocation.setText(R.string.location_permission_denied);
            }
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        Log.d(TAG, "Location found: " + location.getLatitude() + ", " + location.getLongitude());
                        getAddressFromLocation(location);
                    } else {
                        Log.w(TAG, "Last known location is null.");
                        if (tvUserLocation != null) {
                            tvUserLocation.setText(R.string.unable_to_detect_location);
                        }
                        Toast.makeText(getContext(), "Could not get current location. Please ensure location is enabled.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(requireActivity(), e -> {
                    Log.e(TAG, "Error getting location", e);
                    if (tvUserLocation != null) {
                        tvUserLocation.setText(R.string.error_detecting_location);
                    }
                    Toast.makeText(getContext(), "Error getting location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1,
                        addresses -> processAddresses(addresses, location));
            } else {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                processAddresses(addresses, location);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder IOException", e);
            if (tvUserLocation != null) {
                tvUserLocation.setText(String.format(Locale.getDefault(),
                        "Lat: %.4f, Lng: %.4f", location.getLatitude(), location.getLongitude()));
            }
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void processAddresses(List<Address> addresses, Location location) {
        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            StringBuilder addressText = new StringBuilder();

            if (address.getLocality() != null) {
                addressText.append(address.getLocality());
            }
            if (address.getAdminArea() != null) {
                if (addressText.length() > 0) addressText.append(", ");
                addressText.append(address.getAdminArea());
            }

            if (addressText.length() > 0) {
                tvUserLocation.setText(addressText.toString());
            } else if (address.getAddressLine(0) != null) {
                tvUserLocation.setText(address.getAddressLine(0));
            } else {
                tvUserLocation.setText(String.format(Locale.getDefault(),
                        "Lat: %.4f, Lng: %.4f", location.getLatitude(), location.getLongitude()));
            }

            Log.d(TAG, "Address found: " + addressText);
        } else {
            Log.w(TAG, "No address found for the location.");
            tvUserLocation.setText(String.format(Locale.getDefault(),
                    "Area near Lat: %.2f, Lng: %.2f", location.getLatitude(), location.getLongitude()));
        }
    }

}