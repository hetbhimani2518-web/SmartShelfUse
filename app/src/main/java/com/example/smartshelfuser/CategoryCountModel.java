package com.example.smartshelfuser;

public class CategoryCountModel {

    private String categoryName;
    private int count;

    public CategoryCountModel() {}

    public CategoryCountModel(String categoryName, int count) {
        this.categoryName = categoryName;
        this.count = count;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCount() {
        return count;
    }
}
