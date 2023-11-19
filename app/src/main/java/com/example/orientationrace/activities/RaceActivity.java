package com.example.orientationrace.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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

import com.example.orientationrace.MqttManager;
import com.example.orientationrace.R;
import com.example.orientationrace.gardens.Garden;
import com.example.orientationrace.gardens.GardensAdapter;
import com.example.orientationrace.gardens.GardensDataset;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RaceActivity extends AppCompatActivity implements SensorEventListener, OnInitListener, MqttCallback {

    // Gardens dataset:
    private static final String TAG = "TAGListOfGardens, GardenActivity";
    public GardensDataset gardensDataset = new GardensDataset();

    private ImageView compassImage;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float currentDegree = 0f;

    final GardensAdapter gardensAdapter = new GardensAdapter(gardensDataset, this);

    private TextToSpeech textToSpeech;

    private static final long SPEECH_DELAY_MILLIS = 2000; // Délai de 2 secondes
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
    private String username;
    private int checkpointsReached;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_compass);

        // Create the get Intent object
        Intent intent = getIntent();
        // Receive the username
        String[] gardenNames = intent.getStringArrayExtra("gardenNames");
        username = intent.getStringExtra("username");

        for (int i = 0; i < gardenNames.length; i++) {
            Garden garden = new Garden(gardenNames[i], (long) i);
            gardensDataset.addGarden(garden);
        }

        textToSpeech = new TextToSpeech(this, this);

        initRecyclerView();
        checkpointsReached = 0;

        mqttExecutor = Executors.newSingleThreadExecutor();
        mqttManager = MqttManager.getInstance();

        subscribeToTopic();

        // Applying OnLongClickListener to our Adapter
        gardensAdapter.setOnLongClickListener(new GardensAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(int position, Garden garden) {
                showPopup(position);
            }
        });

        compassImage = findViewById(R.id.compassImageView);
        bCurrentLocation = findViewById(R.id.buttonCurrentLocation);
        isButtonAvailable = true;

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        speechHandler = new Handler();
        startSpeechTimer();

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
            // Handle accelerometer data
        } else if (event.sensor == magnetometer) {
            // Handle magnetometer data
            float degree = calculateDegree(event.values);

            rotateCompass(degree);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private float calculateDegree(float[] values) {
        // Calculer l'azimut à partir des données du magnétomètre
        //values = normalize(values);
        float azimuth = (float) Math.toDegrees(Math.atan2(values[1], values[0]));
        // Correction pour assurer que l'azimut est dans la plage [0, 360)
        azimuth = (azimuth + 360) % 360;

        return azimuth;
    }
    private static final float INITIAL_ROTATION_OFFSET = 300.0f; // Ajustez cette valeur selon votre image

    private void rotateCompass(float degree) {
        correctedDegree = currentDegree + INITIAL_ROTATION_OFFSET;
        //speakOrientation(getOrientationLabel(degree));
        RotateAnimation ra = new RotateAnimation(
                correctedDegree, degree + INITIAL_ROTATION_OFFSET,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        ra.setDuration(250);
        ra.setFillAfter(true);

        compassImage.startAnimation(ra);
        currentDegree = degree;
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

    // Method to show a Popup window requesting the user to confirm the checkpoint
    public void showPopup(int position) {
        // Create a Dialog object
        Dialog popupDialog = new Dialog(this);

        // Set the content view to the layout created for the popup
        popupDialog.setContentView(R.layout.garden_reached_confirmation_popup);

        // Get reference to the "Cancel" button in the popup layout and add onClick Listener
        Button bCancel = popupDialog.findViewById(R.id.buttonCancel);
        bCancel.setOnClickListener(v -> {
            // Close popup when the button is clicked
            popupDialog.dismiss();
        });

        // Get reference to the "Confirm" button in the popup layout and add onClick Listener
        Button bConfirm = popupDialog.findViewById(R.id.buttonConfirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gardensAdapter.itemClickedState[position] = true;
                gardensAdapter.notifyItemChanged(position);
                popupDialog.dismiss();
            }
        });

        // Show the popup
        popupDialog.show();
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

        // After 5 minutes, enable the button again
        new Handler().postDelayed(() -> {
            isButtonAvailable = true;
            bCurrentLocation.setEnabled(true);
            bCurrentLocation.setText("See current location");
        }, 2 * 10 * 1000); // 5 minutes in milliseconds
    }
    private float[] normalize(float[] values) {
        float norm = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        values[0] /= norm;
        values[1] /= norm;
        values[2] /= norm;
        return values;
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Définir la langue pour le discours (dans cet exemple, anglais)
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {

            }
        } else {
            float speechRate = 0.5f; // Vous pouvez ajuster cette valeur selon vos préférences
            textToSpeech.setSpeechRate(speechRate);
        }
    }

    private void speakOrientation(String orientation) {
        // Utiliser le TextToSpeech pour lire l'orientation
        textToSpeech.speak("" + orientation, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    private void startSpeechTimer() {
        speechHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Appeler la fonction de synthèse vocale à chaque expiration de la minuterie
                speakOrientation(getOrientationLabel(correctedDegree-300.0f));
                // Programmer la prochaine exécution de la minuterie
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

    private void publishCheckpointReached(int checkpointNumber) {
        String message = username + " reached Checkpoint Number: " + checkpointNumber;
        try {
            mqttManager.publishMessage(TOPIC_CHECKPOINTS, message);
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Publishing");
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
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            String incomingMessage = new String(message.getPayload());
            Log.d(MQTTCONNECTION, "Message arrived: " + incomingMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}