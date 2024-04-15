package com.example.travel_tales.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.travel_tales.models.JournalEntry;
import com.example.travel_tales.models.Location;
import com.example.travel_tales.utility.DateUtility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Helper class for managing database creation and version management.
 *
 * @author Nabin Ghatani 2024-04-13
 */
public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "TravelTales.db";

    // Table Names
    private static final String TABLE_JOURNAL_ENTRIES = "JournalEntries";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_UPDATED_AT = "updated_at";

    // USER Table - column names
    private static final String KEY_USER_ID = "user_id";


    // JOURNAL_ENTRIES Table - column names
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_LOCATION_NAME = "location_name";
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_IMAGE_PATHS = "image_paths";

    // Table Create Statements
    // Journal Entries table create statement
    private static final String CREATE_TABLE_JOURNAL_ENTRIES = "CREATE TABLE " + TABLE_JOURNAL_ENTRIES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_CREATED_AT + " DATE,"
            + COL_UPDATED_AT + " DATE,"
            + KEY_USER_ID + " INTEGER,"
            + COL_TITLE + " TEXT NOT NULL,"
            + COL_DESCRIPTION + " TEXT,"
            + COL_DATE + " TEXT,"
            + COL_LOCATION_NAME + " TEXT,"
            + COL_LATITUDE + " REAL,"
            + COL_LONGITUDE + " REAL,"
            + COL_IMAGE_PATHS + " TEXT" + ")";

    /**
     * Constructor
     *
     * @param context application context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_JOURNAL_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNAL_ENTRIES);
        // create new tables
        onCreate(db);
    }

    // ------------------------ "Journal entries" table methods ----------------//

    /**
     * Inserts a new journal into the database.
     *
     * @param journalEntry - Journal of the user
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean insertJournalEntry(JournalEntry journalEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CREATED_AT, DateUtility.getCurrentDateTime());
        values.put(COL_UPDATED_AT, DateUtility.getCurrentDateTime());


        // Setting other values from the JournalEntry object
        values.put(KEY_USER_ID, journalEntry.getUserId());
        values.put(COL_TITLE, journalEntry.getTitle());
        values.put(COL_DESCRIPTION, journalEntry.getDescription());
        values.put(COL_DATE, DateUtility.formatDateToString(journalEntry.getDate()));

        // Setting location information if available
        Location location = journalEntry.getLocation();
        if (location != null) {
            values.put(COL_LOCATION_NAME, location.getName());
            values.put(COL_LATITUDE, location.getLatitude());
            values.put(COL_LONGITUDE, location.getLongitude());
        }

        // Convert list of image paths to comma-separated string and set in the ContentValues
        values.put(COL_IMAGE_PATHS, TextUtils.join(",", journalEntry.getImagePaths()));

        // Insert the values into the database
        long result = db.insert(TABLE_JOURNAL_ENTRIES, null, values);

        // Check if insertion was successful
        return result != -1;
    }

    /**
     * Getting a journal entry by its ID.
     * Handles gracefully if the ID is not found.
     *
     * @param entryId The ID of the journal entry to retrieve.
     * @return The journal entry corresponding to the ID, or null if not found.
     */
    @SuppressLint("Range")
    public JournalEntry getJournalEntryById(int entryId) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_JOURNAL_ENTRIES + " WHERE "
                + KEY_ID + " = " + entryId;

        Cursor c = db.rawQuery(selectQuery, null);

        JournalEntry journalEntry = null;

        if (c != null && c.moveToFirst()) {
            journalEntry = new JournalEntry();
            journalEntry.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            journalEntry.setCreatedAt(DateUtility.parseStringToDate(c.getString(c.getColumnIndex(COL_CREATED_AT))));
            journalEntry.setUpdatedAt(DateUtility.parseStringToDate(c.getString(c.getColumnIndex(COL_UPDATED_AT))));
            journalEntry.setUserId(c.getInt(c.getColumnIndex(KEY_USER_ID)));
            journalEntry.setTitle(c.getString(c.getColumnIndex(COL_TITLE)));
            journalEntry.setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
            journalEntry.setDate(DateUtility.parseStringToDate(c.getString(c.getColumnIndex(COL_DATE))));

            Location location = new Location();
            location.setName(c.getString(c.getColumnIndex(COL_LOCATION_NAME)));
            location.setLongitude(c.getDouble(c.getColumnIndex(COL_LONGITUDE)));
            location.setLatitude(c.getDouble(c.getColumnIndex(COL_LATITUDE)));

            journalEntry.setLocation(location);

            // Retrieving the image paths from the cursor
            String imagePathString = c.getString(c.getColumnIndex(COL_IMAGE_PATHS));
            // Parsing the image paths string into a list of strings
            List<String> imagePaths = Arrays.asList(imagePathString.split("\\s*,\\s*"));
            // Setting the image paths in the JournalEntry object
            journalEntry.setImagePaths(imagePaths);
        }

        if (c != null) {
            c.close();
        }

        return journalEntry;
    }

    /**
     * Updates a journal entry in the database.
     *
     * @param journalEntry - Journal entry to update
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateJournalEntry(JournalEntry journalEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, journalEntry.getTitle());
        values.put(COL_DESCRIPTION, journalEntry.getDescription());
        values.put(COL_DATE, DateUtility.formatDateToString(journalEntry.getDate()));

        // Setting location information
        Location location = journalEntry.getLocation();
        if (location != null) {
            values.put(COL_LOCATION_NAME, location.getName());
            values.put(COL_LATITUDE, location.getLatitude());
            values.put(COL_LONGITUDE, location.getLongitude());
        }

        values.put(COL_UPDATED_AT, System.currentTimeMillis()); // Updating with current timestamp

        // Converting the list of image paths to a single string separated by commas
        String imagePathString = TextUtils.join(",", journalEntry.getImagePaths());
        values.put(COL_IMAGE_PATHS, imagePathString);

        int rowsAffected = db.update(TABLE_JOURNAL_ENTRIES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(journalEntry.getId())});

        return (rowsAffected > 0);
    }


    /**
     * Deleting a journal entry by its ID.
     *
     * @param entryId The ID of the journal entry to delete.
     */
    public void deleteJournalEntry(int entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_JOURNAL_ENTRIES, KEY_ID + " = ?", new String[]{String.valueOf(entryId)});
    }

    /**
     * Retrieves the image paths associated with a specific journal entry from the database.
     *
     * @param journalEntryId The ID of the journal entry.
     * @return A list of image paths associated with the specified journal entry.
     */
    public List<String> getImagePathsByJournalEntryId(int journalEntryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> imagePaths = new ArrayList<>();

        // Query to retrieve the image paths for the specified journal entry ID
        String selectQuery = "SELECT " + COL_IMAGE_PATHS + " FROM " + TABLE_JOURNAL_ENTRIES +
                " WHERE " + KEY_ID + " = ?";

        // Executing the query with the journal entry ID parameter
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(journalEntryId)});

        // Checking if the cursor is not null and move it to the first row
        if (cursor != null && cursor.moveToFirst()) {
            // Getting the image paths string from the cursor
            @SuppressLint("Range") String imagePathString = cursor.getString(cursor.getColumnIndex(COL_IMAGE_PATHS));
            // Checking if the image path string is not null
            if (imagePathString != null) {
                // Splitting the comma-separated string into individual image paths
                String[] pathsArray = imagePathString.split(",");
                // Adding each path to the list of image paths
                Collections.addAll(imagePaths, pathsArray);
            }
            // Closing the cursor
            cursor.close();
        }
        // Returning the list of image paths
        return imagePaths;
    }

    /**
     * Retrieves the image paths associated with the user from the database.
     *
     * @param userId The ID of the user.
     * @return A list of image paths associated with the user.
     */
    public List<String> getImagePathsByUserId(int userId) {
        List<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Query to retrieve the image paths for the specified user ID
            String selectQuery = "SELECT " + COL_IMAGE_PATHS + " FROM " + TABLE_JOURNAL_ENTRIES +
                    " WHERE " + KEY_USER_ID + " = ?";

            // Executing the query with the user ID parameter
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

            // Checking if the cursor has results
            if (cursor != null && cursor.moveToFirst()) {
                // Getting the image paths string from the cursor
                @SuppressLint("Range") String imagePathString = cursor.getString(cursor.getColumnIndex(COL_IMAGE_PATHS));
                if (imagePathString != null && !imagePathString.isEmpty()) {
                    // Splitting the comma-separated string into individual image paths
                    String[] pathsArray = imagePathString.split(",");
                    // Adding each path to the list of image paths
                    Collections.addAll(imagePaths, pathsArray);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Closing the cursor if not null
            if (cursor != null) {
                cursor.close();
            }
        }
        // Returning the list of image paths
        return imagePaths;
    }

    /**
     * Retrieves a list of distinct locations (name, latitude, longitude) associated with the specified user ID.
     *
     * @param userId The ID of the user for which to retrieve distinct locations.
     * @return A list of Location objects containing distinct location information.
     */
    @SuppressLint("Range")
    public List<Location> getDistinctLocationsByUserId(int userId) {
        List<Location> distinctLocations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query to select distinct location names, latitude, and longitude for the given user ID
        String query = "SELECT DISTINCT " + COL_LOCATION_NAME + ", " + COL_LATITUDE + ", " + COL_LONGITUDE +
                " FROM " + TABLE_JOURNAL_ENTRIES +
                " WHERE " + KEY_USER_ID + " = ?";

        // Executing the query with the user ID parameter
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            // Iterating through the result set and construct Location objects
            do {
                // Checking if all three values (name, latitude, and longitude) are not null
                String locationName = cursor.getString(cursor.getColumnIndex(COL_LOCATION_NAME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE));
                if (locationName != null && latitude != 0 && longitude != 0) {
                    Location location = new Location();
                    location.setName(locationName);
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    distinctLocations.add(location);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return distinctLocations;
    }
}