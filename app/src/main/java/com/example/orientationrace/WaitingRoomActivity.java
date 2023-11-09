package com.example.orientationrace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WaitingRoomActivity extends AppCompatActivity {

    Button bQuitRace;
    TextView welcomeText;
    private Handler handler = new Handler();
    private Runnable launchDelayedActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        // Get references to UI elements
        bQuitRace = findViewById(R.id.buttonQuitRace);
        welcomeText = findViewById(R.id.welcomeText);

        // Create the get Intent object
        Intent intent = getIntent();
        // Receive the username
        String username = intent.getStringExtra("username");
        // Welcome the user to the Race
        welcomeText.setText("Hello " + username +"\n" + "Welcome to the Madrid Orientation Race 2023!");

        // Initialize the Runnable to launch DelayedActivity, the activity where participants are shown
        launchDelayedActivity = new Runnable() {
            @Override
            public void run() {
                // Start the DelayedActivity
                Intent intent = new Intent(WaitingRoomActivity.this, ParticipantsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish(); // Close the current (second) activity
            }
        };

        // Post the Runnable with a 10-second delay
        handler.postDelayed(launchDelayedActivity, 2000); // 10000 milliseconds (10 seconds)

        // Set a click listener for the Quit Race button to navigate back to the main activity
        bQuitRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // Create an Intent to go back to the main activity
                Intent intent = new Intent(WaitingRoomActivity.this, MainActivity.class);
                startActivity(intent);

                // Finish the current second activity
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Remove the callbacks from the handler when the user goes back
        handler.removeCallbacks(launchDelayedActivity);
    }
}