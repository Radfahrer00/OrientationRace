package com.example.orientationrace;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttHandler {

    private MqttClient client;
    private MqttAndroidClient mqttAndroidClient;
    private static final String BROKER_URI = "tcp://192.168.56.1:1883";

    public MqttHandler(Context context, String clientId) {
        mqttAndroidClient = new MqttAndroidClient(context, BROKER_URI, clientId);
        connect();
    }

    private void connect() {
        try {
            IMqttToken token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Connection successful
                    //subscribeToTopic("participants"); // Subscribe to the topic for participants
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Handle connection failure
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}