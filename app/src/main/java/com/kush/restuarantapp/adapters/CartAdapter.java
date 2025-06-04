package com.kush.restuarantapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kush.restuarantapp.R;
import com.kush.restuarantapp.models.Item;
import com.kush.restuarantapp.utils.CartManager;
import com.kush.restuarantapp.utils.ImageLoader;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Item> cartItems;
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(List<Item> cartItems, OnCartUpdateListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Item item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgDish;
        private TextView txtDishName, txtPrice, txtQuantity;
        private Button btnMinus, btnPlus, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish = itemView.findViewById(R.id.imgDish);
            txtDishName = itemView.findViewById(R.id.txtDishName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(Item item) {
            ImageLoader.loadImage(imgDish, item.getImageUrl());
            txtDishName.setText(item.getName());
            txtPrice.setText(item.getPrice());
            txtQuantity.setText(String.valueOf(item.getQuantity()));

            btnPlus.setOnClickListener(v -> {
                CartManager.getInstance().addItem(item);
                txtQuantity.setText(String.valueOf(item.getQuantity()));
                if (listener != null) {
                    listener.onCartUpdated();
                }
            });

            btnMinus.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    txtQuantity.setText(String.valueOf(item.getQuantity()));
                    if (listener != null) {
                        listener.onCartUpdated();
                    }
                }
            });

            btnRemove.setOnClickListener(v -> {
                CartManager.getInstance().removeItem(item);
                cartItems.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                if (listener != null) {
                    listener.onCartUpdated();
                }
            });
        }
    }
}