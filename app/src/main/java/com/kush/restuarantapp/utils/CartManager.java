package com.kush.restuarantapp.utils;

import android.util.Log;

import com.kush.restuarantapp.models.Item;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Item> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(Item item) {
        boolean found = false;
        for (Item cartItem : cartItems) {
            if (cartItem.getId().equals(item.getId())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            Item newItem = new Item(item.getId(), item.getName(), item.getImageUrl(), item.getPrice(), item.getRating());
            newItem.setQuantity(1);
            cartItems.add(newItem);
        }
    }

    public void removeItem(Item item) {
        cartItems.removeIf(cartItem -> cartItem.getId().equals(item.getId()));
    }

    public void removeItem(String itemId) {
        cartItems.removeIf(cartItem -> cartItem.getId().equals(itemId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<Item> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getCartSize() {
        return cartItems.size();
    }

    public boolean isInCart(String itemId) {
        return cartItems.stream().anyMatch(item -> item.getId().equals(itemId));
    }

    public int getItemQuantity(String itemId) {
        for (Item item : cartItems) {
            if (item.getId().equals(itemId)) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public int getTotalItems() {
        int total = 0;
        for (Item item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public double getSubTotal() {
        double total = 0;
        for (Item item : cartItems) {
            total += item.getPriceAsDouble() * item.getQuantity();
        }
        return total;
    }

    public double getCGST() {
        return getSubTotal() * Constants.CGST_RATE;
    }

    public double getSGST() {
        return getSubTotal() * Constants.SGST_RATE;
    }

    public double getGrandTotal() {
        return getSubTotal() + getCGST() + getSGST();
    }

    public int getCartItemCount() {
        try {
            if (cartItems == null) {
                return 0;
            }

            int totalCount = 0;
            for (Item item : cartItems) {
                totalCount += item.getQuantity();
            }

            return totalCount;
        } catch (Exception e) {
            Log.e("CartManager", "Error getting cart item count: " + e.getMessage());
            return 0;
        }
    }

    public int getUniqueItemCount() {
        try {
            return cartItems != null ? cartItems.size() : 0;
        } catch (Exception e) {
            Log.e("CartManager", "Error getting unique item count: " + e.getMessage());
            return 0;
        }
    }
}