package com.kush.restuarantapp.utils;

public class Constants {
    public static final String BASE_URL = "https://uat.onebanc.ai";
    public static final String GET_ITEMS_ENDPOINT = "/emulator/interview/get_item_list";
    public static final String GET_ITEM_BY_ID_ENDPOINT = "/emulator/interview/get_item_by_id";
    public static final String GET_ITEM_BY_FILTER_ENDPOINT = "/emulator/interview/get_item_by_filter";
    public static final String MAKE_PAYMENT_ENDPOINT = "/emulator/interview/make_payment";

    public static final String API_KEY_HEADER = "X-Partner-API-Key";
    public static final String API_KEY_VALUE = "uonebancservceemultrS3cg8RaL30";
    public static final String PROXY_ACTION_HEADER = "X-Forward-Proxy-Action";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/json";

    public static final String ACTION_GET_ITEM_LIST = "get_item_list";
    public static final String ACTION_GET_ITEM_BY_ID = "get_item_by_id";
    public static final String ACTION_GET_ITEM_BY_FILTER = "get_item_by_filter";
    public static final String ACTION_MAKE_PAYMENT = "make_payment";

    public static final String TAG = "RestaurantApp";
    public static final int PAGINATION_COUNT = 100;
    public static final int TOP_DISHES_COUNT = 3;

    public static final String INTENT_CUISINE_NAME = "cuisine_name";
    public static final String INTENT_CUISINE_ID = "cuisine_id";

    public static final double CGST_PERCENTAGE = 2.5;
    public static final double SGST_PERCENTAGE = 2.5;
    public static final double CGST_RATE = 0.025;
    public static final double SGST_RATE = 0.025;

    public static final int SUCCESS_CODE = 200;

    public static final String PREF_NAME = "restaurant_prefs";
    public static final String PREF_LANGUAGE = "language";
    public static final String PREF_IS_ENGLISH = "is_english";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_HINDI = "hi";

    public static final int IMAGE_PLACEHOLDER = android.R.drawable.ic_menu_gallery;
    public static final int IMAGE_ERROR = android.R.drawable.ic_menu_close_clear_cancel;

    public static final int TIMEOUT_CONNECT = 10000;
    public static final int TIMEOUT_READ = 15000;
}