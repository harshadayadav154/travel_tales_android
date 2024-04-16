package com.example.travel_tales.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.travel_tales.adapters.ImageAdapterGridView;
import com.example.travel_tales.databinding.ActivityJournalGalleryBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.utility.NotificationUtility;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nabin Ghatani 2024-04-14
 */
public class JournalGalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ActivityJournalGalleryBinding binding;
    private DBHelper dbHelper;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private ImageAdapterGridView imageAdapterGridView;

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
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
            List<String> imagePaths = dbHelper.getImagePathsByUserId(1); // Fix this later for user id
            if (imagePaths.isEmpty()) {
                switchToEmptyView();
            } else {
                // If images exist, populating the GridView with images
                imageAdapterGridView = new ImageAdapterGridView(this, 380, 380);
                imageAdapterGridView.setImagePaths(imagePaths);
                binding.journalImageGrid.setAdapter(imageAdapterGridView);
                switchToGalleryView();
            }
        } catch (Exception e) {
            Log.e("JournalGalleryActivity", "Error loading images", e);
            NotificationUtility.showNotification(getApplicationContext(), "Unable to show images");
        }
    }

    private void switchToGalleryView() {
        binding.journalImageGrid.setVisibility(View.VISIBLE);
        binding.txtNoImages.setVisibility(View.GONE);
    }

    private void switchToEmptyView() {
        binding.journalImageGrid.setVisibility(View.GONE);
        binding.txtNoImages.setVisibility(View.VISIBLE);
    }

    /**
     * Handle the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImagesFromStorage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Register event listeners for UI components.
     */
    private void registerEventListeners() {
        this.binding.journalImageGrid.setOnItemClickListener(this);
        this.binding.btnGoToHome.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("imagePaths", (Serializable) imageAdapterGridView.getImagePaths());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.binding.btnGoToHome.getId()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}

