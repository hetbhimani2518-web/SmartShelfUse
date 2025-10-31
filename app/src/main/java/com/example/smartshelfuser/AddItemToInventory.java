package com.example.smartshelfuser;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

public class AddItemToInventory extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private InventoryItemAdapter adapter;
    private List<ShoppingRowListItem> itemList = new ArrayList<>();
    private SearchView searchView;
    private Button btnAddToInventory;

    private DatabaseReference rowListItemsRef, categoryRef, rowListRef;
    private String rowListId, listName, currentUserId;
    private String purchasePlace, location;
    private Long createdAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item_to_inventory);

        rowListId = getIntent().getStringExtra("rowListId");
        listName = getIntent().getStringExtra("rowListName");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "You must be logged in to add items.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rowListItemsRef = FirebaseDatabase.getInstance()
                .getReference("RowListsItems")
                .child(currentUserId)
                .child(rowListId);

        rowListRef = FirebaseDatabase.getInstance()
                .getReference("RowLists")
                .child(currentUserId)
                .child(rowListId);

        categoryRef = FirebaseDatabase.getInstance()
                .getReference("Categories")
                .child(currentUserId);

        recyclerViewItems = findViewById(R.id.recyclerViewInventoryItems);
        searchView = findViewById(R.id.searchViewItems);
        btnAddToInventory = findViewById(R.id.btnAddToInventory);

        adapter = new InventoryItemAdapter(this, itemList);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(adapter);

        fetchItems();
        btnAddToInventory.setOnClickListener(v -> addSelectedItemsToInventory());
        setupSearch();
    }

    private void fetchItems() {
        rowListItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ShoppingRowListItem item = itemSnapshot.getValue(ShoppingRowListItem.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddItemToInventory.this, "Failed To Load Items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        rowListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShoppingRowList rowList = snapshot.getValue(ShoppingRowList.class);
                if (rowList != null) {
                    listName = rowList.getListName();
                    purchasePlace = rowList.getPurchasePlace();
                    location = rowList.getLocation();
                    createdAt = rowList.getCreatedAt();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddItemToInventory.this, "Failed to load rowlist: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                adapter.updateList(filtered);
                return true;
            }
        });
    }

    private void addSelectedItemsToInventory() {
        List<ShoppingRowListItem> selected = adapter.getSelectedItems();
        for (ShoppingRowListItem item : selected) {
            String category = item.getItemCategory();
            String itemId = item.getItemId();
            categoryRef.child(category).child(itemId).setValue(item);
        }
        Toast.makeText(this, "Items added to inventory successfully", Toast.LENGTH_SHORT).show();
    }
}