package com.example.orientationrace;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
        bEnterRace.setOnClickListener(v -> {
            if (usernameText.getText().toString().trim().isEmpty()) {
                // Show Pop up Window to enter username
                showPopup(v);
            }
            else {
                // Create an Intent to start the waiting room activity
                Intent intent = new Intent(MainActivity.this, WaitingRoomActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to show a Popup window requesting the user to type in a username if he did not do it before clicking the Enter Race Button
    public void showPopup(View view) {
        // Create a Dialog object
        Dialog popupDialog = new Dialog(this);

        // Set the content view to the layout created for the popup
        popupDialog.setContentView(R.layout.missing_username_popup_layout);

        // Get reference to the "Close" button in the popup layout and add onClick Listener
        Button bCloseButton = popupDialog.findViewById(R.id.buttonClose);
        bCloseButton.setOnClickListener(v -> {
            // Close popup when the button is clicked
            popupDialog.dismiss();
        });

        // Show the popup
        popupDialog.show();
    }
}