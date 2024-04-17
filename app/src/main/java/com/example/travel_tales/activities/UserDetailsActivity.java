package com.example.travel_tales.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travel_tales.databinding.ActivityUserDetailsBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.User;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityUserDetailsBinding binding;
    private DBHelper dbHelper;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        registerEventListeners();

        // Retrieve user email from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = preferences.getString("EMAIL", "");

        // Check if user email is available
        if (!userEmail.isEmpty()) {
            // Fetch user details
            fetchUserDetails(userEmail);
        } else {
            Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        dbHelper = new DBHelper(this);
    }

    // Method to register event listeners for buttons
    private void registerEventListeners() {
        binding.btnSaveDetails.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);
    }

    // Method to fetch and display user details
    private void fetchUserDetails(String email) {
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());
            binding.etFirstName.setText(user.getFirstName());
            binding.etLastName.setText(user.getLastName());
        } else {
            Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update user details
    private void updateUserDetails() {
        // Get entered details
        String email = binding.tvEmail.getText().toString().trim();
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();

        // Update user details in the database
        boolean isUpdated = dbHelper.insertOrUpdateUserDetails(email, firstName, lastName, newPassword);

        // Show appropriate message
        if (isUpdated) {
            Toast.makeText(UserDetailsActivity.this, "Your details updated", Toast.LENGTH_SHORT).show();
            // Check and display if all details have been saved
            checkAndDisplayAllDetailsSaved(email);
        } else {
            Toast.makeText(UserDetailsActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to open the gallery and select an image
    private void openGallery() {
        ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        imageUri = result;
                        binding.imgProfile.setImageURI(imageUri);
                    }
                });

        galleryLauncher.launch("image/*");
    }

    // Method to check if all user details have been saved and display a message
    private void checkAndDisplayAllDetailsSaved(String email) {
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            Toast.makeText(UserDetailsActivity.this, "All details have been saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnSaveDetails.getId()) {
            updateUserDetails();
        } else if (v.getId() == binding.btnUploadImage.getId()) {
            openGallery();
        }
    }
}


