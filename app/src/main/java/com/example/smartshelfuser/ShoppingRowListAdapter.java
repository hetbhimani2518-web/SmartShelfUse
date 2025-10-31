package com.example.smartshelfuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShoppingRowListAdapter extends RecyclerView.Adapter<ShoppingRowListAdapter.ViewHolder> {

    //below both are added
    public interface RowListActionListener {
        void onPickLocationRequested(ShoppingRowList item, int position);
    }
    private RowListActionListener actionListener;

    private Context context;
    private List<ShoppingRowList> rowListItems;
    private String userId;

    private SimpleDateFormat dateFormatter =
            new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.getDefault());

    //Here is the change in last perameter
    public ShoppingRowListAdapter(Context context, List<ShoppingRowList> rowListItems ,  RowListActionListener actionListener) {
        this.context = context;
        this.rowListItems = rowListItems;
        this.actionListener = actionListener;
        this.userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonymous";
    }



    @NonNull
    @Override
    public ShoppingRowListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list_row, parent, false);
        return new ViewHolder(view);
    }

    @UnstableApi
    @Override
    public void onBindViewHolder(@NonNull ShoppingRowListAdapter.ViewHolder holder, int position) {
        ShoppingRowList item = rowListItems.get(position);

        holder.tvListName.setText(item.getListName());
        holder.tvPurchasePlace.setText("Purchase Place: " + item.getPurchasePlace());

        if (item.getCreatedAt() != null) {
            String formattedDate = dateFormatter.format(new Date(item.getCreatedAt()));
            holder.tvDateTime.setText("Created: " + formattedDate);
        } else {
            holder.tvDateTime.setText("Created: --");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddItemsRowListActivity.class);
            intent.putExtra("rowListId", item.getRowListId());
            intent.putExtra("listName", item.getListName());
            context.startActivity(intent);
        });

        // Popup menu click
        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnMenu);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_shopping_row_list_item, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.action_info_list) {
                    showRowListInfo(item);
                    return true;
                } else if (id == R.id.action_edit_list) {
                    showEditRowListDialog(item, position);
                    return true;
                } else if (id == R.id.action_delete_list) {
                    deleteRowList(item, position);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return rowListItems.size();
    }

    private void deleteRowList(ShoppingRowList item, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("RowLists")
                .child(userId)
                .child(item.getRowListId());

        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                rowListItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, rowListItems.size());
                Toast.makeText(context, "Deleted " + item.getListName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRowListInfo(ShoppingRowList item) {
        String info = "List: " + item.getListName()
                + "\nPurchase Place: " + item.getPurchasePlace()
                + "\nLocation: " + (item.getLocation() != null ? item.getLocation() : "N/A");

        new AlertDialog.Builder(context)
                .setTitle("Row List Info")
                .setMessage(info)
                .setPositiveButton("OK", null)
                .show();
    }


    @UnstableApi
    private void showEditRowListDialog(ShoppingRowList item, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_row_list, null);

        EditText etListName = dialogView.findViewById(R.id.etEditListName);
        EditText etPurchasePlace = dialogView.findViewById(R.id.etEditPurchasePlace);
        TextView tvLocation = dialogView.findViewById(R.id.tvEditSelectedLocation);
        Button btnPickLocation = dialogView.findViewById(R.id.btnPickNewLocation);

        etListName.setText(item.getListName());
        etPurchasePlace.setText(item.getPurchasePlace());
        tvLocation.setText(item.getLocation() != null ? item.getLocation() : "No location set");

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Edit Row List")
                .setView(dialogView)
                .setPositiveButton("Save", (d, which) -> {
                    String newName = etListName.getText().toString().trim();
                    String newPlace = etPurchasePlace.getText().toString().trim();


                    String newLocation = tvLocation.getText().toString().equals("No location set")
                            ? null : tvLocation.getText().toString(); // Get potentially updated location

                    if (newName.isEmpty() || newPlace.isEmpty()) {
                        Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReference("RowLists")
                            .child(userId)
                            .child(item.getRowListId());

                    long newTime = System.currentTimeMillis();
                    item.setListName(newName);
                    item.setPurchasePlace(newPlace);
                    item.setCreatedAt(newTime);

                    //This part is added
                    item.setLocation(newLocation);

                    ref.setValue(item).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            notifyItemChanged(position);
                            Toast.makeText(context, "Row List Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .create();

        // Pick new location (launch map activity)
        //Actual code of our and below is added
//        btnPickLocation.setOnClickListener(v -> {
//            Intent intent = new Intent(context, Activity_rowlist_google_map_picker.class);
//            ((Activity) context).startActivityForResult(intent, 200); // handle result in fragment/activity
//            dialog.dismiss();
//        });

        //This is added
        btnPickLocation.setOnClickListener(v -> {
            // Use the callback interface instead of direct casting
            if (actionListener != null) {
                actionListener.onPickLocationRequested(item, position);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    //This is added
    // Add a public method to update the temporary location in the dialog after map result
    @SuppressLint("SetTextI18n")
    public void updateItemLocation(String newLocation, int position) {
        // Optional: Update the model immediately so the next dialog open reflects the change
        // This assumes the item is still the same object reference
        rowListItems.get(position).setLocation(newLocation);
        // Notify the change so the RecyclerView updates instantly
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvListName, tvPurchasePlace, tvDateTime;
        ImageButton btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.textViewItemListName);
            tvPurchasePlace = itemView.findViewById(R.id.textViewItemPurchasePlace);
            tvDateTime = itemView.findViewById(R.id.textViewItemCreationDate);
            btnMenu = itemView.findViewById(R.id.imageButtonItemMenu);
        }
    }
}
