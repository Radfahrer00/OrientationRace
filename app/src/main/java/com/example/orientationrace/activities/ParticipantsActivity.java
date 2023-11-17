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
import android.widget.Button;
import android.widget.TextView;

import com.example.orientationrace.LoadURLContents;
import com.example.orientationrace.MainActivity;
import com.example.orientationrace.MqttManager;
import com.example.orientationrace.participants.Participant;
import com.example.orientationrace.participants.ParticipantsAdapter;
import com.example.orientationrace.participants.ParticipantsDataset;
import com.example.orientationrace.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipantsActivity extends AppCompatActivity implements MqttCallback {

    // Participants dataset:
    public ParticipantsDataset participantsDataset = new ParticipantsDataset();
    private Long userCount = (long) 1;
    private RecyclerView recyclerView;
    String username = "";

    // For downloading the Madrid Garden File
    public static final String LOADWEBTAG = "LOAD_WEB_TAG";
    private String threadAndClass; // to clearly identify logs
    private static final String URL_GARDENS = "https://short.upm.es/3qnno";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private TextView text;
    ExecutorService downloadExecutor;
    ExecutorService mqttExecutor;
    JSONObject jsonObject;
    String[] randomGardensArray;

    // MQTT broker configurations
    public static final String MQTTCONNECTION = "MQTT_connection";
    private static final String TOPIC_PARTICIPANTS = "madridOrientationRace/participants";
    private String client_Id;
    private MqttManager mqttManager;
    Button bInformParticipants;

    // Condition Handler for Activity change
    private Handler checkConditionsHandler = new Handler();
    private static final int CHECK_CONDITIONS_INTERVAL = 8000; // 8 seconds

    private Runnable checkConditionsRunnable = new Runnable() {
        @Override
        public void run() {
            if (participantsDataset.getSize() >= 5 && randomGardensArray != null && randomGardensArray.length != 0) {
                // Create an Intent to launch the new activity
                Intent newIntent = new Intent(ParticipantsActivity.this, RaceCompassActivity.class);
                newIntent.putExtra("gardenNames", randomGardensArray);
                newIntent.putExtra("username", username);
                startActivity(newIntent);

                // Finish the current activity if needed
                finish();
            } else {
                // Conditions not met, schedule the next check
                checkConditionsHandler.postDelayed(this, CHECK_CONDITIONS_INTERVAL);
            }
        }
    };
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
        username = intent.getStringExtra("username");
        client_Id = username; // For MQTT

        // Create the current user as a new participant and add him to the dataset
        Participant currentParticipant = new Participant(username, (long)0);
        participantsDataset.addParticipant(currentParticipant);

        text = findViewById(R.id.HTTPTextView);
        bInformParticipants = findViewById(R.id.buttonInform);

        initRecyclerView();

        // Create an executor for the background tasks:
        downloadExecutor = Executors.newSingleThreadExecutor();
        // Submit download-related tasks to the download executor
        downloadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                readJSON(text);
            }
        });


        mqttExecutor = Executors.newSingleThreadExecutor();
        mqttManager = MqttManager.getInstance();
        Log.d(MQTTCONNECTION, "Handler defined");
        // Submit the MQTT-related tasks to the executor for background execution
        mqttExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(MQTTCONNECTION, "Starting MQTT Thread");

                // Connect to the MQTT broker when the activity starts.
                mqttManager.connect(client_Id);
                Log.d(MQTTCONNECTION, "Connection successful");

                // Once connected, subscribe to topics and publish messages
                subscribeToTopic();
                publishConnection();
            }
        });

        bInformParticipants.setOnClickListener(v -> {
            publishConnection();
        });

        checkConditionsHandler.postDelayed(checkConditionsRunnable, CHECK_CONDITIONS_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        // Remove the callback when the activity is destroyed to avoid memory leaks
        checkConditionsHandler.removeCallbacks(checkConditionsRunnable);
        super.onDestroy();
    }

    private void publishConnection() {
        try {
            mqttManager.publishMessage(TOPIC_PARTICIPANTS, client_Id);
            Log.d(MQTTCONNECTION, "Publishing successful");
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Publishing");
        }
    }

    private void subscribeToTopic() {
        try {
            mqttManager.subscribeToTopic(TOPIC_PARTICIPANTS, ParticipantsActivity.this);
            Log.d(MQTTCONNECTION, "Subscription successful");
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Subscription");
        }
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
                    try {
                        processJsonData(string_result);
                    } catch (Exception e) {
                        // Catch other exceptions including UnknownHostException
                        e.printStackTrace();
                        Log.e(LOADWEBTAG, "Exception occurred", e);
                        if (e instanceof UnknownHostException) {
                            // Handle UnknownHostException (network unavailable or host not reachable)
                            Log.e(LOADWEBTAG, "UnknownHostException: Unable to resolve host", e);
                            // Show a message to the user or take appropriate action
                            runOnUiThread(() -> {
                                // Update UI or show a message indicating network issues
                                text.setText("Network unavailable. Please check your internet connection.");
                            });
                        }
                    }
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
        Log.d(MQTTCONNECTION, "Loading URL Contents...");
        downloadExecutor.execute(loadURLContents);
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

    private void updateParticipantsList(MqttMessage mqttMessage) {
        try {
            String incomingMessage = new String(mqttMessage.getPayload());

            // Check if a participant with the same client_Id already exists
            boolean participantExists = false;
            for (Participant existingParticipant : participantsDataset.getParticipants()) {
                if (existingParticipant.getUsername().equals(incomingMessage)) {
                    participantExists = true;
                    break;
                }
            }

            if (!participantExists && !incomingMessage.equals(client_Id)) {
                Participant newParticipant = new Participant(incomingMessage, userCount);
                participantsDataset.addParticipant(newParticipant);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
                userCount = Long.sum(userCount, (long) 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MQTTCONNECTION, "Error in updateParticipantsList: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        // Handle the case when the connection to the broker is lost
        if (cause != null) {
            Log.e(MQTTCONNECTION, "Connection to MQTT broker lost: " + cause.getMessage());
        } else {
            Log.e(MQTTCONNECTION, "Connection to MQTT broker lost");
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        updateParticipantsList(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTTCONNECTION, "Delivery complete");
    }

}