package com.example.travel_tales.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.travel_tales.adapters.ImageAdapterGridView;
import com.example.travel_tales.databinding.ActivityJournalGalleryBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.utility.NotificationUtility;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nabin Ghatani 2024-04-14
 */
public class JournalGalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityJournalGalleryBinding binding;
    private DBHelper dbHelper;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflating the layout using view binding
        binding = ActivityJournalGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing components
        initializeComponents();

        // Registering event listeners
        registerEventListeners();
    }

    /**
     * Initializes UI components and checks permissions to load images from storage.
     */
    private void initializeComponents() {
        // Check if the app has permission to read external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            // Permission is granted, load images from storage
            loadImagesFromStorage();
        }
    }

    /**
     * Load images from storage and display them in the GridView.
     */
    private void loadImagesFromStorage() {
        try {
            dbHelper = new DBHelper(getApplicationContext());
            // Fetch image URIs from the database
            List<String> uriList = dbHelper.getImagePathsByUserId(1); // Fix this later for user id
            if (uriList.isEmpty()) {
                // If no images, hide the GridView and show a message
                binding.journalImageGrid.setVisibility(View.GONE);
                binding.txtNoImages.setVisibility(View.VISIBLE);
            } else {
                // If images exist, populate the GridView with images
                ImageAdapterGridView imageAdapterGridView = new ImageAdapterGridView(this, uriList.stream().map(Uri::parse).collect(Collectors.toList()));
                binding.journalImageGrid.setAdapter(imageAdapterGridView);
                binding.txtNoImages.setVisibility(View.GONE);
                // Notify the adapter that the data has changed
                imageAdapterGridView.notifyDataSetChanged();
            }
        } catch (Exception e) {
            NotificationUtility.showNotification(getApplicationContext(), "Unable to show images");
        }
    }

    /**
     * Handle the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load images from storage
                loadImagesFromStorage();
            } else {
                // Permission denied, handle it gracefully
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Register event listeners for UI components.
     */
    private void registerEventListeners() {
        // Register event listeners for UI components if needed
    }

    @Override
    public void onClick(View v) {
        // Handle clicks on UI components if needed
    }
}

