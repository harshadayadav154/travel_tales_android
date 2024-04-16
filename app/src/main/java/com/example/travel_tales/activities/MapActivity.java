package com.example.travel_tales.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import com.example.travel_tales.R;
import com.example.travel_tales.databinding.ActivityMapBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.Location;
import com.example.travel_tales.utility.NotificationUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nabin Ghatani 2024-04-13
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener, Geocoder.GeocodeListener, View.OnClickListener {
    private ActivityMapBinding mapBinding;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private String searchedQuery;
    private Location searchedLocation;

    private List<Location> userLocationList;
    private boolean isInitialMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflating layout using view binding
        mapBinding = ActivityMapBinding.inflate(getLayoutInflater());
        View ui = mapBinding.getRoot();
        setContentView(ui);
        init();
        registerEventListeners();
    }


    /**
     * Initializing the map and search view along with the geocoder object and dbHelper.
     */
    private void init() {
        // Obtaining the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        userLocationList = new ArrayList<>();

        //initializing geocoder object
        geocoder = new Geocoder(this);

        isInitialMapView = true;
        //mapBinding.mapView.setVisibility(View.GONE); //todo remove this later
    }

    private void registerEventListeners() {
        // Setting up search view listener
        mapBinding.searchView.setOnQueryTextListener(this);
        mapBinding.btnGoToHome.setOnClickListener(this);
    }


    /**
     * Callback method for when the map is ready to be used
     *
     * @param googleMap - Google map {@link GoogleMap}
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        //todo - remove this later
        /*runOnUiThread(() -> {
            this.mapBinding.progressBar.setVisibility(View.VISIBLE);
            this.mapBinding.mapView.setVisibility(View.GONE);
        });*/

        LatLng firstLocation = null;

        // If it's the initial map view, fetching user locations and add markers
        if (isInitialMapView) {
            try (DBHelper dbHelper = new DBHelper(getApplicationContext())) {
                // Fetching distinct locations by user id
                userLocationList = dbHelper.getDistinctLocationsByUserId(1); //todo - fix this later
            } catch (Exception ignore) {
            }

            // If there are user locations, adding markers for each location
            if (!userLocationList.isEmpty()) {
                userLocationList.forEach(location -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    // Adding a marker to the map at the user location
                    googleMap.addMarker(new MarkerOptions().position(latLng));
                });
                // Setting the initial LatLng to the first user location
                firstLocation = new LatLng(userLocationList.get(0).getLatitude(), userLocationList.get(0).getLongitude());
                // Setting isInitialMapView to false after initial setup
                isInitialMapView = false;
            }
        }

        // If there's a location set, updating the LatLng
        if (searchedLocation != null) {
            firstLocation = new LatLng(searchedLocation.getLatitude(), searchedLocation.getLongitude());
        }

        // If there's a LatLng set, adding a marker and animate the camera to the position
        if (firstLocation != null) {
            // Adding a marker to the map at the LatLng
            googleMap.addMarker(new MarkerOptions().position(firstLocation));
            // Animating the camera to the LatLng with a zoom level of 12
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12));
        }

        //todo remove this later
        /*runOnUiThread(() -> {
            this.mapBinding.progressBar.setVisibility(View.GONE);
            this.mapBinding.mapView.setVisibility(View.VISIBLE);
        });*/
    }


    /**
     * Callback method for when the user submits a search query
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false otherwise
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        this.searchedQuery = query;
        // Checking if the query is not empty
        if (!query.isEmpty()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> geocoder.getFromLocationName(query, 1, this));
        } else {
            // Showing a toast message indicating that the query is empty
            NotificationUtility.showNotification(this, "Please enter a location.");
        }
        return false;
    }


    /**
     * Callback method for when the search query text changes
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        this.searchedQuery = newText;
        return false;
    }


    /**
     * Callback method invoked when geocoding is successful and addresses are obtained.
     *
     * @param addresses The list of addresses obtained from geocoding.
     */
    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        // Checking if the list of addresses is not empty
        if (!addresses.isEmpty()) {
            // Getting the location from the list at the first position
            Address address = addresses.get(0);

            // Creating a LatLng object with the location's latitude and longitude
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            searchedLocation = new Location();
            searchedLocation.setName(searchedQuery);
            searchedLocation.setLatitude(latLng.latitude);
            searchedLocation.setLongitude(latLng.longitude);
            // Using runOnUiThread to perform UI operations as GeocodeListener methods might be called from a background thread
            runOnUiThread(() -> {
                // Adding a marker to the map at the specified position with the given title
                googleMap.addMarker(new MarkerOptions().position(latLng).title(searchedQuery));

                // Animating the camera to the specified position with a zoom level
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            });
        } else {
            // Using runOnUiThread to show toast message as UI operations should be done on the UI thread i.e main thread
            runOnUiThread(() -> {
                // Showing a toast message indicating that the location was not found
                NotificationUtility.showNotification(this, "Location not found.");
            });
        }
    }


    @Override
    public void onError(@Nullable String errorMessage) {
        Geocoder.GeocodeListener.super.onError(errorMessage);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.mapBinding.btnGoToHome.getId()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}

