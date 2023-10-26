package com.example.orientationrace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button bEnterRace;
    EditText usernameText;
    boolean usernameTyped;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // No username is typed when creating the activity
        usernameTyped = false;

        // Get references to UI elements
        bEnterRace = findViewById(R.id.buttonEnterRace);
        usernameText = findViewById(R.id.usernameInput);

        // Listener for the Enter Race Button
        bEnterRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameText.getText().toString().trim().isEmpty()) {
                    // Show Pop up Window to enter username
                }
                else {
                    // Create an Intent to start the waiting room activity
                    Intent intent = new Intent(MainActivity.this, WaitingRoomActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}