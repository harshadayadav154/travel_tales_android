package com.example.travel_tales.activities;

import static com.example.travel_tales.utility.ImageUtility.generateImageName;
import static com.example.travel_tales.utility.ImageUtility.getBitmapFromUri;
import static com.example.travel_tales.utility.ImageUtility.saveImageToInternalStorage;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travel_tales.databinding.ActivityJournalBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.JournalEntry;
import com.example.travel_tales.models.Location;
import com.example.travel_tales.utility.DateUtility;
import com.example.travel_tales.utility.ImageUtility;
import com.example.travel_tales.utility.NotificationUtility;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Nabin Ghatani 2024-04-13
 */
public class JournalActivity extends AppCompatActivity implements View.OnClickListener, Geocoder.GeocodeListener {

    private ActivityJournalBinding binding; //View binding object

    private Geocoder geocoder;
    private Location location;

    private DBHelper dbHelper;
    private List<Uri> uriList;
    private List<String> imagePaths;

    // constant to compare
    // the activity result code
    private final int SELECT_PICTURE_REQUEST = 200;
    private static final String TAG = "JournalActivity";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJournalBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initializeComponents();
        registerEventListeners();
    }

    // Initialize components such as DBHelper, Geocoder
    private void initializeComponents() {
        binding.imgPreview.setVisibility(View.GONE);
        dbHelper = new DBHelper(getApplicationContext());
        geocoder = new Geocoder(this);
        location = new Location();
        uriList = new ArrayList<>();
        imagePaths = new ArrayList<>();
    }

    // Register event listeners for UI components
    private void registerEventListeners() {
        binding.editTextDate.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        try {
            if (viewId == binding.btnUploadImage.getId()) {
                imageChooser();
            } else if (viewId == binding.editTextDate.getId()) {
                showDatePicker();
            } else if (viewId == binding.btnSubmit.getId()) {
                if (isFormValid()) {
                    processFormSubmission();
                }
            }
        } catch (Exception e) {
            NotificationUtility.showNotification(this, "Oops, something went wrong.");
        }
    }

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), SELECT_PICTURE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE_REQUEST && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                processImages(clipData);
            } else {
                Uri uri = data.getData();
                processImage(uri);
            }
        }
    }

    private void processImages(ClipData clipData) {
        executorService.execute(() -> {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                try {
                    Bitmap bitmap = ImageUtility.getBitmapFromUri(JournalActivity.this, uri);
                    String imageName = ImageUtility.generateImageName();
                    String imagePath = ImageUtility.saveImageToInternalStorage(JournalActivity.this, bitmap, imageName);
                    if (imagePath != null) {
                        imagePaths.add(imagePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Update UI on the main thread after processing all images
            runOnUiThread(this::updateUI);
        });
    }

    private void processImage(Uri uri) {
        executorService.execute(() -> {
            try {
                Bitmap bitmap = ImageUtility.getBitmapFromUri(JournalActivity.this, uri);
                String imageName = generateImageName();
                String imagePath = ImageUtility.saveImageToInternalStorage(JournalActivity.this, bitmap, imageName);
                if (imagePath != null) {
                    imagePaths.add(imagePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Update UI on the main thread after processing the image
            runOnUiThread(this::updateUI);
        });
    }

    // Method to update UI after processing images
    private void updateUI() {
        runOnUiThread(() -> {
            if (!imagePaths.isEmpty()) {
                binding.imgPreview.setImageURI(Uri.parse(imagePaths.get(0)));
                String txt = imagePaths.size() > 1 ? " images." : " image.";
                binding.txtImageCount.setText("Selected " + imagePaths.size() + txt);
                binding.imgPreview.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    // Processing form submission
    private void processFormSubmission() throws ParseException {
        searchLocation(); // Searching for location coordinates

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setUserId(1); //TODO fix this later
        journalEntry.setTitle(Objects.requireNonNull(binding.editTextTitle.getText()).toString().trim());
        journalEntry.setDescription(Objects.requireNonNull(binding.editTextDescription.getText()).toString().trim());
        journalEntry.setDate(DateUtility.parseStringToDate(Objects.requireNonNull(binding.editTextDate.getText()).toString()));
        journalEntry.setImagePaths(imagePaths);
        if (location != null) {
            journalEntry.setLocation(location);
        }
        boolean insertStatus = dbHelper.insertJournalEntry(journalEntry);
        if (insertStatus) {
            NotificationUtility.showRecordSuccessNotification(this, NotificationUtility.RecordOperation.ADD);
        } else {
            NotificationUtility.showRecordFailedNotification(this, NotificationUtility.RecordOperation.ADD);
        }
    }

    /**
     * Checks if the form fields are valid.
     * If any field is invalid, it displays an error message and returns false.
     *
     * @return true if the form is valid, false otherwise.
     */
    private boolean isFormValid() {
        if (binding.editTextTitle.getText() != null && binding.editTextTitle.getText().toString().isEmpty()) {
            binding.editTextTitle.setError("Title is required.");
            return false;
        } else if (binding.editTextDate.getText() != null && binding.editTextDate.getText().toString().isEmpty()) {
            binding.editTextDate.setError("Date is required.");
            showDatePicker();
            return false;
        }
        return true;
    }

    /**
     * This method displays the date picker dialog when the date edit text is clicked.
     */
    private void showDatePicker() {
        // Creating instance of Calendar
        Calendar calendar = Calendar.getInstance();
        int dayOfVisit = calendar.get(Calendar.DAY_OF_MONTH);
        int monthOfVisit = calendar.get(Calendar.MONTH);
        int yearOfVisit = calendar.get(Calendar.YEAR);

        // Creating and showing date picker dialog
        // Setting selected date in the edit text
        // Date picker dialog
        DatePickerDialog datePicker = new DatePickerDialog(JournalActivity.this, (view, year, month, dayOfMonth) -> {
            // Setting selected date in the edit text
            binding.editTextDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
        }, yearOfVisit, monthOfVisit, dayOfVisit);

        datePicker.show();
    }


    /**
     * Search for location coordinates based on user input
     */
    public void searchLocation() {
        try {
            String locationName = Objects.requireNonNull(binding.editTextAddressSearch.getText()).toString().trim();
            // Checking if the query is not empty
            if (!locationName.isEmpty()) {
                geocoder.getFromLocationName(locationName, 1, this);
            }
        } catch (Exception e) {
            NotificationUtility.showNotification(this, "Oops, something went wrong!!!");
        }
    }

    /**
     * Callback method invoked when geocoding is successful and addresses are obtained.
     *
     * @param addresses The list of addresses obtained from geocoding.
     */
    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        // Checking if the list of addresses is not empty
        if (!addresses.isEmpty()) {
            // Getting the location from the list at the first position
            Address address = addresses.get(0);

            // Creating a LatLng object with the location's latitude and longitude
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            location.setName(Objects.requireNonNull(binding.editTextAddressSearch.getText()).toString().trim());
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
        } else {
            // Using runOnUiThread to show toast message as UI operations should be done on the UI thread i.e main thread
            runOnUiThread(() -> {
                // Showing a toast message indicating that the location was not found
                NotificationUtility.showNotification(this, "Location not found.");
            });
        }
    }

    @Override
    public void onError(@Nullable String errorMessage) {
        Geocoder.GeocodeListener.super.onError(errorMessage);
        runOnUiThread(() -> {
            // Showing a toast message indicating that the location was not found
            NotificationUtility.showNotification(this, "Location not found.");
        });
    }
}