package com.example.travel_tales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travel_tales.activities.JournalActivity;
import com.example.travel_tales.activities.JournalGalleryActivity;
import com.example.travel_tales.activities.MapActivity;
import com.example.travel_tales.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initialize();
    }

    private void initialize() {
        binding.button.setOnClickListener(this);
        binding.mapButton.setOnClickListener(this);
        binding.capturedImages.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.button.getId()) {
            Intent intent = new Intent(this, JournalActivity.class);
            startActivity(intent);
        }else if (v.getId() == binding.mapButton.getId()) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }else if (v.getId() == binding.capturedImages.getId()) {
            Intent intent = new Intent(MainActivity.this, JournalGalleryActivity.class);
            startActivity(intent);
        }
    }
}