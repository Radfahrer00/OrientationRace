package com.example.orientationrace.model;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttManager {
    private static MqttManager instance;
    private MqttClient mqttClient;
    private static final String BROKER_URI = "tcp://91.121.93.94:1883";
    public static final String MQTTCONNECTION = "MQTT_connection";
    String clientId;

    private MqttManager() {
        // Private constructor to prevent instantiation outside of this class.
    }

    public static synchronized MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }
        return instance;
    }


    public void connect(String clientId) {
        try {
            // Connect to the MQTT broker and set up necessary configurations.
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(BROKER_URI, clientId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            mqttClient.connect(options);
        } catch (MqttException e) {
            Log.e(MQTTCONNECTION, "Error connecting to MQTT broker", e);
            e.printStackTrace();
        }
    }

    public void disconnect() throws MqttException {
        // Disconnect from the MQTT broker.
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
    }

    public void publishMessage(String topic, String message) throws MqttException {
        // Publish a message to the specified MQTT topic.
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }

    public void subscribeToTopic(String topic, MqttCallback callback) throws MqttException {
        // Subscribe to the specified MQTT topic.
        mqttClient.setCallback(callback);
        mqttClient.subscribe(topic);
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}