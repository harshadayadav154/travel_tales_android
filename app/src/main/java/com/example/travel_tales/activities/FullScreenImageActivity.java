package com.example.travel_tales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.travel_tales.adapters.ImageAdapterGridView;
import com.example.travel_tales.databinding.ActivityFullScreenImageBinding;

import java.io.File;
import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {
    ActivityFullScreenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullScreenImageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initialize(); // Initializing the activity
    }

    /**
     * Initialize the activity.
     */
    private void initialize() {
        // Hiding the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Getting the intent that started this activity
        Intent intent = getIntent();

        // Retrieve the position of the image to display from the intent
        int position = intent.getExtras().getInt("id");

        // Creating an instance of ImageAdapterGridView to retrieve the list of image paths
        ImageAdapterGridView adapterGridView = new ImageAdapterGridView(this);

        // Retrieving the list of image paths
        List<String> imagePaths = adapterGridView.getImagePaths();

        // Getting the image path at the specified position
        String imagePath = imagePaths.get(position);

        // Loading the image into the ImageView using Glide
        Glide.with(this)
                .load(new File(imagePath)) // Loading image from file path
                .into(binding.fullScreenImgView); // Setting the loaded image into ImageView
    }
}

