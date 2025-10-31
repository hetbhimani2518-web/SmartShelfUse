package com.example.smartshelfuser;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ShoppingRowList {

    public String rowListId;
    public String userId;
    public String listName;
    public String purchasePlace;
    public Long createdAt;   // store timestamp in millis
    public String location;

    public ShoppingRowList() {
    }

    public ShoppingRowList(String rowListId, String userId, String listName, String purchasePlace, Long createdAt, String location) {
        this.rowListId = rowListId;
        this.userId = userId;
        this.listName = listName;
        this.purchasePlace = purchasePlace;
        this.createdAt = createdAt;
        this.location = location;
    }

    public String getRowListId() {
        return rowListId;
    }

    public void setRowListId(String rowListId) {
        this.rowListId = rowListId;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
