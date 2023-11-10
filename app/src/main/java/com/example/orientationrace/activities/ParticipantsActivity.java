package com.example.orientationrace.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.orientationrace.LoadURLContents;
import com.example.orientationrace.MqttHandler;
import com.example.orientationrace.participants.Participant;
import com.example.orientationrace.participants.ParticipantsAdapter;
import com.example.orientationrace.participants.ParticipantsDataset;
import com.example.orientationrace.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipantsActivity extends AppCompatActivity {

    // Participants dataset:
    private static final String TAG = "TAGListOfParticipants, ParticipantActivity";
    public ParticipantsDataset participantsDataset = new ParticipantsDataset();
    private RecyclerView recyclerView;

    // For downloading the Madrid Garden File
    public static final String LOADWEBTAG = "LOAD_WEB_TAG"; // to easily filter logs
    private String threadAndClass; // to clearly identify logs
    private static final String URL_GARDENS = "https://short.upm.es/3qnno";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private TextView text;
    ExecutorService es;
    JSONObject jsonObject;
    String[] randomGardensArray;

    // MQTT Connection
    private String client_Id;
    private MqttHandler mqttHandler;
    final String subscriptionTopic = "race/participants";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Build the logTag with the Thread and Class names:
        threadAndClass = "Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        // Create the get Intent object
        Intent intent = getIntent();
        // Receive the username
        String username = intent.getStringExtra("username");
        client_Id = username; // For MQTT

        // MQTT Connection
        //mqttHandler = new MqttHandler();
        //mqttHandler.connect(SERVER_URI, client_Id);


        // Create the current user as a new participant and add him to the dataset
        Participant currentParticipant = new Participant(username, (long)0);
        participantsDataset.addParticipant(currentParticipant);

        text = findViewById(R.id.HTTPTextView);

        initRecyclerView();

        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();
        readJSON(text);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to launch the new activity
                Intent intent = new Intent(ParticipantsActivity.this, RaceCompassActivity.class);
                intent.putExtra("gardenNames", randomGardensArray);
                startActivity(intent);
                finish(); // Optional: finish the current activity
            }
        }, 10000); // 10000 milliseconds = 10 seconds
    }

    // Define the handler that will receive the messages from the background thread:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(LOADWEBTAG, threadAndClass + ": message received from the background thread");

            if (msg.getData() != null) {
                String string_result = msg.getData().getString("text");
                if (string_result != null) {
                    processJsonData(string_result);
                }
            }
        }

        private void processJsonData(String jsonData) {
            try {
                jsonObject = new JSONObject(jsonData);
                JSONArray graph = jsonObject.getJSONArray("@graph");
                String[] gardenTitlesArray = extractTitlesFromJson(graph);
                randomGardensArray = getRandomGardens(gardenTitlesArray);

                text.setText(Arrays.toString(randomGardensArray));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Extracts titles from a JSON array and stores them in a string array.
         * @param jsonArray A JSON array containing objects to extract titles from.
         * @return An array of titles extracted from the JSON array.
         * @throws JSONException If there is an error in JSON parsing.
         */
        private String[] extractTitlesFromJson(JSONArray jsonArray) throws JSONException {
            String[] titleArray = new String[jsonArray.length()];

            // Iterate through the JSON array to extract titles.
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject graphItem = jsonArray.getJSONObject(i);
                if (graphItem.has("title")) {
                    // Extract and store the title in the titleArray.
                    titleArray[i] = graphItem.getString("title");
                }
            }
            return titleArray;
        }

        /**
         * Generates an array of random gardens by selecting unique elements from the original garden array.
         * @param originalGardensArray An array containing the source garden elements.
         * @return An array of 6 unique random garden names.
         */
        private String[] getRandomGardens(String[] originalGardensArray) {
            String[] randomGardensArray = new String[6];
            Random random = new Random();
            // Create a set to keep track of selected indices to ensure uniqueness.
            Set<Integer> selectedIndices = new HashSet<>();

            // Generate 6 unique random indices and select corresponding garden names.
            while (selectedIndices.size() < 6) {
                int randomIndex = random.nextInt(originalGardensArray.length);

                if (selectedIndices.add(randomIndex)) {
                    randomGardensArray[selectedIndices.size() - 1] = originalGardensArray[randomIndex];
                }
            }
            return randomGardensArray;
        }
    };

    public void readJSON(View view) {
        text.setText("Loading " + URL_GARDENS + "..."); // Inform the user by means of the TextView

        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_GARDENS);
        es.execute(loadURLContents);
    }

    private void initRecyclerView() {
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        ParticipantsAdapter recyclerViewAdapter = new ParticipantsAdapter(participantsDataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // by default, a linear layout is chosen:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void publishMessage(String topic, String message) {
        mqttHandler.publish(topic, message);
    }

    private void subscribeToTopic(String topic) {
        mqttHandler.subscribe(topic);
    }

}