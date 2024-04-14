package com.example.travel_tales.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Nabin Ghatani 2024-04-13
 */
public class NotificationUtility {

    /**
     * Enum to represent different types of record operations
     */
    public enum RecordOperation {
        ADD, UPDATE, DELETE
    }

    /**
     * Show notification with given message
     *
     * @param context Context of the application
     * @param message Notification message
     */
    public static void showNotification(Context context, String message) {
        // Showing notification as toast
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show a success notification message for record operations
     *
     * @param context   Context of the application
     * @param operation The record operation (e.g., ADD, UPDATE, DELETE)
     */
    public static void showRecordSuccessNotification(Context context, RecordOperation operation) {
        String message = "SUCCESS - Record " + operation.name().toLowerCase() + "ed" + " successfully.";
        // Showing notification as toast
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show a failed notification message for record operations
     *
     * @param context   Context of the application
     * @param operation The record operation (e.g., ADD, UPDATE, DELETE)
     */
    public static void showRecordFailedNotification(Context context, RecordOperation operation) {
        String message = "FAILED -  Unable to " + operation.name().toLowerCase() + " record.";
        // Showing notification as toast
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}

