package com.example.smartshelfuser;

public class FetchCategoryProduct {

    private String itemName;
    private String itemDescription;
    private String itemCategory;
    private String itemQuantity;
    private String itemUnit;
    private String addedDateTime;

    public FetchCategoryProduct(String itemName, String itemDescription, String itemCategory, String itemQuantity, String itemUnit, String addedDateTime) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCategory = itemCategory;
        this.itemQuantity = itemQuantity;
        this.itemUnit = itemUnit;
        this.addedDateTime = addedDateTime;
    }

    public FetchCategoryProduct() {}

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    public String getAddedDateTime() {
        return addedDateTime;
    }

    public void setAddedDateTime(String addedDateTime) {
        this.addedDateTime = addedDateTime;
    }
}

