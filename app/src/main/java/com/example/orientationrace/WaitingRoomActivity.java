package com.example.orientationrace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    }
}