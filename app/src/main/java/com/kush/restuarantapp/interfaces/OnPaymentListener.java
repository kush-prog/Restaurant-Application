package com.kush.restuarantapp.interfaces;

public interface OnPaymentListener {

    void onPaymentStarted(double amount);

    void onPaymentSuccess(String transactionId, double amount);

    void onPaymentFailed(String error, double amount);

    void onPaymentCancelled(double amount);
}