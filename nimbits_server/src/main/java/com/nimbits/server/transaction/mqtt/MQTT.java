package com.nimbits.server.transaction.mqtt;

import com.google.gson.Gson;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MQTT {


    //    private MqttClient myClient;
//    private MqttConnectOptions connOpt;
//
    @org.springframework.beans.factory.annotation.Value("${mqtt.broker.url}")
    private String BROKER_URL;
    @org.springframework.beans.factory.annotation.Value("${mqtt.m2m.domain}")
    private String M2MIO_DOMAIN;
    @org.springframework.beans.factory.annotation.Value("${mqtt.m2m.root}")
    private String M2MIO_STUFF;


    private MqttClient myClient;
    private MqttConnectOptions connOpt;


    public void publish(User user, Point point, Value value) throws MqttException {

        String clientID = "com.nimbits.server." + UUID.randomUUID().toString();///point.getId();
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);

        myClient = new MqttClient(BROKER_URL, clientID);

        myClient.connect(connOpt);
        String myTopic = M2MIO_DOMAIN + "/" + user.getEmail().getValue() + "/" + point.getId();
        MqttTopic topic = myClient.getTopic(myTopic);

        Gson gson = GsonFactory.getInstance(true);


        String pubMsg = gson.toJson(value);
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



