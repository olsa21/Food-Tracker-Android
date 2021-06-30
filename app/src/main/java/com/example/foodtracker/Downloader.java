package com.example.foodtracker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        String jsonResult = "";
        URL url;
        HttpURLConnection urlConnection = null;
        int data;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            data = reader.read();
            while (data != -1) {
                jsonResult += (char) data;
                data = reader.read();
            }
            urlConnection.disconnect();
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error", "Error at Download: " + e.toString());
        }

        return null;
    }
}
