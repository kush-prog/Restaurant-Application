package com.kush.restuarantapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.kush.restuarantapp.utils.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public static String getResponseString(HttpURLConnection connection) throws IOException {
        BufferedReader reader = null;
        try {
            int responseCode = connection.getResponseCode();
            Log.d(Constants.TAG, "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            Log.d(Constants.TAG, "Response: " + response.toString());
            return response.toString();

        } finally {
            if (reader != null) {
                reader.close();
            }
            connection.disconnect();
        }
    }

    public static boolean isValidResponse(String response) {
        return response != null && !response.trim().isEmpty();
    }

    public static String getErrorMessage(int responseCode) {
        switch (responseCode) {
            case 400:
                return "Bad Request - Invalid parameters";
            case 401:
                return "Unauthorized - Invalid API key";
            case 404:
                return "Not Found - Resource not available";
            case 500:
                return "Internal Server Error - Please try again later";
            case 503:
                return "Service Unavailable - Server is temporarily down";
            default:
                return "Network error occurred (Code: " + responseCode + ")";
        }
    }
}