package com.example.travel_tales.activities;

import static com.example.travel_tales.utility.ImageUtility.generateImageName;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travel_tales.adapters.ImageAdapterGridView;
import com.example.travel_tales.databinding.ActivityJournalBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.JournalEntry;
import com.example.travel_tales.models.Location;
import com.example.travel_tales.utility.DateUtility;
import com.example.travel_tales.utility.ImageUtility;
import com.example.travel_tales.utility.NotificationUtility;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
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
    private List<String> imagePaths;

    // constant to compare
    // the activity result code
    private final int SELECT_PICTURE_REQUEST = 200;
    private static final String TAG = "JournalActivity";
    private ExecutorService executorService;

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
        dbHelper = new DBHelper(getApplicationContext());
        geocoder = new Geocoder(this);
        location = new Location();
        imagePaths = new ArrayList<>();
        executorService = Executors.newSingleThreadExecutor();

        binding.imgPreview.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
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
        this.imagePaths = new ArrayList<>(); // resetting the value
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

    // Method to process multiple images received through ClipData
    private void processImages(ClipData clipData) {
        executorService.execute(() -> {
            // Update UI to indicate processing
            runOnUiThread(this::switchToProgressBarView);

            // Process each image in the ClipData
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                try {
                    // Get bitmap from URI and save to internal storage
                    Bitmap bitmap = ImageUtility.getBitmapFromUri(JournalActivity.this, uri);
                    String imagePath = ImageUtility.saveImageToInternalStorage(JournalActivity.this, bitmap, uri.getLastPathSegment());
                    if (imagePath != null && !imagePaths.contains(imagePath)) {
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


    /**
     * Switches the UI to a progress bar view by hiding the image preview and showing the progress bar.
     */
    private void switchToProgressBarView() {
        binding.imgPreview.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    // Method to process a single image received as a Uri
    private void processImage(Uri uri) {
        executorService.execute(() -> {
            // Updating UI to indicate processing
            runOnUiThread(this::switchToProgressBarView);

            try {
                // Getting bitmap from URI and save to internal storage
                Bitmap bitmap = ImageUtility.getBitmapFromUri(JournalActivity.this, uri);
                String imagePath = ImageUtility.saveImageToInternalStorage(JournalActivity.this, bitmap, uri.getLastPathSegment());
                if (imagePath != null && !imagePaths.contains(imagePath)) {
                    imagePaths.add(imagePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Updating UI on the main thread after processing the image
            runOnUiThread(this::updateUI);
        });
    }

    // Method to update UI after processing images
    private void updateUI() {
        runOnUiThread(() -> {
            if (!imagePaths.isEmpty()) {
                imagePaths = imagePaths.stream().distinct().collect(Collectors.toList());

                // Showing preview image and hiding progress bar
                binding.imgPreview.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);

                // Limiting the number of images to be displayed to at most 6
                int endIndex = Math.min(imagePaths.size(), 6);
                List<String> imagesToShow = imagePaths.subList(0, endIndex);

                // Creating and setting up the adapter to display images in a GridView
                ImageAdapterGridView imageAdapterGridView = new ImageAdapterGridView(this, 300, 225);
                imageAdapterGridView.setImagePaths(imagesToShow);
                binding.imgPreview.setAdapter(imageAdapterGridView);
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
        journalEntry.setUserId(1); //todo - fix this later
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