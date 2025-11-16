package com.example.smartshelfuser;

import java.util.List;

public class FetchCategory {
    private String categoryName;
    private List<FetchCategoryProduct> products;
    private boolean isExpanded;

    public FetchCategory(String categoryName, List<FetchCategoryProduct> products, boolean isExpanded) {
        this.categoryName = categoryName;
        this.products = products;
        this.isExpanded = isExpanded;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<FetchCategoryProduct> getProducts() {
        return products;
    }

    public void setProducts(List<FetchCategoryProduct> products) {
        this.products = products;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
