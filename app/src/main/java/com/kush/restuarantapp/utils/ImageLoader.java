package com.kush.restuarantapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void loadImage(ImageView imageView, String imageUrl) {
        if (imageView == null) {
            Log.e(Constants.TAG, "ImageView is null");
            return;
        }

        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.d(Constants.TAG, "Image URL is empty, setting placeholder");
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        Log.d(Constants.TAG, "Loading image: " + imageUrl);

        // Set placeholder immediately
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);

        // Load image in background
        executor.execute(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = connection.getResponseCode();
                Log.d(Constants.TAG, "Image response code: " + responseCode + " for URL: " + imageUrl);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    mainHandler.post(() -> {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                            Log.d(Constants.TAG, "Image loaded successfully: " + imageUrl);
                        } else {
                            Log.e(Constants.TAG, "Failed to decode bitmap: " + imageUrl);
                            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                    });
                } else {
                    Log.e(Constants.TAG, "HTTP error " + responseCode + " for image: " + imageUrl);
                    mainHandler.post(() -> {
                        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    });
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e(Constants.TAG, "Error loading image: " + imageUrl + " - " + e.getMessage(), e);
                mainHandler.post(() -> {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                });
            }
        });
    }
}