package com.example.orientationrace.model;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import junit.framework.TestCase;

public class MqttManagerTest extends TestCase {

    public void testConnectAndDisconnect() {
        MqttManager mqttManager = MqttManager.getInstance();
        String clientId = "testClientId";

        // Test connection
        mqttManager.connect(clientId);
        assertTrue("MqttClient should be connected", mqttManager.mqttClient.isConnected());

        // Test disconnection
        try {
            mqttManager.disconnect();
        } catch (MqttException e) {
            fail("Exception should not be thrown during disconnection");
        }
        assertFalse("MqttClient should be disconnected", mqttManager.mqttClient.isConnected());
    }

    public void testPublishMessage() {
        MqttManager mqttManager = MqttManager.getInstance();
        String clientId = "testClientId";
        String topic = "testTopic";
        String message = "testMessage";

        // Test publishing a message
        mqttManager.connect(clientId);
        try {
            mqttManager.publishMessage(topic, message);
        } catch (MqttException e) {
            fail("Exception should not be thrown during message publishing");
        }

    }
}
