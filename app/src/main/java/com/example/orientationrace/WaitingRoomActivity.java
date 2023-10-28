package com.example.orientationrace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class WaitingRoomActivity extends AppCompatActivity {

    Button bQuitRace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        // Get references to UI elements
        bQuitRace = findViewById(R.id.buttonQuitRace);

        bQuitRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to go back to the main activity
                Intent intent = new Intent(WaitingRoomActivity.this, MainActivity.class);
                startActivity(intent);

                // Finish the current second activity
                finish();
            }
        });

        // Start the 3rd activity, where the participants are shown after 10 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to launch the new activity
                Intent intent = new Intent(WaitingRoomActivity.this, ParticipantsActivity.class);
                startActivity(intent);
                finish(); // Optional: finish the current activity
            }
        }, 5000); // 10000 milliseconds = 10 seconds
    }
}