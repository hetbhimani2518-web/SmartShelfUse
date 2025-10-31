package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;

import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RowListComponentFragment extends Fragment implements ShoppingRowListAdapter.RowListActionListener {

    private DatabaseReference rowListRef;
    private TextInputLayout tilListName, tilPurchasePlace;
    private TextInputEditText editTextListName, editTextPurchasePlaceName;
    private TextView textViewCurrentDateTime, textViewSelectedLocation;
    private Button buttonSelectPlaceMap, buttonCreateList;
    private ProgressBar progressBar;

    private RecyclerView recyclerViewRowLists;
    private TextView textViewEmptyMessage;
    private ShoppingRowListAdapter adapter;
    private List<ShoppingRowList> rowLists;
    private View dividerView;
    private TextView textViewExistingListsHeader;
    private ProgressBar progressBarListLoading;

    private DatabaseReference userRowListsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private SimpleDateFormat dateTimeFormatterForDisplay;

    private Double selectedStoreLatitude;
    private Double selectedStoreLongitude;
    private String selectedStoreAddressName;
    private ActivityResultLauncher<Intent> mapPickerLauncher;

    //This is added
    private ShoppingRowList itemToEdit; // Temp storage for item being edited
    private int positionToEdit; // Temp storage for item position
    private ChildEventListener childEventListener;

    public static RowListComponentFragment newInstance() {
        return new RowListComponentFragment();
    }

    @UnstableApi
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedStoreLatitude = data.getDoubleExtra(Activity_rowlist_google_map_picker.EXTRA_PICKED_LATITUDE, 0.0);
                            selectedStoreLongitude = data.getDoubleExtra(Activity_rowlist_google_map_picker.EXTRA_PICKED_LONGITUDE, 0.0);
                            selectedStoreAddressName = data.getStringExtra(Activity_rowlist_google_map_picker.EXTRA_PICKED_ADDRESS_NAME);

                            //This is added
                            // Check if we are in list creation mode or editing mode
                            if (itemToEdit != null) {
                                // Editing Mode: Update the location of the edited item directly in the adapter
                                String newLocation = selectedStoreAddressName != null ? selectedStoreAddressName :
                                        String.format(Locale.getDefault(), "Lat: %.5f, Lng: %.5f", selectedStoreLatitude, selectedStoreLongitude);

                                adapter.updateItemLocation(newLocation, positionToEdit);

                                // Reset temporary storage
                                itemToEdit = null;
                                positionToEdit = -1;
                            } else {
                                // Creation Mode: Update the fragment's UI
                                updateSelectedLocationDisplay();
                            }
                            // till this line

                            //Actual single line
//                            updateSelectedLocationDisplay();
                        }
                    }
                }
        );
    }

    @UnstableApi
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_row_list_component, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rowListRef = FirebaseDatabase.getInstance().getReference("RowLists");

        if (currentUser != null) {
            userRowListsRef = rowListRef.child(currentUser.getUid());
        }

        dateTimeFormatterForDisplay = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm a", Locale.getDefault());

        tilListName = view.findViewById(R.id.tilListName);
        editTextListName = view.findViewById(R.id.editTextListName);
        textViewCurrentDateTime = view.findViewById(R.id.textViewCurrentDateTime);
        tilPurchasePlace = view.findViewById(R.id.tilPurchasePlaceName);
        editTextPurchasePlaceName = view.findViewById(R.id.editTextPurchasePlaceName);
        buttonSelectPlaceMap = view.findViewById(R.id.buttonSelectPlaceMap);
        buttonCreateList = view.findViewById(R.id.buttonCreateList);
        progressBar = view.findViewById(R.id.progressBarCreateList);
        textViewSelectedLocation = view.findViewById(R.id.textViewSelectedLocation);

        textViewEmptyMessage = view.findViewById(R.id.textViewEmptyMessage);
        dividerView = view.findViewById(R.id.divider);
        textViewExistingListsHeader = view.findViewById(R.id.textViewExistingListsHeader);
        progressBarListLoading = view.findViewById(R.id.progressBarListLoading);

        recyclerViewRowLists = view.findViewById(R.id.recyclerViewShoppingLists);

        rowLists = new ArrayList<>();
        adapter = new ShoppingRowListAdapter(getContext(), rowLists , this);
        recyclerViewRowLists.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRowLists.setAdapter(adapter);
//        recyclerViewRowLists.setHasFixedSize(true);

        setCurrentDateTimeForDisplay();

        buttonSelectPlaceMap.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Activity_rowlist_google_map_picker.class);
            String currentPlaceName = editTextPurchasePlaceName.getText().toString().trim();
            if (!currentPlaceName.isEmpty()) {
                intent.putExtra(Activity_rowlist_google_map_picker.EXTRA_INITIAL_SEARCH_TERM, currentPlaceName);
            }
            mapPickerLauncher.launch(intent);
        });

        buttonCreateList.setOnClickListener(v -> processCreateNewShoppingList());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (currentUser == null) {
            showLoginRequiredUI();
        } else {
            loadRowLists();
        }
    }

    private void setCurrentDateTimeForDisplay() {
        if (textViewCurrentDateTime != null) {
            textViewCurrentDateTime.setText("Created on: " + dateTimeFormatterForDisplay.format(new java.util.Date()));
        }
    }

    private void updateSelectedLocationDisplay() {
        if (textViewSelectedLocation == null) return;

        if (selectedStoreAddressName != null && !selectedStoreAddressName.isEmpty()) {
            textViewSelectedLocation.setText(selectedStoreAddressName);
        } else if (selectedStoreLatitude != null && selectedStoreLongitude != null
                && selectedStoreLatitude != 0.0 && selectedStoreLongitude != 0.0) {
            textViewSelectedLocation.setText(
                    String.format(Locale.getDefault(), "Lat: %.5f, Lng: %.5f",
                            selectedStoreLatitude, selectedStoreLongitude)
            );
        } else {
            textViewSelectedLocation.setText(R.string.no_location_selected_placeholder);
        }

        textViewSelectedLocation.setVisibility(View.VISIBLE);
    }

    private void showLoginRequiredUI() {
        Toast.makeText(getContext(), "Please login to manage shopping lists.", Toast.LENGTH_LONG).show();
        dividerView.setVisibility(View.GONE);
        textViewExistingListsHeader.setVisibility(View.GONE);
        recyclerViewRowLists.setVisibility(View.GONE);
        textViewEmptyMessage.setVisibility(View.VISIBLE);
        textViewEmptyMessage.setText("Please login to see your lists.");
        progressBarListLoading.setVisibility(View.GONE);
    }

    private void processCreateNewShoppingList() {
        String listName = editTextListName.getText().toString().trim();
        String purchasePlace = editTextPurchasePlaceName.getText().toString().trim();
        String location = textViewSelectedLocation.getText().toString();

        if (TextUtils.isEmpty(listName)) {
            tilListName.setError("List name is required");
            return;
        } else tilListName.setError(null);

        if (TextUtils.isEmpty(purchasePlace)) {
            tilPurchasePlace.setError("Purchase Place Name is required");
            return;
        } else tilPurchasePlace.setError(null);

        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to create a list.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonCreateList.setEnabled(false);

        String rowListId = UUID.randomUUID().toString();
        String userId = currentUser.getUid();

        long createdAt = System.currentTimeMillis();

        ShoppingRowList rowList = new ShoppingRowList(rowListId, userId, listName, purchasePlace, createdAt, location);

        userRowListsRef.child(rowListId).setValue(rowList)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Row List Created Successfully", Toast.LENGTH_SHORT).show();
                        loadRowLists();
                        clearFields();
                    } else {
                        Toast.makeText(getContext(), "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    buttonCreateList.setEnabled(true);
                });
    }

    private void clearFields() {
        editTextListName.setText("");
        editTextPurchasePlaceName.setText("");
        textViewSelectedLocation.setText(R.string.no_location_selected_placeholder);
        textViewSelectedLocation.setVisibility(View.VISIBLE);
        selectedStoreLatitude = null;
        selectedStoreLongitude = null;
        selectedStoreAddressName = null;
        setCurrentDateTimeForDisplay();
    }

    private void loadRowLists() {
        if (currentUser == null) {
            showLoginRequiredUI();
            return;
        }

        progressBarListLoading.setVisibility(View.VISIBLE);

        //THis is added
        if (childEventListener != null) {
            userRowListsRef.removeEventListener(childEventListener);
        }
        rowLists.clear();
        adapter.notifyDataSetChanged();
        childEventListener = userRowListsRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {

            // Handle new list items
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ShoppingRowList item = snapshot.getValue(ShoppingRowList.class);
                if (item != null) {
                    rowLists.add(item);
                    sortAndNotifyDataChange();
                }
            }

            // Handle list item updates (e.g., from the edit dialog)
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ShoppingRowList updatedItem = snapshot.getValue(ShoppingRowList.class);
                if (updatedItem != null) {
                    // Find and replace the old item
                    for (int i = 0; i < rowLists.size(); i++) {
                        if (rowLists.get(i).getRowListId().equals(updatedItem.getRowListId())) {
                            rowLists.set(i, updatedItem);
                            sortAndNotifyDataChange();
                            return;
                        }
                    }
                }
            }

            // Handle list item deletions
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ShoppingRowList deletedItem = snapshot.getValue(ShoppingRowList.class);
                if (deletedItem != null) {
                    // Find and remove the item
                    for (int i = 0; i < rowLists.size(); i++) {
                        if (rowLists.get(i).getRowListId().equals(deletedItem.getRowListId())) {
                            rowLists.remove(i);
                            sortAndNotifyDataChange();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Not strictly necessary to implement if ordering is based on time, but included for completeness.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarListLoading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading lists: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //Actual code till ---- symbol
        userRowListsRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rowLists.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ShoppingRowList item = ds.getValue(ShoppingRowList.class);
                    if (item != null) rowLists.add(item);
                }

//                Actual Code
//                Collections.sort(rowLists, (o1, o2) -> Long.compare(
//                        o2.getCreatedAt() != null ? o2.getCreatedAt() : 0,
//                        o1.getCreatedAt() != null ? o1.getCreatedAt() : 0
//                ));
                rowLists.sort((o1, o2) -> Long.compare(
                        o2.getCreatedAt() != null ? o2.getCreatedAt() : 0,
                        o1.getCreatedAt() != null ? o1.getCreatedAt() : 0
                ));


                adapter.notifyDataSetChanged();
                progressBarListLoading.setVisibility(View.GONE);

                //Actual Code
//                if (rowLists.isEmpty()) {
//                    textViewEmptyMessage.setVisibility(View.VISIBLE);
//                    recyclerViewRowLists.setVisibility(View.GONE);
//                } else {
//                    textViewEmptyMessage.setVisibility(View.GONE);
//                    recyclerViewRowLists.setVisibility(View.VISIBLE);
//                }

                //THis is added
                if (rowLists.isEmpty()) {
                    textViewEmptyMessage.setVisibility(View.VISIBLE);
                    recyclerViewRowLists.setVisibility(View.GONE);
                    dividerView.setVisibility(View.GONE); // Hide divider if empty
                    textViewExistingListsHeader.setVisibility(View.GONE); // Hide header if empty
                } else {
                    textViewEmptyMessage.setVisibility(View.GONE);
                    recyclerViewRowLists.setVisibility(View.VISIBLE);
                    // CRUCIAL ADDITION: Show the divider and header
                    dividerView.setVisibility(View.VISIBLE);
                    textViewExistingListsHeader.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarListLoading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//        -------------
    }

    //THis is added till -----------------
    // 3. Introduce a helper method for sorting and UI update
    @SuppressLint("NotifyDataSetChanged")
    private void sortAndNotifyDataChange() {
        rowLists.sort((o1, o2) -> Long.compare(
                o2.getCreatedAt() != null ? o2.getCreatedAt() : 0,
                o1.getCreatedAt() != null ? o1.getCreatedAt() : 0
        ));
        adapter.notifyDataSetChanged();
        progressBarListLoading.setVisibility(View.GONE);

        // UI visibility update logic (from previous solutions)
        if (rowLists.isEmpty()) {
            textViewEmptyMessage.setVisibility(View.VISIBLE);
            recyclerViewRowLists.setVisibility(View.GONE);
            dividerView.setVisibility(View.GONE);
            textViewExistingListsHeader.setVisibility(View.GONE);
        } else {
            textViewEmptyMessage.setVisibility(View.GONE);
            recyclerViewRowLists.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.VISIBLE);
            textViewExistingListsHeader.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (childEventListener != null) {
            userRowListsRef.removeEventListener(childEventListener);
        }
    }
//    -----------------

    //This part is added
    @UnstableApi
    @Override
    public void onPickLocationRequested(ShoppingRowList item, int position) {
        // Store the context of the item being edited
        itemToEdit = item;
        positionToEdit = position;

        // Launch the map picker
        Intent intent = new Intent(getActivity(), Activity_rowlist_google_map_picker.class);
        intent.putExtra(Activity_rowlist_google_map_picker.EXTRA_INITIAL_SEARCH_TERM, item.getPurchasePlace());
        mapPickerLauncher.launch(intent);
    }
}