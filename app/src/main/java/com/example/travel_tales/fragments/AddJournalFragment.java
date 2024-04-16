package com.example.travel_tales.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travel_tales.adapters.ImageAdapterGridView;
import com.example.travel_tales.databinding.FragmentAddJournalBinding;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link AddJournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddJournalFragment extends Fragment implements View.OnClickListener, Geocoder.GeocodeListener {

    private FragmentAddJournalBinding binding; //View binding object

    private Geocoder geocoder;
    private Location location;

    private DBHelper dbHelper;
    private List<String> imagePaths;

    private ExecutorService executorService;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public AddJournalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment addJournalFragment.
     */
    public static AddJournalFragment newInstance(String param1, String param2) {
        AddJournalFragment fragment = new AddJournalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddJournalBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        initializeComponents();
        registerEventListeners();

        return view;
    }

    // Initializing components such as DBHelper, Geocoder
    private void initializeComponents() {
        dbHelper = new DBHelper(getContext());
        geocoder = new Geocoder(AddJournalFragment.this.requireContext());
        imagePaths = new ArrayList<>();
        executorService = Executors.newSingleThreadExecutor();

        binding.imgPreview.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
    }

    // This method register event listeners for UI components
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
                chooseMultipleImages();
            } else if (viewId == binding.editTextDate.getId()) {
                showDatePicker();
            } else if (viewId == binding.btnSubmit.getId()) {
                if (isFormValid()) {
                    processFormSubmission();
                }
            }
        } catch (Exception e) {
            NotificationUtility.showNotification(getContext(), "Oops, something went wrong.");
        }
    }

    // ActivityResultLauncher for image selection
    private final ActivityResultLauncher<Intent> mGetMultipleImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            processImages(clipData);
                        } else {
                            Uri uri = data.getData();
                            processImage(uri);
                        }
                    }
                }
            });


    // This method is triggered when
    // the Upload Image Button is clicked
    private void chooseMultipleImages() {
        this.imagePaths = new ArrayList<>(); // resetting the value
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_PICK);
        mGetMultipleImages.launch(Intent.createChooser(intent, "Select Pictures"));
    }


    // Method to process multiple images received through ClipData
    private void processImages(ClipData clipData) {
        executorService.execute(() -> {
            // Updating UI to indicate processing
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::switchToProgressBarView);
            }

            // Processing each image in the ClipData
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                try {
                    // Get bitmap from URI and save to internal storage
                    Bitmap bitmap = ImageUtility.getBitmapFromUri(getActivity().getApplicationContext(), uri);
                    String imagePath = ImageUtility.saveImageToInternalStorage(getActivity().getApplicationContext(), bitmap, uri.getLastPathSegment());
                    if (imagePath != null && !imagePaths.contains(imagePath)) {
                        imagePaths.add(imagePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Update UI on the main thread after processing all images
            if (getActivity() != null) getActivity().runOnUiThread(this::updateUI);
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
            if (getActivity() != null) getActivity().runOnUiThread(this::switchToProgressBarView);

            try {
                // Getting bitmap from URI and save to internal storage
                Bitmap bitmap = ImageUtility.getBitmapFromUri(getActivity().getApplicationContext(), uri);
                String imagePath = ImageUtility.saveImageToInternalStorage(getActivity().getApplicationContext(), bitmap, uri.getLastPathSegment());
                if (imagePath != null && !imagePaths.contains(imagePath)) {
                    imagePaths.add(imagePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Updating UI on the main thread after processing the image
            if (getActivity() != null) getActivity().runOnUiThread(this::updateUI);
        });
    }

    // Method to update UI after processing images
    private void updateUI() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (!imagePaths.isEmpty()) {
                    imagePaths = imagePaths.stream().distinct().collect(Collectors.toList());

                    // Showing preview image and hiding progress bar
                    binding.imgPreview.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);

                    // Limiting the number of images to be displayed to at most 6
                    int endIndex = Math.min(imagePaths.size(), 6);
                    List<String> imagesToShow = imagePaths.subList(0, endIndex);

                    // Creating and setting up the adapter to display images in a GridView
                    ImageAdapterGridView imageAdapterGridView = new ImageAdapterGridView(getContext(), 300, 225);
                    imageAdapterGridView.setImagePaths(imagesToShow);
                    binding.imgPreview.setAdapter(imageAdapterGridView);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
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
            NotificationUtility.showRecordSuccessNotification(getContext(), NotificationUtility.RecordOperation.ADD);
        } else {
            NotificationUtility.showRecordFailedNotification(getContext(), NotificationUtility.RecordOperation.ADD);
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
        if (getContext() != null) {
            @SuppressLint("SetTextI18n") DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                // Setting selected date in the edit text
                binding.editTextDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }, yearOfVisit, monthOfVisit, dayOfVisit);

            datePicker.show();
        }
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
            NotificationUtility.showNotification(getContext(), "Oops, something went wrong!!!");
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
            location = new Location();
            location.setName(Objects.requireNonNull(binding.editTextAddressSearch.getText()).toString().trim());
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
        } else {
            // Using runOnUiThread to show toast message as UI operations should be done on the UI thread i.e main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Showing a toast message indicating that the location was not found
                    NotificationUtility.showNotification(getContext(), "Location not found.");
                });
            }
        }
    }

    @Override
    public void onError(@Nullable String errorMessage) {
        Geocoder.GeocodeListener.super.onError(errorMessage);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Showing a toast message indicating that the location was not found
                NotificationUtility.showNotification(getContext(), "Location not found.");
            });
        }
    }
}