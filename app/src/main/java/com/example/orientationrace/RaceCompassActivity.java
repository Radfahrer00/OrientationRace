package com.example.orientationrace;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.gardens.Garden;
import com.example.orientationrace.gardens.GardensAdapter;
import com.example.orientationrace.gardens.GardensDataset;

public class RaceCompassActivity extends AppCompatActivity implements SensorEventListener {

    // Gardens dataset:
    private static final String TAG = "TAGListOfGardens, GardenActivity";
    public GardensDataset gardensDataset = new GardensDataset();
    private RecyclerView recyclerView;

    private ImageView compassImage;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_compass);

        // Create the get Intent object
        Intent intent = getIntent();
        // Receive the username
        String[] gardenNames = intent.getStringArrayExtra("gardenNames");

        for (int i = 0; i < gardenNames.length; i++) {
            Garden garden = new Garden(gardenNames[i], (long) i);
            gardensDataset.addGarden(garden);
        }

        initRecyclerView();

        compassImage = findViewById(R.id.compassImageView);

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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

    private float calculateDegree(float[] values) {
        // Calculer l'azimut à partir des données du magnétomètre
        float azimuth = (float) Math.toDegrees(Math.atan2(values[1], values[0]));
        return azimuth;
    }

    private void rotateCompass(float degree) {
        RotateAnimation ra = new RotateAnimation(
                currentDegree, degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        ra.setDuration(250);
        ra.setFillAfter(true);

        compassImage.startAnimation(ra);
        currentDegree = degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Gérer les changements d'exactitude des capteurs si nécessaire.
    }

    private void initRecyclerView() {
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.gardensRecyclerView);
        GardensAdapter recyclerViewAdapter = new GardensAdapter(gardensDataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // by default, a linear layout is chosen:
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }
}
