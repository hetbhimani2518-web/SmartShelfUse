package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.ItemViewHolder>{

    private Context context;
    private List<ShoppingRowListItem> itemList;
    private List<ShoppingRowListItem> selectedItems = new ArrayList<>();

    public InventoryItemAdapter(Context context, List<ShoppingRowListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void updateList(List<ShoppingRowListItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    public List<ShoppingRowListItem> getSelectedItems() {
        return selectedItems;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_with_checkbox, parent, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InventoryItemAdapter.ItemViewHolder holder, int position) {
        ShoppingRowListItem item = itemList.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemDetails.setText(item.getItemDescription() + " | " +
                item.getItemQuantity() + " | " +
                item.getItemCategory() + " | " +
                item.getAddedDateTime());

        // Avoid wrong checkbox states during recycling
        holder.checkBoxItem.setOnCheckedChangeListener(null);
        holder.checkBoxItem.setChecked(selectedItems.contains(item));

        holder.checkBoxItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedItems.contains(item)) {
                    selectedItems.add(item);
                }
            } else {
                selectedItems.remove(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxItem;
        TextView tvItemName, tvItemDetails;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxItem = itemView.findViewById(R.id.checkBoxItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDetails = itemView.findViewById(R.id.tvItemDetails);
        }
    }
}
