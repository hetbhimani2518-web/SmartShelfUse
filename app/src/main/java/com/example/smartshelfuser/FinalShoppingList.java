package com.example.smartshelfuser;

import java.util.List;

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
    }

    public FinalShoppingList(String shoppingListId, String userId, String rowListId, String listName, String purchasePlace, String location, Long createdAt, List<ShoppingRowListItem> items) {
        this.shoppingListId = shoppingListId;
        this.userId = userId;
        this.rowListId = rowListId;
        this.listName = listName;
        this.purchasePlace = purchasePlace;
        this.location = location;
        this.createdAt = createdAt;
        this.items = items;
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

    public List<ShoppingRowListItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingRowListItem> items) {
        this.items = items;
    }

    public String getRowListId() {
        return rowListId;
    }

    public void setRowListId(String rowListId) {
        this.rowListId = rowListId;
    }
}
