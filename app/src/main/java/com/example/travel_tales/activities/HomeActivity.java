package com.example.travel_tales.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.travel_tales.activities.JournalActivity;
import com.example.travel_tales.activities.JournalGalleryActivity;
import com.example.travel_tales.activities.MapActivity;
import com.example.travel_tales.databinding.ActivityHomeBinding;
import com.example.travel_tales.databinding.ActivityMainBinding;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHomeBinding homeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = homeBinding.getRoot();
        setContentView(view);
        initialize();
    }

    private void initialize() {
        homeBinding.button.setOnClickListener(this);
        homeBinding.mapButton.setOnClickListener(this);
        homeBinding.capturedImages.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == homeBinding.button.getId()) {
            Intent intent = new Intent(this, JournalActivity.class);
            startActivity(intent);
        }else if (v.getId() == homeBinding.mapButton.getId()) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }else if (v.getId() == homeBinding.capturedImages.getId()) {
            Intent intent = new Intent(this, JournalGalleryActivity.class);
            startActivity(intent);
        }
    }
}