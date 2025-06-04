package com.kush.restuarantapp.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PaymentRequest {
    private double totalAmount;
    private int totalItems;
    private List<PaymentItem> data;

    public PaymentRequest() {}

    public PaymentRequest(double totalAmount, int totalItems, List<PaymentItem> data) {
        this.totalAmount = fixAmount(totalAmount);
        this.totalItems = totalItems;
        this.data = data;
    }

    private double fixAmount(double amount) {
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedAmount = bd.doubleValue();

        if (roundedAmount < 1.0) {
            return 1.0;
        } else if (roundedAmount > 10000.0) {
            return 10000.0;
        }

        return roundedAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = fixAmount(totalAmount);
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<PaymentItem> getData() {
        return data;
    }

    public void setData(List<PaymentItem> data) {
        this.data = data;
    }

    public static class PaymentItem {
        private String cuisineId;
        private String itemId;
        private double itemPrice;
        private int itemQuantity;

        public PaymentItem() {}

        public PaymentItem(String cuisineId, String itemId, double itemPrice, int itemQuantity) {
            this.cuisineId = cuisineId;
            this.itemId = itemId;
            this.itemPrice = fixItemPrice(itemPrice);
            this.itemQuantity = itemQuantity;
        }

        private double fixItemPrice(double price) {
            BigDecimal bd = new BigDecimal(price);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        public String getCuisineId() { return cuisineId; }
        public void setCuisineId(String cuisineId) { this.cuisineId = cuisineId; }

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }

        public double getItemPrice() { return itemPrice; }
        public void setItemPrice(double itemPrice) {
            this.itemPrice = fixItemPrice(itemPrice);
        }

        public int getItemQuantity() { return itemQuantity; }
        public void setItemQuantity(int itemQuantity) { this.itemQuantity = itemQuantity; }
    }
}