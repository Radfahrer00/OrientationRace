package com.example.orientationrace.views.activities;

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
import com.example.orientationrace.MqttManager;
import com.example.orientationrace.gardens.Garden;
import com.example.orientationrace.participants.Participant;
import com.example.orientationrace.participants.ParticipantsAdapter;
import com.example.orientationrace.participants.ParticipantsDataset;
import com.example.orientationrace.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity responsible for showing participants and initiating the race when conditions are met.
 */
public class ParticipantsActivity extends AppCompatActivity implements MqttCallback {

    // Participants dataset
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
    Garden[] randomGardensArray;

    // MQTT broker configurations
    public static final String MQTTCONNECTION = "MQTT_connection";
    private static final String TOPIC_PARTICIPANTS = "madridOrientationRace/participants";
    private String client_Id;
    private MqttManager mqttManager;
    ExecutorService mqttExecutor;
    Button bInformParticipants;

    // Condition Handler for Activity change
    private final Handler checkConditionsHandler = new Handler();
    private static final int CHECK_CONDITIONS_INTERVAL = 8000; // 8 seconds

    // Variable for the number of participants required to start the race
    private static final int PARTICIPANTS_REQUIRED = 1;

    /**
     * Initializes the activity, sets up the UI, and connects to the MQTT broker.
     *
     * @param savedInstanceState The saved instance state.
     */
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

        // Get the references to the UI elements
        text = findViewById(R.id.HTTPTextView);
        bInformParticipants = findViewById(R.id.buttonInform);

        initRecyclerView();

        // Create executors for the background task: Downloading the and parsing the open data file
        downloadExecutor = Executors.newSingleThreadExecutor();
        downloadExecutor.submit(() -> readJSON(text));

        // Create executors for the background task: Connecting and handling the MQTT Connection
        mqttExecutor = Executors.newSingleThreadExecutor();
        mqttManager = MqttManager.getInstance();
        mqttManager.setClientId(client_Id);
        // Submit the MQTT-related tasks to the executor for background execution
        mqttExecutor.submit(() -> {
            // Connect to the MQTT broker when the activity starts.
            mqttManager.connect(client_Id);
            subscribeToTopic();
            publishUserConnected();
        });

        // On Click Listener for The Inform Participants Button about the connection
        bInformParticipants.setOnClickListener(v -> publishUserConnected());

        // Check repeatedly if the conditions are met to go to the next activity
        checkConditionsHandler.postDelayed(checkConditionsRunnable, CHECK_CONDITIONS_INTERVAL);
    }

    /**
     * Runnable used to check conditions for initiating the race. When the sufficient participants
     * are connected and the checkpoints have been set.
     */
    private final Runnable checkConditionsRunnable = new Runnable() {
        @Override
        public void run() {
            if (participantsDataset.getSize() >= PARTICIPANTS_REQUIRED && randomGardensArray != null && randomGardensArray.length != 0) {
                // Create an Intent to launch the new activity
                Intent newIntent = new Intent(ParticipantsActivity.this, RaceActivity.class);
                // Convert 2D array to flat array for easier passing
                newIntent.putExtra("gardenNames", randomGardensArray);
                newIntent.putExtra("username", username);
                startActivity(newIntent);

                // Finish the current activity
                finish();
            } else {
                // Conditions not met, schedule the next check
                checkConditionsHandler.postDelayed(this, CHECK_CONDITIONS_INTERVAL);
            }
        }
    };


    /**
     * Called when the activity is about to be destroyed. Removes the callback to avoid memory leaks.
     */
    @Override
    protected void onDestroy() {
        checkConditionsHandler.removeCallbacks(checkConditionsRunnable);
        super.onDestroy();
    }

    /**
     * Publishes a message to the MQTT broker indicating that the user is connected.
     */
    private void publishUserConnected() {
        try {
            mqttManager.publishMessage(TOPIC_PARTICIPANTS, client_Id);
            Log.d(MQTTCONNECTION, "Publishing successful");
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Publishing");
        }
    }

    /**
     * Subscribes to the MQTT topic for receiving messages about connected participants.
     */
    private void subscribeToTopic() {
        try {
            mqttManager.subscribeToTopic(ParticipantsActivity.TOPIC_PARTICIPANTS, ParticipantsActivity.this);
            Log.d(MQTTCONNECTION, "Subscription successful");
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Subscription");
        }
    }

    // Define the handler that will receive the messages from the background thread
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(LOADWEBTAG, threadAndClass + ": message received from the background thread");

            if (msg.getData() != null) {
                // Check if the message contains the "gardens" key
                if (msg.getData().containsKey("gardens")) {
                    // Retrieve the array of Garden objects from the message
                    Garden[] gardens = (Garden[]) msg.getData().getSerializable("gardens");

                    // Now you can use the 'gardens' array in your activity
                    randomGardensArray = gardens;
                }
            }
            text.setText("Gardens loaded");
        }
    };

    /**
     * Initiates the process of reading JSON data from a specified URL.
     *
     * @param view The view associated with this method.
     */
    public void readJSON(View view) {
        text.setText("Loading " + URL_GARDENS + "..."); // Inform the user by means of the TextView

        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_GARDENS);
        Log.d(MQTTCONNECTION, "Loading URL Contents...");
        downloadExecutor.execute(loadURLContents);
    }

    /**
     * Initializes the RecyclerView to display the list of participants.
     */
    private void initRecyclerView() {
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        ParticipantsAdapter recyclerViewAdapter = new ParticipantsAdapter(participantsDataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the linear layout manager to be set.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Updates the list of participants based on the received MQTT message.
     *
     * @param mqttMessage The MQTT message containing information about connected participants.
     */
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

            // I f the participant eith the client_Id does not exist, add him to the dataset
            if (!participantExists && !incomingMessage.equals(client_Id)) {
                Participant newParticipant = new Participant(incomingMessage, userCount);
                participantsDataset.addParticipant(newParticipant);
                runOnUiThread(() -> recyclerView.getAdapter().notifyDataSetChanged());
                userCount = Long.sum(userCount, (long) 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MQTTCONNECTION, "Error in updateParticipantsList: " + e.getMessage());
        }
    }

    /**
     * Called when the connection to the MQTT broker is lost.
     *
     * @param cause The cause of the connection loss.
     */
    @Override
    public void connectionLost(Throwable cause) {
        // Handle the case when the connection to the broker is lost
        if (cause != null) {
            Log.e(MQTTCONNECTION, "Connection to MQTT broker lost: " + cause.getMessage());
        } else {
            Log.e(MQTTCONNECTION, "Connection to MQTT broker lost");
        }
    }

    /**
     * Called when a new MQTT message is received.
     *
     * @param topic   The topic of the received message.
     * @param message The received message.
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        updateParticipantsList(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTTCONNECTION, "Delivery complete");
    }

}