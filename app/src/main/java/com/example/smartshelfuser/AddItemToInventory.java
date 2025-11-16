package com.example.smartshelfuser;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddItemToInventory extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private InventoryItemAdapter adapter;
    private final List<ShoppingRowListItem> itemList = new ArrayList<>();
    private SearchView searchView;
    private MaterialButton btnAddToInventory;

    private DatabaseReference rowListItemsRef, categoryRef;
    private String rowListId, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item_to_inventory);

        rowListId = getIntent().getStringExtra("rowListId");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
          if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "You must be logged in to add items.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarAddItems_item);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("listName"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rowListItemsRef = FirebaseDatabase.getInstance()
                .getReference("RowListsItems")
                .child(currentUserId)
                .child(rowListId);

        categoryRef = FirebaseDatabase.getInstance()
                .getReference("Categories")
                .child(currentUserId);

        recyclerViewItems = findViewById(R.id.recyclerViewInventoryItems);
        searchView = findViewById(R.id.searchViewItems);
        btnAddToInventory = findViewById(R.id.btnAddToInventory);

        adapter = new InventoryItemAdapter();
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(adapter);

        fetchItems();
        setupSearch();
        btnAddToInventory.setOnClickListener(v -> addSelectedItemsToInventory());
    }

    private void fetchItems() {
        rowListItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ShoppingRowListItem item = itemSnapshot.getValue(ShoppingRowListItem.class);
                    if (item != null) {
                        if (item.getItemId() == null)
                            item.setItemId(itemSnapshot.getKey());

                        itemList.add(item);
                    }
                }
                adapter.submitList(new ArrayList<>(itemList));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddItemToInventory.this, "Failed To Load Items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        rowListRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ShoppingRowList rowList = snapshot.getValue(ShoppingRowList.class);
//                if (rowList != null) {
//                    listName = rowList.getListName();
//                    purchasePlace = rowList.getPurchasePlace();
//                    location = rowList.getLocation();
//                    createdAt = rowList.getCreatedAt();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(AddItemToInventory.this, "Failed to load rowlist: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<ShoppingRowListItem> filtered = new ArrayList<>();
                for (ShoppingRowListItem item : itemList) {
                    if (item.getItemName().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(item);
                    }
                }
                adapter.submitList(filtered);
                return true;
            }
        });
    }

    private void addSelectedItemsToInventory() {
        List<ShoppingRowListItem> selectedItems = new ArrayList<>();

        for (ShoppingRowListItem item : adapter.getCurrentItems()) {
            if (item.isSelected()) selectedItems.add(item);
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No items selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (ShoppingRowListItem item : selectedItems) {
            String category = item.getItemCategory();
            String itemId = item.getItemId();

            if (category != null && itemId != null) {
                // Add to inventory
                categoryRef.child(category).child(itemId).setValue(item)
                        .addOnSuccessListener(aVoid -> {
                            // Remove from RowListsItems permanently
                            rowListItemsRef.child(itemId).removeValue();

                            // Remove from local list instantly
                            itemList.remove(item);
                            adapter.submitList(new ArrayList<>(itemList));
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to add item: " + item.getItemName(), Toast.LENGTH_SHORT).show()
                        );
            }
        }
        Toast.makeText(this, "Selected items added to inventory successfully.", Toast.LENGTH_SHORT).show();
    }
}