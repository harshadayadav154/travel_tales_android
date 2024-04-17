package com.example.travel_tales.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.travel_tales.databinding.ActivitySignUpBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.User;

import java.util.Objects;

/**
 * @author Yashkumar Patel 2024-04-15
 */
public class SignUpActivity extends Activity {
    private ActivitySignUpBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Initialize UI components and register event listeners
        init();
    }

    // Initialize UI components and register event listeners
    private void init() {
        // Set click listener for sign-up button
        binding.btnSignUp.setOnClickListener(v -> registerUser());

        // Set click listener for back to sign-in button
        binding.btnBackToSignIn.setOnClickListener(v -> {
            // Navigate back to sign-in page
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish(); // Close the current activity
        });
    }

    // Method to register a new user
    private void registerUser() {
        // Get user input
        String email = Objects.requireNonNull(binding.etNewEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etNewPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(binding.etConfirmPassword.getText()).toString().trim();

        // Validate user email
        if (email.isEmpty()) {
            // Showing error if email field is empty
            binding.etNewEmail.setError("Email is required");
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Showing error if email is invalid
            binding.etNewEmail.setError("Please enter a valid email.");
            return;
        } else if (password.isEmpty()) {
            binding.etNewPassword.setError("Password is required.");
            return;
        } else if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.setError("Confirm password is required.");
            return;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new User object
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        // Add the user to the database
        if (dbHelper.addUser(user)) {
            Toast.makeText(SignUpActivity.this, "User credentials saved", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}
