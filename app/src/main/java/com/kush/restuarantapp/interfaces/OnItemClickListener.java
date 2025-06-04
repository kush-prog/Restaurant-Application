package com.kush.restuarantapp.interfaces;

import com.kush.restuarantapp.models.Item;

public interface OnItemClickListener {

    void onItemClick(Item item);

    void onAddToCart(Item item);

    default void onRemoveFromCart(Item item) {
    }

    default void onIncreaseQuantity(Item item) {
    }

    default void onDecreaseQuantity(Item item) {
    }

    default boolean onItemLongClick(Item item) {
        return false;
    }

    default void onRatingClick(Item item, float rating) {
    }
}