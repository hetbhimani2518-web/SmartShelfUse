package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddedFinalShoppingListAdapter extends RecyclerView.Adapter<AddedFinalShoppingListAdapter.ViewHolder> {

    private Context context;
    private List<FinalShoppingList> shoppingLists;
    private String userId;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

    public AddedFinalShoppingListAdapter(Context context, List<FinalShoppingList> shoppingLists) {
        this.context = context;
        this.shoppingLists = shoppingLists;
        this.userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonymous";
    }

    @NonNull
    @Override
    public AddedFinalShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finalized_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddedFinalShoppingListAdapter.ViewHolder holder, int position) {
        FinalShoppingList list = shoppingLists.get(position);

        holder.textViewListName.setText(list.getListName() != null ? list.getListName() : "Unnamed List");

        holder.textViewPurchasePlace.setText("Purchase Place: " +
                (list.getPurchasePlace() != null ? list.getPurchasePlace() : "N/A"));

        if (list.getCreatedAt() != null) {
            String formattedDate = dateFormatter.format(new Date(list.getCreatedAt()));
            holder.textViewCreationDate.setText("Created: " + formattedDate);
        } else {
            holder.textViewCreationDate.setText("Created: --");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddItemToInventory.class);
            intent.putExtra("rowListId", list.getRowListId());
            intent.putExtra("listName", list.getListName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewListName;
        TextView textViewPurchasePlace;
        TextView textViewCreationDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewListName = itemView.findViewById(R.id.textViewFinalizedListName);
            textViewPurchasePlace = itemView.findViewById(R.id.textViewFinalizedListPlace);
            textViewCreationDate = itemView.findViewById(R.id.textViewFinalizedListDate);
        }
    }
}
