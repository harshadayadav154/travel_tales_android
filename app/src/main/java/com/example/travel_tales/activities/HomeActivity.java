package com.example.travel_tales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.travel_tales.R;
import com.example.travel_tales.databinding.ActivityHomeBinding;
import com.example.travel_tales.fragments.AddJournalFragment;
import com.example.travel_tales.fragments.DeleteJournalFragment;
import com.example.travel_tales.fragments.ListJournalFragment;
import com.example.travel_tales.fragments.UpdateJournalFragment;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHomeBinding homeBinding;

    ActionBarDrawerToggle mToggle;

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

        // creating navigation drawer
        mToggle = new ActionBarDrawerToggle(this, homeBinding.drawerLayout, homeBinding.materialToolbar, R.string.open, R.string.close);
        homeBinding.drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        setSupportActionBar(homeBinding.materialToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        SetNavigationDrawer();
        setBottomNavigation();
    }

    // Initializing navigation drawer items to the fragments
    private void SetNavigationDrawer() {
        homeBinding.navView.setNavigationItemSelectedListener(item -> {
            Fragment frag = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_add_journal) {
                frag = new AddJournalFragment();
            } else if (itemId == R.id.nav_delete_journal) {
                frag = new DeleteJournalFragment();
            } else if (itemId == R.id.nav_list_journal) {
                frag = new ListJournalFragment();
            } else if (itemId == R.id.nav_update_journal) {
                frag = new UpdateJournalFragment();
            }

            if (frag != null) {
                FragmentTransaction frgTrans = getSupportFragmentManager().beginTransaction();
                frgTrans.replace(R.id.frame, frag);
                frgTrans.commit();

                homeBinding.gridView.setVisibility(View.GONE);
                homeBinding.navView.setVisibility(View.GONE);
                homeBinding.drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == homeBinding.button.getId()) {
            /*Intent intent = new Intent(this, JournalActivity.class);
            startActivity(intent);*/
        } else if (v.getId() == homeBinding.mapButton.getId()) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        } else if (v.getId() == homeBinding.capturedImages.getId()) {
            Intent intent = new Intent(this, JournalGalleryActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the bottom navigation view.
     * When a menu item is selected, it replaces the content frame layout with the corresponding fragment.
     */
    private void setBottomNavigation() {
        // Set listener for bottom navigation
        homeBinding.bottomNavView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_search) {
                //todo
            } else if (item.getItemId() == R.id.nav_bottom_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } /*else if (item.getItemId() == R.id.profile) {
                // Replacing the content frame layout with the ProfileFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, new ProfileFragment())
                        .commit();
            }*/
            // Returning true to indicate the item is selected
            return true;
        });
    }
}