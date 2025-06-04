package com.kush.restuarantapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kush.restuarantapp.CuisineDetailActivity;
import com.kush.restuarantapp.R;
import com.kush.restuarantapp.models.Item;
import com.kush.restuarantapp.utils.CartManager;
import com.kush.restuarantapp.utils.Constants;
import com.kush.restuarantapp.utils.ImageLoader;
import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private List<Item> dishes;
    private Context context;

    public DishAdapter(List<Item> dishes, Context context) {
        this.dishes = dishes;
        this.context = context;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Item dish = dishes.get(position);
        holder.bind(dish);
    }

    @Override
    public int getItemCount() {
        return dishes != null ? dishes.size() : 0;
    }

    class DishViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgDish;
        private TextView txtDishName, txtPrice, txtRating;
        private Button btnAddToCart;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish = itemView.findViewById(R.id.imgDish);
            txtDishName = itemView.findViewById(R.id.txtDishName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtRating = itemView.findViewById(R.id.txtRating);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        public void bind(Item dish) {
            txtDishName.setText(dish.getName());
            txtPrice.setText(dish.getPrice());
            txtRating.setText(dish.getRating());

            String imageUrl = dish.getImageUrl();
            Log.d(Constants.TAG, "Dish: " + dish.getName() + " - Image URL: " + imageUrl);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageLoader.loadImage(imgDish, imageUrl);
            } else {
                Log.d(Constants.TAG, "No image URL for dish: " + dish.getName());
                imgDish.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            btnAddToCart.setOnClickListener(v -> {
                try {
                    CartManager.getInstance().addItem(dish);
                    btnAddToCart.setText("Added!");
                    btnAddToCart.postDelayed(() -> btnAddToCart.setText("Add to Cart"), 1000);

                    if (context instanceof CuisineDetailActivity) {
                        ((CuisineDetailActivity) context).updateCartButton();
                    }

                    Log.d(Constants.TAG, "Added to cart: " + dish.getName());
                } catch (Exception e) {
                    Log.e(Constants.TAG, "Error adding to cart: " + e.getMessage());
                }
            });
        }
    }
}