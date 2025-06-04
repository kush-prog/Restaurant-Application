package com.kush.restuarantapp.interfaces;

public interface ApiCallback<T> {

    void onSuccess(T result);

    void onError(String error);

    default void onStart() {
    }

    default void onComplete() {
    }
}