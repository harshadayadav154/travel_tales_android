package com.example.travel_tales.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Nabin Ghatani 2024-04-13
 */
public class ImageAdapterGridView extends BaseAdapter {
    private final Context mContext;
    private List<String> imagePaths;
    private int width;
    private int height;

    // Constructor to initialize the adapter with context
    public ImageAdapterGridView(Context context, int width, int height) {
        mContext = context;
        this.imagePaths = new ArrayList<>();
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the list of image paths to be displayed.
     * Clears existing paths to prevent duplication or stale data,
     * then adds all new paths to the list.
     * Notifies the adapter to refresh the views with new data.
     *
     * @param imagePaths The list of image paths to be set.
     */
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths.clear(); // Clearing existing paths to prevent duplication or stale data
        this.imagePaths.addAll(imagePaths); // Adding all new paths
        notifyDataSetChanged(); // Notifying the adapter to refresh the views with new data
    }

    /**
     * Retrieves the list of image paths currently set.
     *
     * @return The list of image paths.
     */
    public List<String> getImagePaths() {
        return imagePaths;
    }


    // Returning the number of items in the data set
    @Override
    public int getCount() {
        return imagePaths.size();
    }

    // Getting the data item associated with the specified position in the data set
    @Override
    public String getItem(int position) {
        return imagePaths.get(position);
    }

    // Getting the row ID associated with the specified position in the list
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Getting a View that displays the data at the specified position in the data set
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        // If convertView is null, inflate a new ImageView
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setPadding(16, 16, 16, 16);
        } else {
            // Reusing convertView if available
            imageView = (ImageView) convertView;
        }

        // Get the image path at the specified position
        String imagePath = imagePaths.get(position);

        // Checking if the image path is valid and not empty
        if (imagePath != null && !imagePath.isEmpty()) {
            // Creating a File object from the image path
            File imgFile = new File(imagePath);

            // Checking if the file exists
            if (imgFile.exists()) {
                // Use Glide to load the image into the ImageView
                Glide.with(mContext).load(imgFile).into(imageView);
            }
        } else {
            // Log an error if the image path is invalid or empty
            Log.e("GlideLoadError", "Invalid image path: " + imagePath);
        }

        // Returning the configured ImageView
        return imageView;
    }
}

