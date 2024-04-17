package com.example.travel_tales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travel_tales.databinding.ActivityDeveloperBinding;

public class DeveloperActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityDeveloperBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeveloperBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerEventListeners();
    }

    private void registerEventListeners() {
        binding.btnGoToHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnGoToHome.getId()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}