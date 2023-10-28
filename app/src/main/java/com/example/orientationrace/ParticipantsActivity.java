package com.example.orientationrace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ParticipantsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to launch the new activity
                Intent intent = new Intent(ParticipantsActivity.this, RaceCompassActivity.class);
                startActivity(intent);
                finish(); // Optional: finish the current activity
            }
        }, 5000); // 10000 milliseconds = 10 seconds
    }
}