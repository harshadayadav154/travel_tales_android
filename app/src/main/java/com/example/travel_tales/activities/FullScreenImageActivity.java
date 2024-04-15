package com.example.travel_tales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.travel_tales.adapters.ImageAdapterGridView;
import com.example.travel_tales.databinding.ActivityFullScreenImageBinding;
import com.example.travel_tales.utility.NotificationUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nabin Ghatani 2024-04-15
 */
public class FullScreenImageActivity extends AppCompatActivity {
    ActivityFullScreenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content view using view binding
        binding = ActivityFullScreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize the activity
        initialize();
    }

    /**
     * Initialize the activity.
     */
    private void initialize() {
        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Getting the intent that started this activity
        Intent intent = getIntent();

        // Retrieve the position of the image to display from the intent
        int position = intent.getIntExtra("position", 0);

        // Retrieving the list of image paths from the intent
        ArrayList<String> imagePaths = intent.getStringArrayListExtra("imagePaths");

        if (imagePaths != null && position >= 0 && position < imagePaths.size()) {
            // Getting the image path at the specified position
            String imagePath = imagePaths.get(position);

            // Loading the image into the ImageView using Glide
            Glide.with(this)
                    .load(new File(imagePath)) // Load image from file path
                    .into(binding.fullScreenImgView); // Set the loaded image into ImageView
        } else {
            // Handle the case where image paths or position are not valid
            NotificationUtility.showNotification(this, "Invalid image data");
            finish(); // Finish the activity if data is invalid
        }
    }
}

