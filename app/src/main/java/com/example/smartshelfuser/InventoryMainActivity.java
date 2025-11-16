package com.example.smartshelfuser;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InventoryMainActivity extends AppCompatActivity {

    private static final String TAG = "InventoryMainActivity";
    private RecyclerView inventoryRecyclerView;
    private FetchCategoryAdapter categoryAdapter;
    private List<FetchCategory> categoryList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory_main);

//        toolbar = findViewById(R.id.toolbarCategoriesList);
//        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Categories");
        }


        mAuth = FirebaseAuth.getInstance();

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        categoryAdapter = new FetchCategoryAdapter(this, categoryList);
        inventoryRecyclerView.setAdapter(categoryAdapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Point the database reference to the user's specific categories node
            databaseReference = FirebaseDatabase.getInstance().getReference("Categories").child(userId);
            fetchInventoryData();
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchInventoryData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    List<FetchCategoryProduct> productList = new ArrayList<>();

                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        FetchCategoryProduct product = productSnapshot.getValue(FetchCategoryProduct.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }

                    // Add the populated category to our main list
                    if (!productList.isEmpty()) {
                        categoryList.add(new FetchCategory(categoryName, productList , true));
                    }
                }
                categoryAdapter.notifyDataSetChanged();

                if (categoryList.isEmpty()) {
                    Toast.makeText(InventoryMainActivity.this, "No inventory items found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                Toast.makeText(InventoryMainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}