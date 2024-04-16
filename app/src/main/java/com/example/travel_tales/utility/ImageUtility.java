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

/**
 * @author Nabin Ghatani 2024-04-15
 */
public class ImageUtility {
    private static final String TAG = "ImageUtility";

    /**
     * Converts a URI to a Bitmap.
     *
     * @param context The context.
     * @param uri     The URI of the image.
     * @return The Bitmap object.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Generates a unique image name based on the current timestamp.
     *
     * @return The generated image name.
     */
    public static String generateImageName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timeStamp = sdf.format(new Date());
        return "IMG_" + timeStamp + ".png";
    }

    /**
     * Saves a Bitmap image to internal storage.
     *
     * @param context      The context.
     * @param bitmapImage  The Bitmap image to save.
     * @param imageName    The name of the image file.
     * @return The absolute path of the saved image file.
     */
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

    /**
     * Creates a directory for storing images in internal storage.
     *
     * @param context The context.
     * @return The directory File object.
     */
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

    /**
     * Checks if the given image path is valid.
     *
     * @param imagePath The path of the image file.
     * @return True if the image path is valid, false otherwise.
     */
    public static boolean isValidImagePath(String imagePath) {
        // Check if the image path is not null or empty
        if (imagePath != null && !imagePath.isEmpty()) {
            // Create a File object from the image path
            File file = new File(imagePath);
            // Check if the file exists and is a file (not a directory)
            return file.exists() && file.isFile();
        }
        return false; // Return false for null or empty image paths
    }
}
