package com.kush.restuarantapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LocaleHelper {

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREF_LANGUAGE, Constants.LANGUAGE_ENGLISH);
    }

    public static boolean isEnglish(Context context) {
        return getLanguage(context).equals(Constants.LANGUAGE_ENGLISH);
    }

    public static void setLanguage(Context context, String language) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREF_LANGUAGE, language);
        editor.putBoolean(Constants.PREF_IS_ENGLISH, language.equals(Constants.LANGUAGE_ENGLISH));
        editor.apply();
    }

    public static String switchLanguage(Context context) {
        String currentLanguage = getLanguage(context);
        String newLanguage = currentLanguage.equals(Constants.LANGUAGE_ENGLISH)
                ? Constants.LANGUAGE_HINDI
                : Constants.LANGUAGE_ENGLISH;
        setLanguage(context, newLanguage);
        return newLanguage;
    }

    public static void toggleLanguage(Context context){
        String newLanguage = switchLanguage(context);
        applyLanguage(context, newLanguage);
        if (context instanceof Activity) {
            ((Activity) context).recreate();
        }
    }

    public static void applyLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static Context updateContext(Context context) {
        String language = getLanguage(context);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    public static String getLanguageDisplayName(Context context) {
        String language = getLanguage(context);
        return language.equals(Constants.LANGUAGE_ENGLISH) ? "English" : "हिंदी";
    }
}