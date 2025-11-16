package com.example.smartshelfuser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FetchProductAdapter extends RecyclerView.Adapter<FetchProductAdapter.ProductViewHolder>{
    private final List<FetchCategoryProduct> productList;

    public FetchProductAdapter(List<FetchCategoryProduct> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fetch_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        FetchCategoryProduct item = productList.get(position);

        holder.nameTextView.setText(item.getItemName());
        holder.detailsTextView.setText(
                "Qty: " + (item.getItemQuantity() != null ? item.getItemQuantity() : "N/A") +
                        " | Category: " + (item.getItemCategory() != null ? item.getItemCategory() : "N/A")
                        + "Added Date: " + (item.getAddedDateTime() != null ? item.getAddedDateTime() : "N/A")
        );

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView detailsTextView;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productNameTextView);
            detailsTextView = itemView.findViewById(R.id.productDetailsTextView);
        }
    }
}
