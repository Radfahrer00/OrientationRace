package com.example.orientationrace;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MainActivity extends AppCompatActivity {

    // UI Elements
    Button bEnterRace;
    EditText usernameText;
    boolean usernameTyped;

    // MQTT Connection
    final String serverUri = "tcp://192.168.56.1:1883";
    final String subscriptionTopic = "broker/topic";
    String publishTopic = "androidClient/topic";
    String publishMessage;
    MqttAndroidClient mqttAndroidClient;
    String clientId;
    String lastWillMessage;
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
                clientId = usernameText.getText().toString();
                publishTopic = clientId + "/topic";
                lastWillMessage = "Client " + clientId + " disconnected!";
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


    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //addToHistory("Subscribed to: " + subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //addToHistory("Failed to subscribe");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            //addToHistory(e.toString());
        }
    }

    public void publishMessage() {
        MqttMessage message = new MqttMessage();
        message.setPayload(publishMessage.getBytes());
        message.setRetained(false);
        message.setQos(0);
        try {
            mqttAndroidClient.publish(publishTopic, message);
            //addToHistory("Message Published");
        } catch (Exception e) {
            e.printStackTrace();
            //addToHistory(e.toString());
        }
        if (!mqttAndroidClient.isConnected()) {
            //addToHistory("Client not connected!");
        }
    }
}