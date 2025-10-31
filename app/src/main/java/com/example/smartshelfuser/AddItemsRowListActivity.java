package com.example.smartshelfuser;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.type.Date;

import com.example.smartshelfuser.ShoppingRowListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddItemsRowListActivity extends AppCompatActivity {

    private TextInputEditText editTextItemName, editTextItemDescription, editTextItemQuantity;
    private Spinner spinnerItemCategory;
    private AutoCompleteTextView autoCompleteQuantityUnit;
    private Button buttonAddItem;
    private TextView textViewDateTime, textViewNoItems;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;

    private AddedRowItemsAdapter adapter;
    private List<ShoppingRowListItem> itemList;

    private DatabaseReference rowListItemsRef;
    private String rowListId;
    private String currentUserId;

    private DatabaseReference rowListRef;
    private String listName;
    private String purchasePlace;
    private String location;
    private Long createdAt;

    private List<HashMap<String, Object>> currentRowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_items_row_list);

        // Get the rowListId from the Intent
        rowListId = getIntent().getStringExtra("rowListId");
        listName = getIntent().getStringExtra("listName");

        // Get the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            // Handle case where user is not logged in
            Toast.makeText(this, "You must be logged in to add items.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rowListItemsRef = FirebaseDatabase.getInstance()
                .getReference("RowListsItems")
                .child(currentUserId)
                .child(rowListId);

        rowListRef = FirebaseDatabase.getInstance().getReference("RowLists").child(currentUserId).child(rowListId);

        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        editTextItemQuantity = findViewById(R.id.editTextItemQuantity);
        spinnerItemCategory = findViewById(R.id.spinnerItemCategory);
        autoCompleteQuantityUnit = findViewById(R.id.autoCompleteQuantityUnit);
        buttonAddItem = findViewById(R.id.buttonAddItemToList);
        textViewDateTime = findViewById(R.id.textViewCurrentDateTimeItem);
        textViewNoItems = findViewById(R.id.textViewNoItemsAddedMessage);
        recyclerViewItems = findViewById(R.id.recyclerViewAddedItems);

        Toolbar toolbar = findViewById(R.id.toolbarAddItems);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("listName"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupSpinners();

        // Setup RecyclerView
        itemList = new ArrayList<>();
        adapter = new AddedRowItemsAdapter(this, itemList);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(adapter);

        // Set current date/time
        updateDateTime();

        // Load existing items from the database
        loadItemsFromDatabase();

        // Add Item button
        buttonAddItem.setOnClickListener(v -> addItemToList());


    }

    private void setupSpinners() {
        // Categories Spinner
        String[] categories = {"Groceries", "Dairy", "Produce", "Meat", "Bakery", "Household", "Electronics", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemCategory.setAdapter(categoryAdapter);

        // Quantity Unit AutoCompleteTextView
        String[] units = {"pcs", "Kg", "g", "L", "mL", "unit", "pack", "dozen"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units);
        autoCompleteQuantityUnit.setAdapter(unitAdapter);
    }

    private void updateDateTime() {
        String dateTime = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.getDefault()).format(new java.util.Date());
        textViewDateTime.setText("Entry Time: " + dateTime);
    }

    private void addItemToList() {
        String name = editTextItemName.getText().toString().trim();
        String description = editTextItemDescription.getText().toString().trim();
        String category = spinnerItemCategory.getSelectedItem() != null ? spinnerItemCategory.getSelectedItem().toString() : "";
        String quantity = editTextItemQuantity.getText().toString().trim();
        String unit = autoCompleteQuantityUnit.getText().toString().trim();
        String dateTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new java.util.Date());

        if (TextUtils.isEmpty(name)) {
            editTextItemName.setError("Enter item name");
            return;
        }
        if (TextUtils.isEmpty(quantity) || quantity.equals("0")) {
            editTextItemQuantity.setError("Enter a valid quantity");
            return;
        }

        // Create a unique key for the new item
        String itemId = rowListItemsRef.push().getKey();
        ShoppingRowListItem item = new ShoppingRowListItem(itemId, name, description, category, quantity, unit, dateTime);

        // Save to Firebase Database
        if (itemId != null) {
            rowListItemsRef.child(itemId).setValue(item).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
                    editTextItemName.setText("");
                    editTextItemDescription.setText("");
                    editTextItemQuantity.setText("0");
                    autoCompleteQuantityUnit.setText("");
                    updateDateTime();
                } else {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadItemsFromDatabase() {
        rowListItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        ShoppingRowListItem item = itemSnapshot.getValue(ShoppingRowListItem.class);
                        if (item != null) {
                            itemList.add(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                if (itemList.isEmpty()) {
                    textViewNoItems.setVisibility(View.VISIBLE);
                } else {
                    textViewNoItems.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddItemsRowListActivity.this, "Failed to load items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddItemsRowListActivity.this, "Failed to load items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}