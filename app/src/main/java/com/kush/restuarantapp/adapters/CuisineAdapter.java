package com.kush.restuarantapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kush.restuarantapp.CuisineDetailActivity;
import com.kush.restuarantapp.R;
import com.kush.restuarantapp.models.Cuisine;
import com.kush.restuarantapp.utils.Constants;
import com.kush.restuarantapp.utils.ImageLoader;
import java.util.List;

public class CuisineAdapter extends RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder> {
    private List<Cuisine> cuisines;
    private Context context;

    public CuisineAdapter(List<Cuisine> cuisines, Context context) {
        this.cuisines = cuisines;
        this.context = context;
    }

    @NonNull
    @Override
    public CuisineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuisine, parent, false);
        return new CuisineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CuisineViewHolder holder, int position) {
        Cuisine cuisine = cuisines.get(position);
        holder.bind(cuisine);
    }

    @Override
    public int getItemCount() {
        return cuisines != null ? cuisines.size() : 0;
    }

    class CuisineViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgCuisine;
        private TextView txtCuisineName;

        public CuisineViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCuisine = itemView.findViewById(R.id.imgCuisine);
            txtCuisineName = itemView.findViewById(R.id.txtCuisineName);
        }

        public void bind(Cuisine cuisine) {
            txtCuisineName.setText(cuisine.getCuisine_name());

            // Load cuisine image
            if (cuisine.getCuisineImage() != null && !cuisine.getCuisineImage().isEmpty()) {
                ImageLoader.loadImage(imgCuisine, cuisine.getCuisineImage());
            } else {
                imgCuisine.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CuisineDetailActivity.class);
                intent.putExtra(Constants.INTENT_CUISINE_NAME, cuisine.getCuisine_name());
                intent.putExtra(Constants.INTENT_CUISINE_ID, cuisine.getCuisine_id());
                context.startActivity(intent);
            });
        }
    }
}