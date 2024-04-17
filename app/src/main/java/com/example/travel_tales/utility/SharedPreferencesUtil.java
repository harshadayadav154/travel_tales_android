package com.example.travel_tales.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for managing SharedPreferences related operations.
 *
 * @author Nabin Ghatani 2024-01-16
 */
public class SharedPreferencesUtil {

    private static final String PREF_NAME = "user_prefs";
    private static final String USER_ID = "USER_ID";
    private static final String EMAIL = "EMAIL";

    /**
     * Method to save data to SharedPreferences.
     *
     * @param context The context to access SharedPreferences.
     * @param key     The key under which the value will be saved.
     * @param value   The value to be saved.
     */
    public static void saveData(Context context, String key, String value) {
        // Get SharedPreferences instance and apply changes
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
    }

    /**
     * Method to retrieve data from SharedPreferences.
     *
     * @param context      The context to access SharedPreferences.
     * @param key          The key for which the value will be retrieved.
     * @param defaultValue The default value to be returned if the key is not found.
     * @return The value corresponding to the key, or the default value if key is not found.
     */
    public static String getData(Context context, String key, String defaultValue) {
        // Get SharedPreferences instance and retrieve data
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(key, defaultValue);
    }

    /**
     * Method to retrieve the user ID from SharedPreferences.
     *
     * @param context The context to access SharedPreferences.
     * @return The user ID stored in SharedPreferences, or a default value if not found.
     */
    public static int getUserId(Context context) {
        try {
            if (context == null) {
                return -1;
            }
            // Get SharedPreferences instance and retrieve user ID
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .getInt(USER_ID, -1);
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * Method to retrieve the email ID from SharedPreferences.
     *
     * @param context The context to access SharedPreferences.
     * @return The email ID stored in SharedPreferences, or an empty string if not found.
     */
    public static String getEmailId(Context context) {
        // Get SharedPreferences instance and retrieve email ID
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(EMAIL, "");
    }

    /**
     * Clears all data stored in SharedPreferences.
     * This method is typically used to clear user session data when logging out.
     *
     * @param context The context used to access SharedPreferences.
     */
    public static void clearSession(Context context) {
        // Get the SharedPreferences instance using the predefined PREF_NAME constant
        SharedPreferences shp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Obtain an editor to make changes to the SharedPreferences
        SharedPreferences.Editor editor = shp.edit();

        // Remove all values from the SharedPreferences
        editor.clear();

        // Commit the changes asynchronously
        editor.apply();
    }
}

