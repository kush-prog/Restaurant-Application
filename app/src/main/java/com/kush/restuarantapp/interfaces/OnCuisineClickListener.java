package com.kush.restuarantapp.interfaces;

import com.kush.restuarantapp.models.Cuisine;

public interface OnCuisineClickListener {

    void onCuisineClick(Cuisine cuisine);

    default boolean onCuisineLongClick(Cuisine cuisine) {
        return false;
    }
}