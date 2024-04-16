package com.example.travel_tales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travel_tales.R;
import com.example.travel_tales.databinding.ActivityMainBinding;
import com.example.travel_tales.utility.SliderAdapter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;

    // Array to hold layout resource IDs for slider screens
    int[] layouts;

    // Intent object for navigating to LoginActivity
    Intent intent;

    // Adapter for ViewPager
    SliderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        initialize();

    }

    private void initialize(){
        // Set click listeners for buttons
        binding.btnNext.setOnClickListener(this);
        binding.btnSkip.setOnClickListener(this);

        mAdapter = new SliderAdapter(layouts);
        binding.viewPager.setAdapter(mAdapter);
    }

    private void init(){
        // Define layout resource IDs for slider screens
        layouts = new int[]{
                R.layout.slide_screen_1,
                R.layout.slide_screen_2,
                R.layout.slide_screen_3
        };
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.btnNext.getId()){
            // Get the next item in ViewPager
            int current = getItem(+1);
            if(current < layouts.length){
                // Move to the next screen
                binding.viewPager.setCurrentItem(current);
                // Change button text to "Continue"

            }
            else{
                launchHomeScreen();
            }
        } else if (v.getId() == binding.btnSkip.getId()) {
            launchHomeScreen();
        }
    }

    private void launchHomeScreen() {
        intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

    }

    // Get the item index in ViewPager
    private int getItem(int i) {
        return binding.viewPager.getCurrentItem() + i;
    }
}