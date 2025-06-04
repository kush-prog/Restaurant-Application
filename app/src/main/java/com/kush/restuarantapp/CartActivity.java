package com.kush.restuarantapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kush.restuarantapp.adapters.CartAdapter;
import com.kush.restuarantapp.interfaces.ApiCallback;
import com.kush.restuarantapp.models.Item;
import com.kush.restuarantapp.models.PaymentRequest;
import com.kush.restuarantapp.network.ApiService;
import com.kush.restuarantapp.utils.CartManager;
import com.kush.restuarantapp.utils.Constants;
import com.kush.restuarantapp.utils.LocaleHelper;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {
    private RecyclerView recyclerCartItems;
    private TextView txtSubtotal, txtCGST, txtSGST, txtGrandTotal;
    private Button btnPlaceOrder;
    private CartAdapter cartAdapter;
    private List<Item> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.applyLanguage(this, LocaleHelper.getLanguage(this));
        setContentView(R.layout.activity_cart);

        initViews();
        setupRecyclerView();
        updateUI();
        setupClickListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.cart));
        }

        recyclerCartItems = findViewById(R.id.recyclerCartItems);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtCGST = findViewById(R.id.txtCGST);
        txtSGST = findViewById(R.id.txtSGST);
        txtGrandTotal = findViewById(R.id.txtGrandTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void setupRecyclerView() {
        cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartItems.setAdapter(cartAdapter);
    }

    private void updateUI() {
        CartManager cartManager = CartManager.getInstance();

        txtSubtotal.setText("₹" + String.format("%.2f", cartManager.getSubTotal()));
        txtCGST.setText("₹" + String.format("%.2f", cartManager.getCGST()));
        txtSGST.setText("₹" + String.format("%.2f", cartManager.getSGST()));
        txtGrandTotal.setText("₹" + String.format("%.2f", cartManager.getGrandTotal()));

        btnPlaceOrder.setEnabled(cartItems.size() > 0);

        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
    }

    private void setupClickListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        try {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoading(true);

            List<PaymentRequest.PaymentItem> paymentItems = new ArrayList<>();
            for (Item item : cartItems) {
                String priceStr = item.getPrice().replace("₹", "").replace(",", "");
                double itemPrice = Double.parseDouble(priceStr);

                PaymentRequest.PaymentItem paymentItem = new PaymentRequest.PaymentItem(
                        "1",
                        item.getId(),
                        itemPrice,
                        item.getQuantity()
                );
                paymentItems.add(paymentItem);
            }

            double calculatedTotal = calculateGrandTotal();

            PaymentRequest paymentRequest = new PaymentRequest(
                    calculatedTotal,
                    cartItems.size(),
                    paymentItems
            );

            Log.d(Constants.TAG, "Payment Request - Amount: " + paymentRequest.getTotalAmount() +
                    ", Items: " + paymentRequest.getTotalItems());

            ApiService.makePayment(paymentRequest, new ApiCallback<String>() {
                @Override
                public void onSuccess(String txnRefNo) {
                    showLoading(false);
                    showPaymentSuccess(txnRefNo);
                    clearCart();
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Log.e(Constants.TAG, "Payment Error: " + error);
                    Toast.makeText(CartActivity.this, "Payment failed: " + error, Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            showLoading(false);
            Log.e(Constants.TAG, "Error in placeOrder: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing order: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showLoading(boolean show) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        if (btnPlaceOrder != null) {
            btnPlaceOrder.setEnabled(!show);
            btnPlaceOrder.setText(show ? "Processing..." : "Place Order");
        }
    }

    private double calculateGrandTotal() {
        double subtotal = 0.0;

        try {
            for (Item item : cartItems) {
                // Extract numeric price (remove ₹ symbol and any commas)
                String priceStr = item.getPrice().replace("₹", "").replace(",", "").trim();
                double itemPrice = Double.parseDouble(priceStr);
                subtotal += itemPrice * item.getQuantity();
            }

            // Add taxes (CGST 2.5% + SGST 2.5% = 5% total)
            double cgst = subtotal * 0.025;
            double sgst = subtotal * 0.025;
            double grandTotal = subtotal + cgst + sgst;

            return grandTotal;

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error calculating total: " + e.getMessage());
            return 0.0;
        }
    }

    private void showPaymentSuccess(String txnRefNo) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Payment Successful!")
                    .setMessage("Your order has been placed successfully.\n\nTransaction ID: " + txnRefNo)
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        finish(); // Close cart activity
                    })
                    .setCancelable(false)
                    .show();

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error showing success dialog: " + e.getMessage());
            Toast.makeText(this, "Payment successful! Transaction ID: " + txnRefNo, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void clearCart() {
        try {
            cartItems.clear();

            if (CartManager.getInstance() != null) {
                CartManager.getInstance().clearCart();
            }
            if (cartAdapter != null) {
                cartAdapter.notifyDataSetChanged();
            }

            updateTotals();

            Log.d(Constants.TAG, "Cart cleared successfully");

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error clearing cart: " + e.getMessage());
        }
    }

    private void updateTotals() {
        try {
            double subtotal = 0.0;
            for (Item item : cartItems) {
                String priceStr = item.getPrice().replace("₹", "").replace(",", "").trim();
                double itemPrice = Double.parseDouble(priceStr);
                subtotal += itemPrice * item.getQuantity();
            }

            double cgst = subtotal * 0.025;
            double sgst = subtotal * 0.025;
            double grandTotal = subtotal + cgst + sgst;

            TextView txtSubtotal = findViewById(R.id.txtSubtotal);
            TextView txtCgst = findViewById(R.id.txtCGST);
            TextView txtSgst = findViewById(R.id.txtSGST);
            TextView txtGrandTotal = findViewById(R.id.txtGrandTotal);

            if (txtSubtotal != null) txtSubtotal.setText("₹" + String.format("%.2f", subtotal));
            if (txtCgst != null) txtCgst.setText("₹" + String.format("%.2f", cgst));
            if (txtSgst != null) txtSgst.setText("₹" + String.format("%.2f", sgst));
            if (txtGrandTotal != null) txtGrandTotal.setText("₹" + String.format("%.2f", grandTotal));

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error updating totals: " + e.getMessage());
        }
    }

    @Override
    public void onCartUpdated() {
        updateUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}