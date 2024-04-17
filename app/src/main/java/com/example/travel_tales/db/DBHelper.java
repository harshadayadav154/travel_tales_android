package com.example.travel_tales.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.travel_tales.models.JournalEntry;
import com.example.travel_tales.models.Location;
import com.example.travel_tales.models.User;
import com.example.travel_tales.models.Todo;
import com.example.travel_tales.utility.DateUtility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private static final String TABLE_USERS = "Users";

    private static final String TABLE_TODO = "Todo";

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

    // USER_ENTRIES Table - column names
    public static final String COLUMN_USER_FIRST_NAME = "first_name";
    public static final String COLUMN_USER_LAST_NAME = "last_name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    private static final String COL_ID = "id";
    private static final String COL_TODO_TITLE = "Title";
    private static final String COL_STATUS = "Status";

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

    private static final String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER,"
            + COL_TODO_TITLE + " TEXT,"
            + COL_STATUS + " REAL" + ")";


    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_USER_FIRST_NAME + " TEXT,"
            + COLUMN_USER_LAST_NAME + " TEXT" + ")";

    private static final String TAG = "DBHelper";

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

        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(CREATE_TABLE_JOURNAL_ENTRIES);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNAL_ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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
     * @param journalId The ID of the journal entry to retrieve.
     * @return The journal entry corresponding to the ID, or null if not found.
     */
    @SuppressLint("Range")
    public JournalEntry getJournalEntryById(int journalId) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_JOURNAL_ENTRIES + " WHERE "
                + KEY_ID + " = " + journalId;

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
     * Retrieves all journal entries associated with the given user ID from the database.
     *
     * @param userId The ID of the user whose journal entries are to be retrieved.
     * @return A list of journal entries belonging to the specified user. Returns an empty list if an exception occurs during database access.
     */
    @SuppressLint("Range")
    public List<JournalEntry> getAllJournalsByUserId(int userId) {
        // Getting readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Defining the select query
        String selectQuery = "SELECT  * FROM " + TABLE_JOURNAL_ENTRIES + " WHERE "
                + KEY_USER_ID + " = " + userId;

        // Executing the query
        Cursor c = db.rawQuery(selectQuery, null);

        JournalEntry journalEntry = null;
        List<JournalEntry> journalEntries = new ArrayList<>();

        try {
            // Checking if the cursor is not null and moving to the first entry
            if (c != null && c.moveToFirst()) {
                do {
                    // Initializing a new JournalEntry object
                    journalEntry = new JournalEntry();
                    // Setting attributes of the JournalEntry object from the cursor data
                    journalEntry.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    journalEntry.setCreatedAt(DateUtility.parseStringToDate(c.getString(c.getColumnIndex(COL_CREATED_AT))));
                    journalEntry.setUpdatedAt(DateUtility.parseStringToDate(c.getString(c.getColumnIndex(COL_UPDATED_AT))));
                    journalEntry.setUserId(c.getInt(c.getColumnIndex(KEY_USER_ID)));
                    journalEntry.setTitle(c.getString(c.getColumnIndex(COL_TITLE)));
                    journalEntry.setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
                    journalEntry.setDate(DateUtility.parseStringToDate(c.getString(c.getColumnIndex(COL_DATE))));

                    // Creating a new Location object and setting its attributes
                    Location location = new Location();
                    location.setName(c.getString(c.getColumnIndex(COL_LOCATION_NAME)));
                    location.setLongitude(c.getDouble(c.getColumnIndex(COL_LONGITUDE)));
                    location.setLatitude(c.getDouble(c.getColumnIndex(COL_LATITUDE)));

                    // Setting the Location object in the JournalEntry
                    journalEntry.setLocation(location);

                    // Retrieving the image paths from the cursor
                    String imagePathString = c.getString(c.getColumnIndex(COL_IMAGE_PATHS));
                    // Parsing the image paths string into a list of strings
                    List<String> imagePaths = Arrays.asList(imagePathString.split("\\s*,\\s*"));
                    // Setting the image paths in the JournalEntry object
                    journalEntry.setImagePaths(imagePaths);

                    // Adding the JournalEntry object to the list
                    journalEntries.add(journalEntry);
                } while (c.moveToNext()); // Moving to the next entry if available
            }
        } catch (Exception e) {
            // Log any exceptions that occur
            Log.e(TAG, "Error retrieving journal entries: " + e.getMessage());
            // Return an empty list in case of exception
            return new ArrayList<>();
        } finally {
            // Closing the cursor
            if (c != null) {
                c.close();
            }
        }

        // Returning the list of JournalEntry objects
        return journalEntries;
    }

    public void updateJournalEntry(JournalEntry journalEntry) {
        // Open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, journalEntry.getTitle());
        values.put(COL_DESCRIPTION, journalEntry.getDescription());
        values.put(COL_DATE, DateUtility.formatDateToString(journalEntry.getDate()));

        // Set location information if available
        Location location = journalEntry.getLocation();
        if (location != null) {
            values.put(COL_LOCATION_NAME, location.getName());
            values.put(COL_LATITUDE, location.getLatitude());
            values.put(COL_LONGITUDE, location.getLongitude());
        } else {
            // If location is null, clear the location information
            values.putNull(COL_LOCATION_NAME);
            values.putNull(COL_LATITUDE);
            values.putNull(COL_LONGITUDE);
        }

        // Filter out empty image paths and join them with commas
        List<String> validImagePaths = journalEntry.getImagePaths().stream()
                .filter(imagePath -> !TextUtils.isEmpty(imagePath))
                .collect(Collectors.toList());
        values.put(COL_IMAGE_PATHS, String.join(",", validImagePaths));

        // Update the journal entry in the database
        try {
            db.update(TABLE_JOURNAL_ENTRIES, values, KEY_ID + " = ? AND " + KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(journalEntry.getId()), String.valueOf(journalEntry.getUserId())});
        } catch (Exception e) {
            // Print the stack trace for debugging
            e.printStackTrace();
        } finally {
            // Close the database
            db.close();
        }
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


    //--------------------USER-------------------------

    /**
     * Adds a new user to the database.
     *
     * @param user The user to add.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_USER_LAST_NAME, user.getLastName());
        // Inserting row
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Checks if a user exists with the given email and password.
     *
     * @param email    The email of the user.
     * @param password The password of the user.
     * @return true if a matching user is found, false otherwise.
     */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                KEY_ID // You can select only the user id since you just need to check existence
        };
        String selection = COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    /**
     * Retrieves user details by email.
     *
     * @param email The email of the user.
     * @return A Cursor containing the user details.
     */
    @SuppressLint("Range")
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME)));
            cursor.close();
        }
        db.close();
        return user;
    }


    /**
     * Updates user details in the database using the user's ID.
     *
     * @param user User object containing all the details to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUserDetails(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        contentValues.put(COLUMN_USER_LAST_NAME, user.getLastName());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Update the user details where the user ID matches
        int rowsAffected = db.update(TABLE_USERS, contentValues, KEY_ID + " = ?", new String[]{String.valueOf(user.getId())});

        return rowsAffected > 0; // Return true if at least one row was updated
    }

    /**
     * Updates the password of a user.
     *
     * @param email       The email of the user.
     * @param newPassword The new password to set.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, newPassword);

        // Updating row
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + " = ?",
                new String[]{email});
        db.close();

        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1; // Default value if user is not found

        // Define columns to retrieve
        String[] columns = {KEY_ID};

        // Define selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        // Query the database
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        // Check if user exists and retrieve user ID
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            cursor.close(); // Close the cursor to free up resources
        }

        db.close(); // Close the database connection

        return userId;
    }

    // ------------------------ "TODO" table methods ----------------//

    /**
     * Inserts a new todo into the database.
     *
     * @param toDO - todo of the user
     */

    public void insertTodo(Todo toDO) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TODO_TITLE, toDO.getTodoTitle());
        cv.put(KEY_USER_ID, 1);
        cv.put(COL_STATUS, 0);
        db.insert(TABLE_TODO, null, cv);
    }

    /**
     * Updates a Todo in the database.
     *
     * @param id-   unique id
     * @param todo- todo name to update
     */

    public void updateTodo(int id, String todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TODO_TITLE, todo);
        db.update(TABLE_TODO, cv, "ID=?", new String[]{String.valueOf(id)});
    }

    /**
     * Updates a Todo in the database.
     *
     * @param id-     unique id
     * @param status- todo status to update
     */

    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, status);
        db.update(TABLE_TODO, cv, "ID=?", new String[]{String.valueOf(id)});
    }

    /**
     * delete a Todo record from database.
     *
     * @param id- unique id
     */
    public void deleteTodo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, "ID=?", new String[]{String.valueOf(id)});
    }

    /**
     * Retrieves a list of todos
     *
     * @param userId The ID of the user for which to retrieve todo.
     * @return A list of all todos.
     */
    @SuppressLint("Range")
    public List<Todo> getAllTodos(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<Todo> todoList = new ArrayList<>();

        db.beginTransaction();
        try {
            // Define your query
            String query = "SELECT * FROM " + TABLE_TODO + " WHERE " +
                    KEY_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Todo todo = new Todo();
                    todo.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    todo.setUser_id(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
                    todo.setTodoTitle(cursor.getString(cursor.getColumnIndex(COL_TODO_TITLE)));
                    todo.setStatus(cursor.getInt(cursor.getColumnIndex(COL_STATUS)));
                    todoList.add(todo);
                } while ((cursor.moveToNext()));
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }
        return todoList;
    }
}