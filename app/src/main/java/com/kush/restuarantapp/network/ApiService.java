package com.kush.restuarantapp.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.kush.restuarantapp.interfaces.ApiCallback;
import com.kush.restuarantapp.models.Cuisine;
import com.kush.restuarantapp.models.Item;
import com.kush.restuarantapp.models.PaymentRequest;
import com.kush.restuarantapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void getItemList(int page, int count, ApiCallback<List<Cuisine>> callback) {
        new Thread(() -> {
            try {
                Log.d(Constants.TAG, "Making API call for getItemList");

                JSONObject requestBody = new JSONObject();
                requestBody.put("page", page);
                requestBody.put("count", count);

                String response = makeApiCall(
                        Constants.GET_ITEMS_ENDPOINT,
                        Constants.ACTION_GET_ITEM_LIST,
                        requestBody.toString()
                );

                Log.d(Constants.TAG, "Parsing response...");
                List<Cuisine> cuisines = parseGetItemListResponse(response);

                Log.d(Constants.TAG, "Parsed " + cuisines.size() + " cuisines, switching to main thread");
                mainHandler.post(() -> {
                    Log.d(Constants.TAG, "Calling onSuccess on main thread");
                    callback.onSuccess(cuisines);
                });

            } catch (Exception e) {
                Log.e(Constants.TAG, "Error in getItemList: " + e.getMessage(), e);
                mainHandler.post(() -> {
                    Log.d(Constants.TAG, "Calling onError on main thread");
                    callback.onError("Failed to fetch cuisines: " + e.getMessage());
                });
            }
        }).start();
    }

    public static void getItemById(String itemId, ApiCallback<Item> callback) {
        new Thread(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("item_id", Integer.parseInt(itemId));

                String response = makeApiCall(
                        Constants.GET_ITEM_BY_ID_ENDPOINT,
                        Constants.ACTION_GET_ITEM_BY_ID,
                        requestBody.toString()
                );

                Item item = parseGetItemByIdResponse(response);
                mainHandler.post(() -> callback.onSuccess(item));

            } catch (Exception e) {
                Log.e(Constants.TAG, "Error in getItemById: " + e.getMessage());
                mainHandler.post(() -> callback.onError("Failed to fetch item: " + e.getMessage()));
            }
        }).start();
    }

    public static void getItemsByFilter(String cuisineName, ApiCallback<List<Cuisine>> callback) {
        new Thread(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                JSONArray cuisineTypes = new JSONArray();
                cuisineTypes.put(cuisineName);
                requestBody.put("cuisine_type", cuisineTypes);

                String response = makeApiCall(
                        Constants.GET_ITEM_BY_FILTER_ENDPOINT,
                        Constants.ACTION_GET_ITEM_BY_FILTER,
                        requestBody.toString()
                );

                List<Cuisine> cuisines = parseGetItemByFilterResponse(response);
                mainHandler.post(() -> callback.onSuccess(cuisines));

            } catch (Exception e) {
                Log.e(Constants.TAG, "Error in getItemsByFilter: " + e.getMessage());
                mainHandler.post(() -> callback.onError("Failed to fetch filtered items: " + e.getMessage()));
            }
        }).start();
    }

    public static void makePayment(PaymentRequest paymentRequest, ApiCallback<String> callback) {
        new Thread(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("total_amount", paymentRequest.getTotalAmount());
                requestBody.put("total_items", paymentRequest.getTotalItems());

                JSONArray dataArray = new JSONArray();
                for (PaymentRequest.PaymentItem item : paymentRequest.getData()) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("cuisine_id", item.getCuisineId());
                    itemObj.put("item_id", item.getItemId());
                    itemObj.put("item_price", item.getItemPrice());
                    itemObj.put("item_quantity", item.getItemQuantity());
                    dataArray.put(itemObj);
                }
                requestBody.put("data", dataArray);

                String response = makeApiCall(
                        Constants.MAKE_PAYMENT_ENDPOINT,
                        Constants.ACTION_MAKE_PAYMENT,
                        requestBody.toString()
                );

                String txnRefNo = parsePaymentResponse(response);
                mainHandler.post(() -> callback.onSuccess(txnRefNo));

            } catch (Exception e) {
                Log.e(Constants.TAG, "Error in makePayment: " + e.getMessage());
                mainHandler.post(() -> callback.onError("Payment failed: " + e.getMessage()));
            }
        }).start();
    }

    private static String makeApiCall(String endpoint, String action, String requestBody) throws IOException {
        URL url = new URL(Constants.BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty(Constants.API_KEY_HEADER, Constants.API_KEY_VALUE);
            connection.setRequestProperty(Constants.PROXY_ACTION_HEADER, action);
            connection.setRequestProperty(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE_VALUE);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            Log.d(Constants.TAG, "API Response Code: " + responseCode);

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseString = response.toString();
            Log.d(Constants.TAG, "API Response: " + responseString);

            if (responseCode != Constants.SUCCESS_CODE) {
                throw new IOException("HTTP error code: " + responseCode + ", Response: " + responseString);
            }

            return responseString;

        } finally {
            connection.disconnect();
        }
    }

    private static List<Cuisine> parseGetItemListResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        List<Cuisine> cuisines = new ArrayList<>();

        if (jsonResponse.getInt("response_code") == Constants.SUCCESS_CODE) {
            JSONArray cuisinesArray = jsonResponse.getJSONArray("cuisines");

            for (int i = 0; i < cuisinesArray.length(); i++) {
                JSONObject cuisineObj = cuisinesArray.getJSONObject(i);
                Cuisine cuisine = new Cuisine();
                cuisine.setCuisine_id(cuisineObj.getString("cuisine_id"));
                cuisine.setCuisine_name(cuisineObj.getString("cuisine_name"));
                cuisine.setCuisineImage(cuisineObj.getString("cuisine_image_url"));

                List<Item> items = new ArrayList<>();
                JSONArray itemsArray = cuisineObj.getJSONArray("items");

                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemObj = itemsArray.getJSONObject(j);
                    Item item = new Item(
                            itemObj.getString("id"),
                            itemObj.getString("name"),
                            itemObj.getString("image_url"),
                            "₹" + itemObj.getString("price"),
                            "⭐ " + itemObj.getString("rating")
                    );
                    items.add(item);
                }
                cuisine.setItems(items);
                cuisines.add(cuisine);
            }
        } else {
            throw new JSONException("API returned error: " + jsonResponse.getString("response_message"));
        }

        return cuisines;
    }

    private static Item parseGetItemByIdResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        if (jsonResponse.getInt("response_code") == Constants.SUCCESS_CODE) {
            return new Item(
                    String.valueOf(jsonResponse.getInt("item_id")),
                    jsonResponse.getString("item_name"),
                    jsonResponse.getString("item_image_url"),
                    "₹" + jsonResponse.getInt("item_price"),
                    "⭐ " + jsonResponse.getDouble("item_rating")
            );
        } else {
            throw new JSONException("API returned error: " + jsonResponse.getString("response_message"));
        }
    }

    private static List<Cuisine> parseGetItemByFilterResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        List<Cuisine> cuisines = new ArrayList<>();

        if (jsonResponse.getInt("response_code") == Constants.SUCCESS_CODE) {
            JSONArray cuisinesArray = jsonResponse.getJSONArray("cuisines");

            for (int i = 0; i < cuisinesArray.length(); i++) {
                JSONObject cuisineObj = cuisinesArray.getJSONObject(i);
                Cuisine cuisine = new Cuisine();
                cuisine.setCuisine_id(String.valueOf(cuisineObj.getInt("cuisine_id")));
                cuisine.setCuisine_name(cuisineObj.getString("cuisine_name"));
                cuisine.setCuisineImage(cuisineObj.getString("cuisine_image_url"));

                List<Item> items = new ArrayList<>();
                JSONArray itemsArray = cuisineObj.getJSONArray("items");

                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemObj = itemsArray.getJSONObject(j);
                    Item item = new Item(
                            String.valueOf(itemObj.getInt("id")),
                            itemObj.getString("name"),
                            itemObj.getString("image_url"),
                            "₹" + itemObj.getString("price"),
                            "⭐ " + itemObj.getString("rating")
                    );
                    items.add(item);
                }
                cuisine.setItems(items);
                cuisines.add(cuisine);
            }
        } else {
            throw new JSONException("API returned error: " + jsonResponse.getString("response_message"));
        }

        return cuisines;
    }

    private static String parsePaymentResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        if (jsonResponse.getInt("response_code") == Constants.SUCCESS_CODE) {
            return jsonResponse.getString("txn_ref_no");
        } else {
            throw new JSONException("Payment failed: " + jsonResponse.getString("response_message"));
        }
    }
}