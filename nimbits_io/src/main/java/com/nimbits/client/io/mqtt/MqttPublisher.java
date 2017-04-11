package com.nimbits.client.io.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttPublisher {


    private MqttClient myClient;
    private MqttConnectOptions connOpt;

    private static final String BROKER_URL = "tcp://54.205.203.60:1883";
    private static final String M2MIO_DOMAIN = "com.nimbits";
    private static final String M2MIO_STUFF = "things";
    private static final String M2MIO_THING = "124A-AAA";

    public static void main(String[] args) throws MqttException {
        MqttPublisher smc = new MqttPublisher();
        smc.runClient();
    }

    private void runClient() throws MqttException {
        // setup MQTT Client
        String clientID = "server";
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);

        // Connect to Broker

            myClient = new MqttClient(BROKER_URL, clientID);

            myClient.connect(connOpt);


        System.out.println("Connected to " + BROKER_URL);

        // setup topic
        // topics on m2m.io are in the form <domain>/<stuff>/<thing>
        String myTopic = M2MIO_DOMAIN + "/" + M2MIO_STUFF + "/" + M2MIO_THING;
        MqttTopic topic = myClient.getTopic(myTopic);


            for (int i=1; i<=10; i++) {
                String pubMsg = "{\"pubmsg\":" + i + "}";
                int pubQoS = 0;
                MqttMessage message = new MqttMessage(pubMsg.getBytes());
                message.setQos(pubQoS);
                message.setRetained(false);

                // Publish the message
                System.out.println("Publishing to topic \"" + topic + "\" qos " + pubQoS);
                MqttDeliveryToken token = null;
                try {
                    // publish message to broker
                    token = topic.publish(message);
                    // Wait until the message has been delivered to the broker
                    token.waitForCompletion();
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


}