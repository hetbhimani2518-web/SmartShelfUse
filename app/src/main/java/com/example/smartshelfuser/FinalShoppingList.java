package com.example.smartshelfuser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FinalShoppingList {
    private String shoppingListId;
    private String userId;
    private String rowListId;
    private String listName;
    private String purchasePlace;
    private String location;
    private Long createdAt;
    private List<ShoppingRowListItem> items;

    public FinalShoppingList() {
        this.items = new ArrayList<>();
    }

    public FinalShoppingList(String shoppingListId, String userId, String rowListId, String listName, String purchasePlace, String location, Long createdAt, List<ShoppingRowListItem> items) {
        this.shoppingListId = shoppingListId;
        this.userId = userId;
        this.rowListId = rowListId;
        this.listName = listName;
        this.purchasePlace = purchasePlace;
        this.location = location;
        this.createdAt = createdAt;
        this.items = (items != null) ? items : new ArrayList<>();
    }

    public String getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(String shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getPurchasePlace() {
        return purchasePlace;
    }

    public void setPurchasePlace(String purchasePlace) {
        this.purchasePlace = purchasePlace;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getRowListId() {
        return rowListId;
    }

    public void setRowListId(String rowListId) {
        this.rowListId = rowListId;
    }

    public List<ShoppingRowListItem> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinalShoppingList that = (FinalShoppingList) o;
        // Use shoppingListId if available; fall back to rowListId for compatibility
        String idThis = shoppingListId != null ? shoppingListId : rowListId;
        String idThat = that.shoppingListId != null ? that.shoppingListId : that.rowListId;

        return Objects.equals(idThis, idThat)
                && Objects.equals(listName, that.listName)
                && Objects.equals(purchasePlace, that.purchasePlace)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        String id = shoppingListId != null ? shoppingListId : rowListId;
        return Objects.hash(id, listName, purchasePlace, createdAt);
    }
}
