package com.example.smartshelfuser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryCountAdapter extends RecyclerView.Adapter<CategoryCountAdapter.ViewHolder>{

    private final List<CategoryCountModel> list;

    public CategoryCountAdapter(List<CategoryCountModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryCountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_counted_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCountAdapter.ViewHolder holder, int position) {
        CategoryCountModel item = list.get(position);

        holder.title.setText(item.getCategoryName());
        holder.sub.setText("Total Items: " + item.getCount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, sub;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewCategoryName);
            sub = itemView.findViewById(R.id.textViewCategoryItemCount);
        }
    }
}
