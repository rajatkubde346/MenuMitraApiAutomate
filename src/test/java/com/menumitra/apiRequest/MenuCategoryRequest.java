package com.menumitra.apiRequest;

import java.io.File;

public class MenuCategoryRequest {
    private int outletId;
    private String categoryName;
    private File image;
    private String userId;

    // Constructor
    public MenuCategoryRequest(int outletId, String categoryName, String userId) {
        this.outletId = outletId;
        this.categoryName = categoryName;
        this.userId = userId;
    }

    // Getters and Setters
    public int getOutletId() {
        return outletId;
    }

    public void setOutletId(int outletId) {
        this.outletId = outletId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Builder pattern for optional image
    public MenuCategoryRequest withImage(File image) {
        this.image = image;
        return this;
    }

    public MenuCategoryRequest withImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            this.image = new File(imagePath);
        }
        return this;
    }
}