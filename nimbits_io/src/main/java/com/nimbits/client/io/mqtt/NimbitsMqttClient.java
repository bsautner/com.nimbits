package com.nimbits.client.io.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class NimbitsMqttClient implements MqttCallback {

    MqttClient myClient;
    MqttConnectOptions connOpt;

    static final String BROKER_URL = "tcp://54.205.203.60:1883";
    static final String M2MIO_DOMAIN = "com.nimbits";
    static final String M2MIO_STUFF = "things";
    static final String M2MIO_THING = "124A-AAA";


    // the following two flags control whether this example is a publisher, a subscriber or both
    static final Boolean subscriber = true;

    /**
     *
     * connectionLost
     * This callback is invoked upon losing the MQTT connection.
     *
     */
    @Override
    public void connectionLost(Throwable t) {
        System.out.println("Connection lost!");
        t.printStackTrace();
        // code to reconnect to the broker would go here if desired
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("-------------------------------------------------");
        System.out.println("| Topic:" + s);
        System.out.println("| Message: " + new String(mqttMessage.getPayload()));
        System.out.println("-------------------------------------------------");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }




    /**
     *
     * MAIN
     *
     */
    public static void main(String[] args) {
        NimbitsMqttClient smc = new NimbitsMqttClient();
        smc.runClient();
    }

    /**
     *
     * runClient
     * The main functionality of this simple example.
     * Create a MQTT client, connect to broker, pub/sub, disconnect.
     *
     */
    public void runClient() {
        // setup MQTT Client
        String clientID = M2MIO_THING;
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(300);

        // Connect to Broker
        try {
            myClient = new MqttClient(BROKER_URL, clientID);
            myClient.setCallback(this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Connected to " + BROKER_URL);

        // setup topic
        // topics on m2m.io are in the form <domain>/<stuff>/<thing>
        String myTopic = M2MIO_DOMAIN + "/" + M2MIO_STUFF + "/" + M2MIO_THING;
        System.out.println(myTopic);
      //  MqttTopic topic = myClient.getTopic(myTopic);

        // subscribe to topic if subscriber
        if (subscriber) {
            try {
                int subQoS = 0;
                myClient.subscribe(myTopic, subQoS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//
//        // disconnect
//        try {
//            // wait to ensure subscribed messages are delivered
//            if (subscriber) {
//                Thread.sleep(60000);
//            }
//            myClient.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}