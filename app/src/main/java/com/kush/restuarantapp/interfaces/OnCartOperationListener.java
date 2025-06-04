package com.kush.restuarantapp.interfaces;

import com.kush.restuarantapp.models.Item;

public interface OnCartOperationListener {

    void onCartUpdated(int totalItems, double totalAmount);

    void onItemAdded(Item item, int quantity);

    void onItemRemoved(Item item);

    void onQuantityUpdated(Item item, int oldQuantity, int newQuantity);

    void onCartCleared();
}