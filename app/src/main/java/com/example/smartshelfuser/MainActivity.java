package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;

    private final HomeFrg homeFrg = new HomeFrg();
    private final SearchItemFrg searchItemFrg = new SearchItemFrg();
    private final ShoppingListFrg shoppingListFrg = new ShoppingListFrg();
    private final ProfileFrg profileFrg = new ProfileFrg();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavView = findViewById(R.id.bottomNavView);

        if (savedInstanceState == null) {
            loadFragment(homeFrg, true);
        }

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId") // This is okay if your IDs are fixed.
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = homeFrg;
                } else if (itemId == R.id.nav_search) {
                    selectedFragment = searchItemFrg;
                } else if (itemId == R.id.nav_list) {
                    selectedFragment = shoppingListFrg;
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = profileFrg;
                } else {
                    Toast.makeText(MainActivity.this, "Failed To load Fragment", Toast.LENGTH_SHORT).show();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment, false);
                    return true;
                }
                return false;
            }
        });

    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.replace(R.id.fragment_container_view, fragment);
        } else {
            fragmentTransaction.replace(R.id.fragment_container_view, fragment);
        }

        fragmentTransaction.commit();
    }
}