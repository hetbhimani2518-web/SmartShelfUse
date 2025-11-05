package com.example.smartshelfuser;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShoppingRowListItem {

    private String itemId;
    private String itemName;
    private String itemDescription;
    private String itemCategory;
    private String itemQuantity;
    private String itemUnit;
    private String addedDateTime;

    public ShoppingRowListItem() {}

    public ShoppingRowListItem(String itemId, String itemName, String itemDescription,
                               String itemCategory, String itemQuantity,
                               String itemUnit, String addedDateTime) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCategory = itemCategory;
        this.itemQuantity = itemQuantity;
        this.itemUnit = itemUnit;
        this.addedDateTime = addedDateTime;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public String getItemQuantity() { return itemQuantity; }
    public void setItemQuantity(String itemQuantity) { this.itemQuantity = itemQuantity; }

    public String getItemUnit() { return itemUnit; }
    public void setItemUnit(String itemUnit) { this.itemUnit = itemUnit; }

    public String getAddedDateTime() { return addedDateTime; }
    public void setAddedDateTime(String addedDateTime) { this.addedDateTime = addedDateTime; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ShoppingRowListItem)) return false;
        return itemId != null && itemId.equals(((ShoppingRowListItem) obj).getItemId());
    }

    @Override
    public int hashCode() {
        return itemId != null ? itemId.hashCode() : 0;
    }

}
