package com.example.travel_tales.activities;

import android.content.Intent;
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
import com.example.travel_tales.utility.SharedPreferencesUtil;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityUserDetailsBinding binding;
    private DBHelper dbHelper;
    private Uri imageUri;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        validateData();
        registerEventListeners();
        initDataBinding();
    }

    private void initDataBinding() {
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());
            binding.etFirstName.setText(user.getFirstName());
            binding.etLastName.setText(user.getLastName());
        }
    }

    private void validateData() {
        String userEmail = SharedPreferencesUtil.getEmailId(getApplicationContext());
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
        binding.btnGoToHome.setOnClickListener(this);
    }

    // Method to fetch and display user details
    private void fetchUserDetails(String email) {
        user = dbHelper.getUserByEmail(email);
        if (user != null) {
            Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update user details
    private void updateUserDetails() {
        // Update user details in the database
        boolean isUpdated = dbHelper.updateUserDetails(user);

        // Show appropriate message
        if (isUpdated) {
            Toast.makeText(UserDetailsActivity.this, "Your details updated", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnSaveDetails.getId()) {
            updateUserDetails();
        } else if (v.getId() == binding.btnUploadImage.getId()) {
            openGallery();
        } else if (v.getId() == binding.btnGoToHome.getId()) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}


