package com.example.travel_tales.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Nabin Ghatani 2024-04-13
 */
public class DateUtility {

    /**
     * Parses a string into a Date object.
     *
     * @param dateString The string representation of the date in the format "yyyy-MM-dd".
     * @return The Date object representing the parsed date.
     * @throws ParseException If the input string cannot be parsed into a Date.
     */
    public static Date parseStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.parse(dateString);
    }

    /**
     * Formats a Date object into a string.
     *
     * @param date The Date object to be formatted.
     * @return A string representation of the date in the format "yyyy-MM-dd".
     */
    public static String formatDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * Helper method to get current date and time in string format
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}

