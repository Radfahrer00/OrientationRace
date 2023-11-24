package com.example.orientationrace.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orientationrace.R;

/**
 * MainActivity serves as the entry point for the application where users input their username
 * to enter the race. It then leads the user to ParticipantsActivity.
 */
public class MainActivity extends AppCompatActivity {

    // UI Elements
    Button bEnterRace;
    EditText usernameText;
    TextView usernameLabel;

    /**
     * Called when the activity is first created. This method initializes the activity,
     * sets the content view, and configures UI elements. It also adds a TextWatcher to the
     * usernameText to dynamically show/hide the usernameLabel based on user input. The
     * method sets a click listener for the Enter Race Button, and when clicked, it checks
     * if a username is entered. If not, it shows a popup requesting the user to enter a
     * username; otherwise, it starts the ParticipantsActivity with the entered username.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state,
     *                           or null if there was no saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to UI elements
        bEnterRace = findViewById(R.id.buttonEnterRace);
        usernameText = findViewById(R.id.usernameInput);
        usernameLabel = findViewById(R.id.usernameLabel);

        // Add a TextWatcher to the usernameText to dynamically show/hide the label
        usernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the EditText has text, if yes, set the label visible; otherwise, invisible
                usernameLabel.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Listener for the Enter Race Button
        bEnterRace.setOnClickListener(v -> {
            if (usernameText.getText().toString().trim().isEmpty()) {
                // Show Pop up Window to enter username
                showPopup(v);
            } else {
                // Create an Intent to start the Participants activity
                Intent intent = new Intent(MainActivity.this, ParticipantsActivity.class);
                // Send the username to the next activity
                intent.putExtra("username", usernameText.getText().toString());
                startActivity(intent);
            }
        });
    }

    /**
     * Method to show a Popup window requesting the user to type in a username
     * if they did not do it before clicking the Enter Race Button.
     *
     * @param view The current view to which the popup is associated.
     */
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