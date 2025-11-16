package com.example.smartshelfuser;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FetchCategoryAdapter extends RecyclerView.Adapter<FetchCategoryAdapter.CategoryViewHolder> {

    private final List<FetchCategory> categoryList;
    private final Context context;

    public FetchCategoryAdapter(Context context, List<FetchCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fetch_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        FetchCategory category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getCategoryName());

        boolean isExpanded = category.isExpanded();
        holder.productsRecyclerView.setVisibility(isExpanded ? VISIBLE : GONE);

        if (isExpanded) {
            FetchProductAdapter productAdapter = new FetchProductAdapter(category.getProducts());
            holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.productsRecyclerView.setAdapter(productAdapter);
        }

        holder.itemView.setOnClickListener(v -> {
            category.setExpanded(!category.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        RecyclerView productsRecyclerView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            productsRecyclerView = itemView.findViewById(R.id.productsRecyclerView);
        }
    }
}
