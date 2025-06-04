package com.kush.restuarantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kush.restuarantapp.adapters.DishAdapter;
import com.kush.restuarantapp.interfaces.ApiCallback;
import com.kush.restuarantapp.models.Cuisine;
import com.kush.restuarantapp.models.Item;
import com.kush.restuarantapp.network.ApiService;
import com.kush.restuarantapp.utils.CartManager;
import com.kush.restuarantapp.utils.Constants;
import com.kush.restuarantapp.utils.LocaleHelper;
import java.util.ArrayList;
import java.util.List;

public class CuisineDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerDishes;
    private ProgressBar progressBar;
    private DishAdapter dishAdapter;
    private List<Item> dishList;
    private String cuisineName;
    private String cuisineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.applyLanguage(this, LocaleHelper.getLanguage(this));
        setContentView(R.layout.activity_cuisine_detail);

        initViews();
        getCuisineData();
        loadDishes();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerDishes = findViewById(R.id.recyclerDishes);
        progressBar = findViewById(R.id.progressBar);

        Button btnViewCart = findViewById(R.id.btnViewCart);
        if (btnViewCart != null) {
            btnViewCart.setOnClickListener(v -> openCartActivity());
        }

        dishList = new ArrayList<>();
        dishAdapter = new DishAdapter(dishList, this);
        recyclerDishes.setLayoutManager(new LinearLayoutManager(this));
        recyclerDishes.setAdapter(dishAdapter);
    }

    private void getCuisineData() {
        cuisineName = getIntent().getStringExtra(Constants.INTENT_CUISINE_NAME);
        cuisineId = getIntent().getStringExtra(Constants.INTENT_CUISINE_ID);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(cuisineName != null ? cuisineName : "Cuisine Details");
        }

        Log.d(Constants.TAG, "Cuisine: " + cuisineName + ", ID: " + cuisineId);
    }

    private void loadDishes() {
        if (cuisineName == null) {
            Toast.makeText(this, "Invalid cuisine data", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        ArrayList<Item> passedDishes = getIntent().getParcelableArrayListExtra("CUISINE_DISHES");

        if (passedDishes != null && !passedDishes.isEmpty()) {
            Log.d(Constants.TAG, "Using dishes from MainActivity: " + passedDishes.size() + " dishes");
            dishList.clear();
            dishList.addAll(passedDishes);
            dishAdapter.notifyDataSetChanged();
            showLoading(false);
            Toast.makeText(this, "Loaded " + dishList.size() + " dishes", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(Constants.TAG, "No dishes passed from MainActivity, trying filter API for: " + cuisineName);

        ApiService.getItemsByFilter(cuisineName, new ApiCallback<List<Cuisine>>() {
            @Override
            public void onSuccess(List<Cuisine> cuisines) {
                try {
                    showLoading(false);
                    dishList.clear();

                    if (cuisines != null && !cuisines.isEmpty()) {
                        for (Cuisine cuisine : cuisines) {
                            if (cuisine.getItems() != null) {
                                dishList.addAll(cuisine.getItems());
                            }
                        }
                    }

                    if (dishList.isEmpty()) {
                        Log.d(Constants.TAG, "Filter API returned no dishes, loading fallback dishes");
                        loadFallbackDishes(cuisineName);
                    } else {
                        dishAdapter.notifyDataSetChanged();
                        Toast.makeText(CuisineDetailActivity.this, "Loaded " + dishList.size() + " dishes from API", Toast.LENGTH_SHORT).show();
                        Log.d(Constants.TAG, "Loaded " + dishList.size() + " dishes from filter API");
                    }

                } catch (Exception e) {
                    Log.e(Constants.TAG, "Error processing filter API dishes: " + e.getMessage(), e);
                    loadFallbackDishes(cuisineName);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(Constants.TAG, "Filter API Error: " + error);
                showLoading(false);
                loadFallbackDishes(cuisineName);
            }
        });
    }

    private void loadFallbackDishes(String cuisineName) {
        try {
            Log.d(Constants.TAG, "Loading fallback dishes for " + cuisineName);
            dishList.clear();

            switch (cuisineName) {
                case "North Indian":
                    dishList.add(new Item("1", "Butter Chicken", "https://uat-static.onebanc.ai/picture/ob_dish_butter_chicken.webp", "₹199", "⭐ 4.5"));
                    dishList.add(new Item("2", "Paneer Tikka", "https://uat-static.onebanc.ai/picture/ob_dish_panner_tikka.webp", "₹150", "⭐ 4.0"));
                    dishList.add(new Item("3", "Aloo Gobhi", "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=400", "₹120", "⭐ 4.2"));
                    dishList.add(new Item("4", "Naan", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400", "₹40", "⭐ 4.6"));
                    dishList.add(new Item("5", "Dal Makhani", "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400", "₹130", "⭐ 4.4"));
                    break;

                case "Chinese":
                    dishList.add(new Item("6", "Sweet and Sour Chicken", "https://uat-static.onebanc.ai/picture/ob_dish_sweet_and_sour_chicken.webp", "₹250", "⭐ 4.5"));
                    dishList.add(new Item("7", "Chowmein", "https://images.unsplash.com/photo-1555126634-323283e090fa?w=400", "₹150", "⭐ 4.0"));
                    dishList.add(new Item("8", "Spring Rolls", "https://images.unsplash.com/photo-1563379091639-cdcb3c741f6e?w=400", "₹120", "⭐ 4.3"));
                    dishList.add(new Item("9", "Fried Rice", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400", "₹180", "⭐ 4.2"));
                    dishList.add(new Item("10", "Manchurian", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400", "₹160", "⭐ 4.1"));
                    break;

                case "Mexican":
                    dishList.add(new Item("101", "Chicken Tacos", "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400", "₹180", "⭐ 4.3"));
                    dishList.add(new Item("102", "Burrito Bowl", "https://images.unsplash.com/photo-1553979459-d2229ba7433a?w=400", "₹200", "⭐ 4.4"));
                    dishList.add(new Item("103", "Nachos", "https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?w=400", "₹140", "⭐ 4.0"));
                    dishList.add(new Item("104", "Quesadilla", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400", "₹160", "⭐ 4.2"));
                    dishList.add(new Item("105", "Guacamole", "https://images.unsplash.com/photo-1553621042-f6e147245754?w=400", "₹80", "⭐ 4.1"));
                    break;

                case "South Indian":
                    dishList.add(new Item("201", "Masala Dosa", "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=400", "₹80", "⭐ 4.6"));
                    dishList.add(new Item("202", "Idli Sambar", "https://images.unsplash.com/photo-1630383249896-424e482df921?w=400", "₹60", "⭐ 4.3"));
                    dishList.add(new Item("203", "Uttapam", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400", "₹70", "⭐ 4.2"));
                    dishList.add(new Item("204", "Vada", "https://images.unsplash.com/photo-1626132647523-66f6bf6d6c99?w=400", "₹50", "⭐ 4.0"));
                    dishList.add(new Item("205", "Rasam", "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=400", "₹40", "⭐ 4.3"));
                    break;

                case "Italian":
                    dishList.add(new Item("301", "Margherita Pizza", "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=400", "₹280", "⭐ 4.7"));
                    dishList.add(new Item("302", "Pasta Alfredo", "https://images.unsplash.com/photo-1621996346565-e3dbc353d2e5?w=400", "₹220", "⭐ 4.4"));
                    dishList.add(new Item("303", "Garlic Bread", "https://images.unsplash.com/photo-1573821663912-6df460f9c684?w=400", "₹90", "⭐ 4.1"));
                    dishList.add(new Item("304", "Lasagna", "https://images.unsplash.com/photo-1574894709920-11b28e7367e3?w=400", "₹300", "⭐ 4.6"));
                    dishList.add(new Item("305", "Tiramisu", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400", "₹150", "⭐ 4.5"));
                    break;

                default:
                    dishList.add(new Item("999", "Sample Dish", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400", "₹100", "⭐ 4.0"));
                    break;
            }

            dishAdapter.notifyDataSetChanged();

            if (dishList.isEmpty()) {
                Toast.makeText(this, "No dishes available for " + cuisineName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Loaded " + dishList.size() + " sample dishes", Toast.LENGTH_SHORT).show();
            }

            Log.d(Constants.TAG, "Loaded " + dishList.size() + " fallback dishes for " + cuisineName);

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error loading fallback dishes: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading dishes", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerDishes != null) {
            recyclerDishes.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void openCartActivity() {
        try {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            Log.d(Constants.TAG, "Opening cart activity");
        } catch (Exception e) {
            Log.e(Constants.TAG, "Error opening cart: " + e.getMessage(), e);
            Toast.makeText(this, "Error opening cart", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateCartButton() {
        try {
            Button btnViewCart = findViewById(R.id.btnViewCart);
            if (btnViewCart != null && CartManager.getInstance() != null) {
                int cartCount = CartManager.getInstance().getUniqueItemCount();
                if (cartCount > 0) {
                    btnViewCart.setText("View Cart (" + cartCount + ")");
                } else {
                    btnViewCart.setText("View Cart");
                }
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "Error updating cart button: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButton();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}