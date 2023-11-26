package com.example.orientationrace.model;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.orientationrace.views.activities.ParticipantsActivity;
import com.example.orientationrace.model.gardens.Garden;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A Runnable class for downloading text-based content from a URL and communicating with the UI thread.
 * This class is designed to run in a separate thread.
 */
public class LoadURLContents implements Runnable {
    Handler creator; // handler to the main activity, who creates this task
    private final String expectedContent_type;
    private final String string_URL;


    /**
     * Constructs a LoadURLContents object with the specified parameters.
     *
     * @param handler              The handler to the creator of this object.
     * @param cnt_type             The content type expected (e.g., "application/vnd.google-earth.kml+xml").
     * @param strURL               The URL to load.
     */
    public LoadURLContents(Handler handler, String cnt_type, String strURL) {
        creator = handler;
        expectedContent_type = cnt_type;
        string_URL = strURL;
    }

    /**
     * The method executed when the thread is started. Downloads content from the specified URL,
     * validates the content type, and sends a message to the UI thread.
     */
    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        // initial preparation of the message to communicate with the UI Thread:
        Message msg = creator.obtainMessage();
        Bundle msg_data = msg.getData();

        StringBuilder response = new StringBuilder(); // This string will contain the loaded contents of a text resource
        HttpURLConnection urlConnection;

        // Build the string with thread and Class names (used in logs):
        String threadAndClass = "Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        Log.d(ParticipantsActivity.LOADWEBTAG, threadAndClass + ": run() called, starting load");

        try {
            URL url = new URL(string_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            String actualContentType = urlConnection.getContentType(); // content-type header from HTTP server
            InputStream is = urlConnection.getInputStream();

            // Extract MIME type and subtype (get rid of the possible parameters present in the content-type header
            // Content-type: type/subtype;parameter1=value1;parameter2=value2...
            if((actualContentType != null) && (actualContentType.contains(";"))) {
                Log.d(ParticipantsActivity.LOADWEBTAG, threadAndClass + ": Complete HTTP content-type header from server = " + actualContentType);
                int beginparam = actualContentType.indexOf(";");
                actualContentType = actualContentType.substring(0, beginparam);
            }
            Log.d(ParticipantsActivity.LOADWEBTAG, threadAndClass + ": MIME type reported by server = " + actualContentType);

            if (expectedContent_type.equals(actualContentType)) {
                // We check that the actual content type got from the server is the expected one
                // and if it is, download text
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(reader);
                // We read the text contents line by line and add them to the response:
                String line = in.readLine();
                while (line != null) {
                    response.append(line).append("\n");
                    line = in.readLine();
                }

                // Parse the JSON string and return an array of 6 Gardens
                Garden[] gardens = parseJsonString(response.toString());

                // Send the array of Gardens to the UI thread using the handler
                msg_data.putSerializable("gardens", gardens);
                
            } else { // content type not supported
                response = new StringBuilder("Actual content type different from expected (" +
                        actualContentType + " vs " + expectedContent_type + ")");
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            response = new StringBuilder(e.toString());
        }

        Log.d(ParticipantsActivity.LOADWEBTAG, threadAndClass + ": load complete, sending message to UI thread");
        if (!"".equals(response.toString())) {
            msg_data.putString("text", response.toString());
        }
        msg.sendToTarget();
    }

    /**
     * Parses a JSON string to extract information about gardens.
     *
     * @param jsonString The JSON string to parse.
     * @return An array of Garden objects parsed from the JSON string.
     * @throws JSONException If there is an error in JSON parsing.
     */
    private Garden[] parseJsonString(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray graph = jsonObject.getJSONArray("@graph");

        String[][] gardenInfoArray = extractGardensFromJson(graph);
        return getRandomGardens(gardenInfoArray);
    }

    /**
     * Extracts information about gardens from a JSON array.
     *
     * @param jsonArray A JSON array containing objects representing gardens.
     * @return A 2D array containing information about gardens (title, latitude, longitude).
     * @throws JSONException If there is an error in JSON parsing.
     */
    private String[][] extractGardensFromJson(JSONArray jsonArray) throws JSONException {
        String[][] gardenArray = new String[jsonArray.length()][3];

        // Iterate through the JSON array to extract titles, latitude and longitude.
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject graphItem = jsonArray.getJSONObject(i);
            // Extract and store the title in the gardenArray.
            if (graphItem.has("title")) {
                // Extract and store the title in the gardenArray.
                gardenArray[i][0] = graphItem.getString("title");
            }

            // Extract and store the latitude in the gardenArray.
            if (graphItem.has("location") && graphItem.getJSONObject("location").has("latitude")) {
                gardenArray[i][1] = String.valueOf(graphItem.getJSONObject("location").getDouble("latitude"));
            }

            // Extract and store the longitude in the gardenArray.
            if (graphItem.has("location") && graphItem.getJSONObject("location").has("longitude")) {
                gardenArray[i][2] = String.valueOf(graphItem.getJSONObject("location").getDouble("longitude"));
            }
        }
        return gardenArray;
    }

    /**
     * Generates an array of random gardens based on the provided garden details array.
     *
     * @param gardenDetailsArray An array containing information about gardens.
     * @return An array of Garden objects representing randomly selected gardens.
     */
    private Garden[] getRandomGardens(String[][] gardenDetailsArray) {
        Garden[] randomGardensArray = new Garden[6];
        Random random = new Random();
        // Create a set to keep track of selected indices to ensure uniqueness.
        Set<Integer> selectedIndices = new HashSet<>();

        // Generate 6 unique random indices and select corresponding garden details.
        while (selectedIndices.size() < 6) {
            int randomIndex = random.nextInt(gardenDetailsArray.length);

            if (selectedIndices.add(randomIndex)) {
                String title = gardenDetailsArray[randomIndex][0];
                double latitude = Double.parseDouble(gardenDetailsArray[randomIndex][1]);
                double longitude = Double.parseDouble(gardenDetailsArray[randomIndex][2]);

                randomGardensArray[selectedIndices.size() - 1] = new Garden(title, latitude, longitude);
            }
        }
        return randomGardensArray;
    }


}

