package com.example.orientationrace.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.model.MqttManager;
import com.example.orientationrace.R;
import com.example.orientationrace.model.gardens.Garden;
import com.example.orientationrace.model.gardens.GardensAdapter;
import com.example.orientationrace.model.gardens.GardensDataset;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RaceActivity extends AppCompatActivity implements SensorEventListener, OnInitListener, MqttCallback {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_FIRST_TIME = "first_time";

    // Configurations for the compass
    private ImageView compassImage;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];
    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];

    // Gardens dataset:
    public GardensDataset gardensDataset = new GardensDataset();
    final GardensAdapter gardensAdapter = new GardensAdapter(gardensDataset, this);

    // Text to Speech Accessibility
    private TextToSpeech textToSpeech;

    private static final long SPEECH_DELAY_MILLIS = 2000; // Delay 2 seconds
    private Handler speechHandler;

    private float correctedDegree ;

    // To see the current Location on the map
    private Button bCurrentLocation;
    private boolean isButtonAvailable;

    // MQTT Connection
    public static final String MQTTCONNECTION = "MQTT_connection";
    private static final String TOPIC_CHECKPOINTS = "madridOrientationRace/checkpoints";
    private MqttManager mqttManager;
    ExecutorService mqttExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
        textToSpeech = new TextToSpeech(this, this);
        speechHandler = new Handler();
        startSpeechTimer();

        // Get references to UI elements
        compassImage = findViewById(R.id.compassImageView);
        bCurrentLocation = findViewById(R.id.buttonCurrentLocation);

        showTutorial();

        // Receive the Gardens
        Garden[] gardens = (Garden[]) getIntent().getSerializableExtra("gardenNames");

        for (int i = 0; i < gardens.length; i++) {
            Garden garden = new Garden(gardens[i].getGardenName(), gardens[i].getLatitude (), gardens[i].getLongitude(), (long) i);
            gardensDataset.addGarden(garden);
        }

        initRecyclerView();

        mqttExecutor = Executors.newSingleThreadExecutor();
        mqttManager = MqttManager.getInstance();

        subscribeToTopic();

        // Applying OnLongClickListener to our Adapter
        gardensAdapter.setOnLongClickListener();

        gardensAdapter.setOnItemClickListener();


        isButtonAvailable = true;

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        bCurrentLocation.setOnClickListener(v -> {
            if (isButtonAvailable) {
                startCurrentLocationActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            floatGravity = event.values;
        } else if (event.sensor == magnetometer) {
            floatGeoMagnetic = event.values;
        }

        SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
        SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

        float degree = (float) (-floatOrientation[0] * 180 / 3.14159);

        // Ensure the degree is positive
        if (degree < 0) {
            degree += 360;
        }

        // Corrected degree for your specific case
        correctedDegree = degree;

        // Set the compass image rotation
        compassImage.setRotation(correctedDegree);

        // Check orientation and speak
        if (isOrientationInRange(correctedDegree, 0.0f, 20.0f)) {
            speakOrientation("Facing North");
        } else if (isOrientationInRange(correctedDegree, 70.0f, 110.0f)) {
            speakOrientation("Facing East");
        } else if (isOrientationInRange(correctedDegree, 170.0f, 190.0f)) {
            speakOrientation("Facing South");
        } else if (isOrientationInRange(correctedDegree, 250.0f, 290.0f)) {
            speakOrientation("Facing West");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private String getOrientationLabel(float degree) {
        if (degree >= 0.0f && degree < 80.0f){
            return "North";
        }  else if (degree >= 90.0f && degree < 160.0f) {
            return "East";
        }  else if (degree >= 180.0f && degree < 260.0f) {
            return "South";
        }  else if (degree >= 280.0f && degree < 360.0f) {
            return "West";
        } else {
            return "";
        }
    }

    private void initRecyclerView() {
        // Prepare the RecyclerView:
        RecyclerView recyclerView = findViewById(R.id.gardensRecyclerView);
        GardensAdapter recyclerViewAdapter = new GardensAdapter(gardensDataset, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set Grid as layout manager
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void startCurrentLocationActivity() {
        Intent intentLocation = new Intent(RaceActivity.this, CurrentLocationActivity.class);
        startActivity(intentLocation);
        // Disable the button immediately for 5 Minutes
        disableButtonForFiveMinutes();

        Toast.makeText(RaceActivity.this, "Button disabled for 5 minutes!", Toast.LENGTH_SHORT).show();
    }

    private void disableButtonForFiveMinutes() {
        isButtonAvailable = false;
        bCurrentLocation.setEnabled(false);
        bCurrentLocation.setText("Not available");
        bCurrentLocation.setTextColor(Color.WHITE);

        // After 5 minutes, enable the button again
        new Handler().postDelayed(() -> {
            isButtonAvailable = true;
            bCurrentLocation.setEnabled(true);
            bCurrentLocation.setText("See current location");
        }, 2 * 10 * 1000); // 5 minutes in milliseconds
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "Language is not supported.");
            }
        } else {
            Log.e("TextToSpeech", "Initialization failed.");
            // Try reinitializing
            textToSpeech = new TextToSpeech(this, this);
        }
    }

    private void speakOrientation(String orientation) {
        // Use the TextToSpeech to speak the orientation
        textToSpeech.speak(orientation, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    private void startSpeechTimer() {
        speechHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call the function to speak orientation at each timer expiration
                speakOrientation(getOrientationLabel(correctedDegree));
                // Schedule the next execution of the timer
                speechHandler.postDelayed(this, SPEECH_DELAY_MILLIS);
            }
        }, SPEECH_DELAY_MILLIS);
    }

    private void subscribeToTopic() {
        try {
            mqttManager.subscribeToTopic(TOPIC_CHECKPOINTS, RaceActivity.this);
            Log.d(MQTTCONNECTION, "Subscription successful to " + TOPIC_CHECKPOINTS);
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Subscription");
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
        try {
            String incomingMessage = new String(message.getPayload());
            Log.d(MQTTCONNECTION, "Message arrived: " + incomingMessage);

            // Use a Handler to post a Runnable to the main (UI) thread
            new Handler(Looper.getMainLooper()).post(() -> {
                // This code will run on the main thread
                Toast.makeText(RaceActivity.this, incomingMessage, Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private void showTutorial() {
        // Check if it's the first time
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstTime = settings.getBoolean(PREF_FIRST_TIME, true);

        //if (firstTime) {
            // Show the overlay layout
            showOverlay();

            // Update the shared preferences to indicate that the user has seen the tutorial
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREF_FIRST_TIME, false);
            editor.apply();
        //}
    }

    private void showOverlay() {
        View overlayView = LayoutInflater.from(this).inflate(R.layout.tutorial_overlay, null);
        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.addView(overlayView);

        // Add OnClickListener to the close button
        Button closeButton = overlayView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            // Remove the overlay view when the close button is clicked
            rootView.removeView(overlayView);
        });
    }

    private boolean isOrientationInRange(float degree, float start, float end) {
        // Check if the degree is in the specified range
        return degree >= start && degree <= end;
    }
}