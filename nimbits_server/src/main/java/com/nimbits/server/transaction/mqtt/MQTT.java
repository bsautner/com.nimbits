package com.nimbits.server.transaction.mqtt;

import com.google.gson.Gson;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

@Component
public class MQTT {


    private MqttClient myClient;
    private MqttConnectOptions connOpt;

    @org.springframework.beans.factory.annotation.Value("${mqtt.broker.url}")
    private String BROKER_URL;
    @org.springframework.beans.factory.annotation.Value("${mqtt.m2m.domain}")
    private String M2MIO_DOMAIN;
    @org.springframework.beans.factory.annotation.Value("${mqtt.m2m.root}")
    private String M2MIO_STUFF = "things";



    public void publish(User user, Point point, Value value) throws MqttException {

        String clientID = point.getId();

        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        connOpt.setUserName(user.getEmail().getValue());
        connOpt.setPassword(user.getPassword().toCharArray());

        // Connect to Broker
        try {
            myClient = new MqttClient(BROKER_URL, clientID);

            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        String myTopic = M2MIO_DOMAIN + "/" + M2MIO_STUFF + "/" + clientID;
        MqttTopic topic = myClient.getTopic(myTopic);

        Gson gson = GsonFactory.getInstance(true);
        String pubMsg = gson.toJson(value);
        int pubQoS = 1;
        MqttMessage message = new MqttMessage(pubMsg.getBytes());
        message.setQos(pubQoS);
        message.setRetained(false);
        MqttDeliveryToken token = topic.publish(message);
        // Wait until the message has been delivered to the broker
        token.waitForCompletion();
        myClient.disconnect();


    }

}

