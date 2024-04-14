package com.example.travel_tales.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;


/**
 * @author Nabin Ghatani 2024-04-13
 */
public class ImageAdapterGridView extends BaseAdapter {
    private Context mContext;
    private List<Uri> imageUris;

    // Constructor to initialize the adapter with context and image URIs
    public ImageAdapterGridView(Context context, List<Uri> imageUris) {
        mContext = context;
        this.imageUris = imageUris;
    }

    // Returning the number of items in the data set
    public int getCount() {
        return imageUris.size();
    }

    // Getting the data item associated with the specified position in the data set
    public Uri getItem(int position) {
        return imageUris.get(position);
    }

    // Getting the row ID associated with the specified position in the list
    public long getItemId(int position) {
        return position;
    }

    // Getting a View that displays the data at the specified position in the data set
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        // If convertView is null, inflate a new ImageView
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(130, 130));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(16, 16, 16, 16);
        } else {
            // Reuse convertView if available
            imageView = (ImageView) convertView;
        }

        // Set image using the Uri
        imageView.setImageURI(imageUris.get(position));

        return imageView;
    }
}

