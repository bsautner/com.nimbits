package com.nimbits.it.console;

import org.eclipse.paho.client.mqttv3.*;

public class SimpleMqttPublisher   {

    private MqttClient myClient;
    private MqttConnectOptions connOpt;

    private static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
    private static final String M2MIO_DOMAIN = "com.nimbits";
    private static final String M2MIO_STUFF = "things";
    private static final String M2MIO_THING = "1234AAAA";
    private static final String M2MIO_USERNAME = "ben";
    private static final String M2MIO_PASSWORD_MD5 = "password";

    public static void main(String[] args) {
        SimpleMqttPublisher smc = new SimpleMqttPublisher();
        smc.runClient();
    }

    private void runClient() {
        // setup MQTT Client
        String clientID = M2MIO_THING;
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        connOpt.setUserName(M2MIO_USERNAME);
        connOpt.setPassword(M2MIO_PASSWORD_MD5.toCharArray());

        // Connect to Broker
        try {
            myClient = new MqttClient(BROKER_URL, clientID);

            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Connected to " + BROKER_URL);

        // setup topic
        // topics on m2m.io are in the form <domain>/<stuff>/<thing>
        String myTopic = M2MIO_DOMAIN + "/" + M2MIO_STUFF + "/" + M2MIO_THING;
        MqttTopic topic = myClient.getTopic(myTopic);


            for (int i=1; i<=10; i++) {
                String pubMsg = "{\"pubmsg\":" + i + "}";
                int pubQoS = 1;
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