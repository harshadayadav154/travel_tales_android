package com.example.travel_tales.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtility {
    private static final String TAG = "ImageUtility";

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            } catch (IOException e) {
                Log.e(TAG, "Error decoding bitmap: " + e.getMessage());
                throw e;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing InputStream: " + e.getMessage());
                }
            }
        } else {
            throw new FileNotFoundException("Could not open input stream for uri: " + uri);
        }
    }

    // Method to generate a unique image name
    public static String generateImageName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timeStamp = sdf.format(new Date());
        return "IMG_" + timeStamp + ".png";
    }

    // Method to save image to internal storage
    public static String saveImageToInternalStorage(Context context, Bitmap bitmapImage, String imageName) {
        File directory = createImageDirectoryInInternalStorage(context);
        File filePath = new File(directory, imageName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return filePath.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to create image directory in internal storage
    public static File createImageDirectoryInInternalStorage(Context context) {
        File imageDir = new File(context.getFilesDir(), "images");
        if (!imageDir.exists()) {
            if (imageDir.mkdirs()) {
                Log.d(TAG, "Internal image directory created");
            } else {
                Log.e(TAG, "Failed to create internal image directory");
            }
        }
        return imageDir;
    }
}
