package com.example.travel_tales.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.travel_tales.databinding.ActivitySignInBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.utility.NotificationUtility;
import com.example.travel_tales.utility.SharedPreferencesUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

/**
 * @author Yashkumar Patel 2024-04-15
 */
public class SignInActivity extends Activity implements View.OnClickListener {
    private ActivitySignInBinding binding;
    private DBHelper dbHelper;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Register click listeners for buttons
        registerEventListeners();
    }

    /**
     * Register event listeners for buttons.
     */
    private void registerEventListeners() {
        binding.btnSignIn.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);
    }

    /**
     * Handle button clicks.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnSignIn.getId()) {
            signInUser();
        } else if (v.getId() == binding.btnSignUp.getId()) {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        }
    }

    /**
     * Sign in the user.
     */
    private void signInUser() {
        // Get email and password from EditText fields
        email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();

        if (isValid()) {
            // Check if user exists in the database
            if (dbHelper.checkUser(email, password)) {
                // Get user ID from the database
                int userId = dbHelper.getUserId(email);

                // Save user email and ID in SharedPreferences
                saveUserData(email, userId);

                // Proceed to HomeActivity
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(intent);
                NotificationUtility.showNotification(this, "Login Successful");
            } else {
                new MaterialAlertDialogBuilder(SignInActivity.this)
                        .setTitle("Login Failed")
                        .setMessage("Invalid Email and/or Password")
                        .setPositiveButton("Okay", (dialog, which) -> {

                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {

                        }).show();
            }
        }
    }


    /**
     * Save user's email and ID in SharedPreferences.
     *
     * @param email  The user's email.
     * @param userId The user's ID.
     */
    private void saveUserData(String email, int userId) {
        SharedPreferencesUtil.clearSession(getApplicationContext());
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("EMAIL", email);
        editor.putInt("USER_ID", userId);
        editor.apply();
    }

    /**
     * This method checks if the entered username and password are not empty
     *
     * @return true if all fields are valid, false otherwise
     */
    private boolean isValid() {
        email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
        if (email.isEmpty()) {
            // Showing error if email field is empty
            binding.etEmail.setError("Email is required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Showing error if email is invalid
            binding.etEmail.setError("Please enter a valid email.");
            return false;
        } else if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            return false;
        }
        return true;
    }
}

