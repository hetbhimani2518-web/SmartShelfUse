package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoriesMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryCountAdapter adapter;
    private List<CategoryCountModel> categoryList = new ArrayList<>();

    private DatabaseReference categoriesRef;

    private Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories_main);

        toolbar = findViewById(R.id.toolbarCategoriesList);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Categories");
        }

        String userId = FirebaseAuth.getInstance().getUid();

        recyclerView = findViewById(R.id.recyclerViewCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryCountAdapter(categoryList);
        recyclerView.setAdapter(adapter);

        categoriesRef = FirebaseDatabase.getInstance()
                .getReference("Categories")
                .child(userId);

        loadCategories();
    }

    private void loadCategories() {
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    int itemCount = (int) categorySnapshot.getChildrenCount();
                    categoryList.add(new CategoryCountModel(categoryName, itemCount));
                }

                adapter.notifyDataSetChanged();

                if (categoryList.isEmpty()) {
                    Toast.makeText(CategoriesMainActivity.this,
                            "No categories found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}