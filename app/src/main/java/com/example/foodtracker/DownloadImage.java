package com.example.foodtracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... strings) {
        URL url;
        HttpURLConnection urlConnection = null;
        int data;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream input = urlConnection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            urlConnection.disconnect();
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error", "Error at Download: " + e.toString());
        }

        return null;
    }
}
