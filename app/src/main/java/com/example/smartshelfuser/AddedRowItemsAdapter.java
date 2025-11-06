package com.example.smartshelfuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;

import java.util.List;

public class AddedRowItemsAdapter extends RecyclerView.Adapter<AddedRowItemsAdapter.AddedRowItemViewHolder> {

    private Context context;
    private List<ShoppingRowListItem> itemList;

    public AddedRowItemsAdapter(Context context, List<ShoppingRowListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public AddedRowItemsAdapter.AddedRowItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_added_to_row_list, parent, false);
        return new AddedRowItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedRowItemsAdapter.AddedRowItemViewHolder holder, int position) {
        ShoppingRowListItem item = itemList.get(position);

        holder.textName.setText(item.getItemName());
        holder.textDescription.setText(item.getItemDescription().isEmpty() ? "-" : item.getItemDescription());
        holder.textCategory.setText(item.getItemCategory());
        holder.textQuantity.setText(item.getItemQuantity() + " " + item.getItemUnit());
        holder.textDateTime.setText(item.getAddedDateTime());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class AddedRowItemViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDescription, textCategory, textQuantity, textDateTime;

        public AddedRowItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textViewItemName);
            textDescription = itemView.findViewById(R.id.textViewItemDescription);
            textCategory = itemView.findViewById(R.id.textViewItemCategory);
            textQuantity = itemView.findViewById(R.id.textViewItemQuantity);
            textDateTime = itemView.findViewById(R.id.textViewItemDateTime);
        }
    }

    public void updateList(List<ShoppingRowListItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }
}
