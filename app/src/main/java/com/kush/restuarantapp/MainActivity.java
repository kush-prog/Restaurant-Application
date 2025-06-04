package com.kush.restuarantapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kush.restuarantapp.interfaces.ApiCallback;
import com.kush.restuarantapp.models.Cuisine;
import com.kush.restuarantapp.models.Item;
import com.kush.restuarantapp.network.ApiService;
import com.kush.restuarantapp.utils.CartManager;
import com.kush.restuarantapp.utils.Constants;
import com.kush.restuarantapp.utils.ImageLoader;
import com.kush.restuarantapp.utils.LocaleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layoutCuisines;
    private GridLayout gridTopDishes;
    private Button btnCart, btnLanguage;
    private List<Cuisine> cuisineList;
    private List<Item> topDishes;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Initialize executors first
            executorService = Executors.newFixedThreadPool(3);
            mainHandler = new Handler(Looper.getMainLooper());

            // Apply language quickly
            LocaleHelper.applyLanguage(this, LocaleHelper.getLanguage(this));
            setContentView(R.layout.activity_main);

            // Initialize views on main thread
            initViews();

            // Load data in background
            loadDataInBackground();

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error in onCreate: " + e.getMessage(), e);
        }
    }

    private void initViews() {
        try {
            layoutCuisines = findViewById(R.id.layoutCuisines);
            gridTopDishes = findViewById(R.id.gridTopDishes);
            btnCart = findViewById(R.id.btnCart);
            btnLanguage = findViewById(R.id.btnLanguage);

            cuisineList = new ArrayList<>();
            topDishes = new ArrayList<>();

            // Set click listeners
            if (btnLanguage != null) {
                btnLanguage.setOnClickListener(v -> toggleLanguage());
            }

            setupCartButton();

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error in initViews: " + e.getMessage(), e);
        }
    }

    private void loadDataInBackground() {
        // Load cuisines in background thread
        executorService.execute(() -> {
            try {
                loadCuisinesBackground();
            } catch (Exception e) {
                Log.e(Constants.TAG, "Error loading cuisines in background: " + e.getMessage(), e);
                mainHandler.post(() -> loadFallbackCuisines());
            }
        });

        // Load top dishes in background thread
        executorService.execute(() -> {
            try {
                loadTopDishesBackground();
            } catch (Exception e) {
                Log.e(Constants.TAG, "Error loading top dishes in background: " + e.getMessage(), e);
            }
        });
    }

    private void loadCuisinesBackground() {
        Log.d(Constants.TAG, "Loading cuisines in background...");

        ApiService.getItemList(1, 10, new ApiCallback<List<Cuisine>>() {
            @Override
            public void onSuccess(List<Cuisine> cuisines) {
                mainHandler.post(() -> {
                    try {
                        Log.d(Constants.TAG, "API returned " + cuisines.size() + " cuisines");
                        cuisineList.clear();
                        cuisineList.addAll(cuisines);

                        if (cuisines.size() < 5) {
                            addMissingCuisines();
                        }

                        displayCuisines();
                    } catch (Exception e) {
                        Log.e(Constants.TAG, "Error processing cuisines: " + e.getMessage(), e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(Constants.TAG, "Error loading cuisines: " + error);
                mainHandler.post(() -> loadFallbackCuisines());
            }
        });
    }

    private void loadFallbackCuisines() {
        try {
            cuisineList.clear();

            // Simplified cuisine creation
            String[][] cuisineData = {
                    {"1", "North Indian", "https://uat-static.onebanc.ai/picture/ob_cuisine_north_indian.webp"},
                    {"2", "Chinese", "https://uat-static.onebanc.ai/picture/ob_cuisine_chinese.webp"},
                    {"3", "Mexican", ""},
                    {"4", "South Indian", ""},
                    {"5", "Italian", ""}
            };

            for (String[] data : cuisineData) {
                Cuisine cuisine = new Cuisine();
                cuisine.setCuisine_id(data[0]);
                cuisine.setCuisine_name(data[1]);
                cuisine.setCuisineImage(data[2]);
                cuisineList.add(cuisine);
            }

            displayCuisines();
            Log.d(Constants.TAG, "Loaded fallback cuisines");

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error loading fallback cuisines: " + e.getMessage(), e);
        }
    }

    private void addMissingCuisines() {
        // Simplified - only add if not present
        List<String> existingNames = new ArrayList<>();
        for (Cuisine c : cuisineList) {
            existingNames.add(c.getCuisine_name());
        }

        String[] required = {"North Indian", "Chinese", "Mexican", "South Indian", "Italian"};
        int id = 100;

        for (String name : required) {
            if (!existingNames.contains(name)) {
                Cuisine cuisine = new Cuisine();
                cuisine.setCuisine_id(String.valueOf(id++));
                cuisine.setCuisine_name(name);
                cuisine.setCuisineImage("");
                cuisineList.add(cuisine);
            }
        }
    }

    private void displayCuisines() {
        try {
            if (layoutCuisines == null) return;

            layoutCuisines.removeAllViews();

            // Simplified responsive sizing
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int cardWidth = Math.min(screenWidth / 2, 200);
            int cardHeight = (int) (cardWidth * 0.6);

            for (Cuisine cuisine : cuisineList) {
                View cuisineView = LayoutInflater.from(this).inflate(R.layout.item_cuisine, null);

                // Set layout params
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, cardHeight);
                params.setMarginEnd(16);
                cuisineView.setLayoutParams(params);

                // Set data
                ImageView imgCuisine = cuisineView.findViewById(R.id.imgCuisine);
                TextView txtCuisineName = cuisineView.findViewById(R.id.txtCuisineName);

                if (txtCuisineName != null) {
                    txtCuisineName.setText(cuisine.getCuisine_name());
                }

                // Load image in background
                if (imgCuisine != null && cuisine.getCuisineImage() != null && !cuisine.getCuisineImage().isEmpty()) {
                    ImageLoader.loadImage(imgCuisine, cuisine.getCuisineImage());
                } else if (imgCuisine != null) {
                    imgCuisine.setImageResource(android.R.drawable.ic_menu_gallery);
                }

                // Set click listener
                cuisineView.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, CuisineDetailActivity.class);
                    intent.putExtra(Constants.INTENT_CUISINE_NAME, cuisine.getCuisine_name());
                    intent.putExtra(Constants.INTENT_CUISINE_ID, cuisine.getCuisine_id());
                    startActivity(intent);
                });

                layoutCuisines.addView(cuisineView);
            }

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error displaying cuisines: " + e.getMessage(), e);
        }
    }

    private void loadTopDishesBackground() {
        // Load top dishes data
        List<Item> dishes = new ArrayList<>();
        dishes.add(new Item("1", "Butter Chicken", "https://uat-static.onebanc.ai/picture/ob_dish_butter_chicken.webp", "₹199", "⭐ 4.5"));
        dishes.add(new Item("2", "Sweet and Sour Chicken", "https://uat-static.onebanc.ai/picture/ob_dish_sweet_and_sour_chicken.webp", "₹250", "⭐ 4.5"));
        dishes.add(new Item("3", "Paneer Tikka", "https://uat-static.onebanc.ai/picture/ob_dish_panner_tikka.webp", "₹150", "⭐ 4.0"));

        // Update UI on main thread
        mainHandler.post(() -> {
            topDishes.clear();
            topDishes.addAll(dishes);
            displayTopDishes();
        });
    }

    private void displayTopDishes() {
        try {
            if (gridTopDishes == null) return;

            gridTopDishes.removeAllViews();

            for (int i = 0; i < Math.min(topDishes.size(), 4); i++) {
                Item dish = topDishes.get(i);
                View dishView = LayoutInflater.from(this).inflate(R.layout.item_top_dish, null);

                ImageView imgDish = dishView.findViewById(R.id.imgDish);
                TextView txtDishName = dishView.findViewById(R.id.txtDishName);
                TextView txtPrice = dishView.findViewById(R.id.txtPrice);
                TextView txtRating = dishView.findViewById(R.id.txtRating);

                if (txtDishName != null) txtDishName.setText(dish.getName());
                if (txtPrice != null) txtPrice.setText(dish.getPrice());
                if (txtRating != null) txtRating.setText(dish.getRating());

                if (imgDish != null && dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
                    ImageLoader.loadImage(imgDish, dish.getImageUrl());
                }

                dishView.setOnClickListener(v -> {
                    try {
                        CartManager.getInstance().addItem(dish);
                        Toast.makeText(this, dish.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                        updateCartButton();
                    } catch (Exception e) {
                        Log.e(Constants.TAG, "Error adding to cart: " + e.getMessage());
                    }
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(i % 2, 1, 1f);
                params.rowSpec = GridLayout.spec(i / 2);
                dishView.setLayoutParams(params);

                gridTopDishes.addView(dishView);
            }

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error displaying top dishes: " + e.getMessage(), e);
        }
    }

    private void setupCartButton() {
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            });
            updateCartButton();
        }
    }

    private void updateCartButton() {
        try {
            if (btnCart != null && CartManager.getInstance() != null) {
                int cartCount = CartManager.getInstance().getUniqueItemCount();
                if (cartCount > 0) {
                    btnCart.setText("Cart (" + cartCount + ")");
                } else {
                    btnCart.setText("Cart");
                }
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "Error updating cart button: " + e.getMessage());
        }
    }

    private void toggleLanguage() {
        try {
            String currentLang = LocaleHelper.getLanguage(this);
            String newLang = currentLang.equals("en") ? "hi" : "en";
            LocaleHelper.setLanguage(this, newLang);
            if (btnLanguage != null) {
                btnLanguage.setText(newLang.equals("en") ? "English" : "हिंदी");
            }
            recreate();
        } catch (Exception e) {
            Log.e(Constants.TAG, "Error toggling language: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}