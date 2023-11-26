package com.example.orientationrace.model;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Singleton class for managing MQTT (Message Queuing Telemetry Transport) communication.
 * This class provides methods for connecting to an MQTT broker, disconnecting, publishing messages,
 * and subscribing to topics.
 */
public class MqttManager {
    private static MqttManager instance;
    public MqttClient mqttClient;
    private static final String BROKER_URI = "tcp://91.121.93.94:1883";
    public static final String MQTTCONNECTION = "MQTT_connection";
    String clientId;

    /**
     * Private constructor to prevent instantiation outside of this class.
     */
    private MqttManager() {
    }

    /**
     * Gets the singleton instance of MqttManager.
     *
     * @return The MqttManager instance.
     */
    public static synchronized MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }
        return instance;
    }


    /**
     * Connects to the MQTT broker with the specified client ID.
     *
     * @param clientId The client ID to use for the MQTT connection.
     */
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

    /**
     * Disconnects from the MQTT broker.
     *
     * @throws MqttException If an error occurs during disconnection.
     */
    public void disconnect() throws MqttException {
        // Disconnect from the MQTT broker.
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
    }

    /**
     * Publishes a message to the specified MQTT topic.
     *
     * @param topic   The MQTT topic to publish the message to.
     * @param message The message to be published.
     * @throws MqttException If an error occurs during message publishing.
     */
    public void publishMessage(String topic, String message) throws MqttException {
        // Publish a message to the specified MQTT topic.
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }

    /**
     * Subscribes to the specified MQTT topic with the provided callback for handling incoming messages.
     *
     * @param topic    The MQTT topic to subscribe to.
     * @param callback The callback to handle incoming messages.
     * @throws MqttException If an error occurs during subscription.
     */
    public void subscribeToTopic(String topic, MqttCallback callback) throws MqttException {
        // Subscribe to the specified MQTT topic.
        mqttClient.setCallback(callback);
        mqttClient.subscribe(topic);
    }

    /**
     * Sets the client ID for the MQTT connection.
     *
     * @param clientId The client ID to set.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the client ID used for the MQTT connection.
     *
     * @return The client ID.
     */
    public String getClientId() {
        return clientId;
    }
}