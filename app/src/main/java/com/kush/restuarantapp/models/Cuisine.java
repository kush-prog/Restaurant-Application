package com.kush.restuarantapp.models;

import java.util.List;

public class Cuisine {
    private String cuisine_id;
    private String cuisine_name;
    private String cuisineImage;
    private List<Item> items;

    public Cuisine() {}

    public Cuisine(String cuisine_id, String cuisine_name, String cuisineImage, List<Item> items) {
        this.cuisine_id = cuisine_id;
        this.cuisine_name = cuisine_name;
        this.cuisineImage = cuisineImage;
        this.items = items;
    }

    public String getCuisine_id() {
        return cuisine_id;
    }

    public void setCuisine_id(String cuisine_id) {
        this.cuisine_id = cuisine_id;
    }

    public String getCuisine_name() {
        return cuisine_name;
    }

    public void setCuisine_name(String cuisine_name) {
        this.cuisine_name = cuisine_name;
    }

    public String getCuisineImage() {
        return cuisineImage;
    }

    public void setCuisineImage(String cuisineImage) {
        this.cuisineImage = cuisineImage;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}