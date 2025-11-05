package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingListComponentFragment extends Fragment {

    private RecyclerView recyclerViewRowLists;
    private AddedFinalShoppingListAdapter adapter;
    private List<FinalShoppingList> shoppingLists;
    private TextView textViewEmptyMessage;
    private DatabaseReference rowListRef;
    private DatabaseReference userRowListsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ValueEventListener rowListsListener;

    public static ShoppingListComponentFragment newInstance() {
        return new ShoppingListComponentFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list_component, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rowListRef = FirebaseDatabase.getInstance().getReference("RowLists");

        textViewEmptyMessage = view.findViewById(R.id.textViewEmptyMessageFinal);
        recyclerViewRowLists = view.findViewById(R.id.recyclerViewShoppingListsFinal);

        shoppingLists = new ArrayList<>();
        adapter = new AddedFinalShoppingListAdapter(getContext(), shoppingLists);
        recyclerViewRowLists.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRowLists.setAdapter(adapter);

        if (currentUser != null) {
            userRowListsRef = rowListRef.child(currentUser.getUid());
            loadRowLists();
        } else showLoginRequiredUI();

        return view;
    }

    private void showLoginRequiredUI() {
        Toast.makeText(getContext(), "Please login to manage shopping lists.", Toast.LENGTH_LONG).show();
        recyclerViewRowLists.setVisibility(View.GONE);
        textViewEmptyMessage.setVisibility(View.VISIBLE);
        textViewEmptyMessage.setText("Please login to manage shopping lists.");
    }

    private void loadRowLists() {
        if (currentUser == null) {
            showLoginRequiredUI();
            return;
        }

        rowListsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingLists.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    FinalShoppingList item = ds.getValue(FinalShoppingList.class);
                    if (item != null) shoppingLists.add(item);
                }

                Collections.sort(shoppingLists, (o1, o2) -> Long.compare(
                        o2.getCreatedAt() != null ? o2.getCreatedAt() : 0,
                        o1.getCreatedAt() != null ? o1.getCreatedAt() : 0
                ));

                adapter.notifyDataSetChanged();

                if (shoppingLists.isEmpty()) {
                    textViewEmptyMessage.setVisibility(View.VISIBLE);
                    recyclerViewRowLists.setVisibility(View.GONE);
                } else {
                    textViewEmptyMessage.setVisibility(View.GONE);
                    recyclerViewRowLists.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        userRowListsRef.addValueEventListener(rowListsListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userRowListsRef != null && rowListsListener != null) {
            userRowListsRef.removeEventListener(rowListsListener);
        }
    }
}