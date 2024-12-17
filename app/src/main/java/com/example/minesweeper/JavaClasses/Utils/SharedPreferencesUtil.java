package com.example.minesweeper.JavaClasses.Utils;

/*
Utility class destined to centralize and make the code easier to read and encapsulate
how the different types of SharedPreferences are used cross the project

The constructor is private to avoid any instantiation of the class. Everything will be handled
from here and public static methods

Format:

UserKey + Key + Value, where UserKey = username + "_"

Andy_USERNAME: Andy
Andy_PASSWORD: Andy10
Andy_EMAIL_ADDRESS: andylopezrey@hotmail.com

 */

import android.content.Context;
import android.content.SharedPreferences;

import com.example.minesweeper.JavaClasses.SharedPreferences_Keys;

import java.util.Map;

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

    public static void saveStat(Context context, String username, SharedPreferences_Keys key, int value) {
        SharedPreferences sp = getSpecificSharedPreferences(context, STATS_SP);
        String userKey = generateUserKey(username) + key.toString();
        // If a stat is considered as "basic", we need to increment it based on the previous record
        boolean basicStats = isBasicStat(key);
        int currentValue = 0;
        if (basicStats) currentValue = sp.getInt(userKey, 0); // 0 Default
        System.out.println("Is the key: " + key + " a basic stat? " + basicStats);
        sp.edit().putInt(userKey, currentValue + value).apply();
    }

    private static boolean isBasicStat(SharedPreferences_Keys key) {
        return  key == SharedPreferences_Keys.GAMES_PLAYED ||
                key == SharedPreferences_Keys.WINS ||
                key == SharedPreferences_Keys.LOSES ||
                key == SharedPreferences_Keys.SCORE ||
                key == SharedPreferences_Keys.TIME;
    }

    public static void saveStat(Context context, String username, SharedPreferences_Keys key, String value) {
        SharedPreferences sp = getSpecificSharedPreferences(context, STATS_SP);
        String userKey = generateUserKey(username) + key.toString();
        String currentValue = sp.getString(userKey, ""); // Empty Default
        sp.edit().putString(userKey, currentValue + value).apply();
    }

    public static void saveStat(Context context, String username, SharedPreferences_Keys key, float value) {
        SharedPreferences sp = getSpecificSharedPreferences(context, STATS_SP);
        String userKey = generateUserKey(username) + key.toString();
        String currentValue = sp.getString(userKey, ""); // Empty Default
        sp.edit().putString(userKey, currentValue + value).apply();
    }


    // Always update the amount of games played first before calling this method
    public static void saveAverageTimeStat(Context context, String username, SharedPreferences_Keys key, int gamesPlayed, int time) {
        SharedPreferences sp = getSpecificSharedPreferences(context, STATS_SP);
        String userKey = generateUserKey(username) + key.toString();
        float currentAverageTime = sp.getFloat(userKey, 0.0f) ;
        float newAverageTime = (currentAverageTime * (gamesPlayed - 1) + time) / gamesPlayed;
        sp.edit().putFloat(userKey, newAverageTime).apply();
    }

    public static int getStat(Context context, String username, SharedPreferences_Keys key, int defaultValue) {
        SharedPreferences sp = getSpecificSharedPreferences(context, STATS_SP);
        String userKey = generateUserKey(username) + key.toString();
        return sp.getInt(userKey, defaultValue);
    }

    public static String getStat(Context context, String username, SharedPreferences_Keys key, String defaultValue) {
        SharedPreferences sp = getSpecificSharedPreferences(context, STATS_SP);
        String userKey = generateUserKey(username) + key.toString();
        return sp.getString(userKey, defaultValue);
    }

    public static void resetStats(Context context, String username) {
        SharedPreferences sp = getSpecificSharedPreferences(context, STATS_SP);
        Map<String, ?> allEntries = sp.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(generateUserKey(username))) {
                sp.edit().remove(key).apply();
            }
        }
    }

    // Temporary method
    public static Map<String, ?> getAllUsers(Context context) {
        SharedPreferences sp = getSpecificSharedPreferences(context, LOGIN_SP);
        //sp.edit().clear().apply();
        Map<String, ?> allEntries = sp.getAll();
        return allEntries;
    }
}
