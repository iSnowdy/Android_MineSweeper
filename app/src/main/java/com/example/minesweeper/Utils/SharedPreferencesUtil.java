package com.example.minesweeper.Utils;

/*
Utility class destined to centralize and make the code easier to read and encapsulate
how the different types of SharedPreferences are used cross the project

The constructor is private to avoid any instantiation of the class. Everything will be handled
from here and public static methods

 */

import android.content.Context;
import android.content.SharedPreferences;

import com.example.minesweeper.SharedPreferences_Keys;

public class SharedPreferencesUtil {
    // Different types of SP used across the project
    private static final String LOGIN_SP = SharedPreferences_Keys.USER_INFORMATION_SP.toString();
    private static final String SETTINGS_SP = SharedPreferences_Keys.SETTINGS_INFORMATION_SP    .toString();
    private static final String STATS_SP = SharedPreferences_Keys.STATS_INFORMATION_SP.toString();

    private SharedPreferencesUtil() {}

    // Retrieve a specific SP
    private static SharedPreferences getSpecificSharedPreferences(Context context, String spName) {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }
    // Method to be able to create one SP per user
    private static String generateUserKey(String username) {
        return username + "_";
    }

    // ------ Register & Login SP ------

    public static void saveUserInformation(Context context, String username, String key, String value) {
        SharedPreferences sp = getSpecificSharedPreferences(context, LOGIN_SP);
        String userKey = generateUserKey(username) + key;
        sp.edit().putString(userKey, value).apply();
    }

    public static String getUserInformation(Context context, String username, String key, String defaultValue) {
        SharedPreferences sp = getSpecificSharedPreferences(context, LOGIN_SP);
        String userKey = generateUserKey(username) + key;
        return sp.getString(userKey, defaultValue);
    }

    public static void deleteUserInformation(Context context, String username, String key) {
        SharedPreferences sp = getSpecificSharedPreferences(context, LOGIN_SP);
        String userKey = generateUserKey(username) + key;
        sp.edit().remove(userKey).apply();
    }

    //  ------ Settings SP ------

    public static void saveSetting(Context context, SharedPreferences_Keys key, String value) {
        SharedPreferences sp = getSpecificSharedPreferences(context, SETTINGS_SP);
        sp.edit().putString(key.toString(), value).apply();
    }

    public static String getSetting(Context context, SharedPreferences_Keys key, String defaultValue) {
        SharedPreferences sp = getSpecificSharedPreferences(context, SETTINGS_SP);
        return sp.getString(key.toString(), defaultValue);
    }


    //  ------ Stats SP ------


    // Game SP


}
