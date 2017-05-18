package com.nimbits.client.io.mqtt;

import com.google.gson.Gson;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import org.eclipse.paho.client.mqttv3.*;

public class MqttPublisher {


    private final String session;
    private final MqttClient myClient;


    public MqttPublisher(String session, String host) throws Exception {
        String path = "tcp://" + host + ":1883";

        this.session = session;
        this.myClient = new MqttClient(path, session);

    }


    public static MqttPublisher create(String session, String host) throws Exception {
        return new MqttPublisher(session, host);
    }

    public void publish(Point point, Value value) throws MqttException {

        MqttConnectOptions connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);

        myClient.connect(connOpt);


        // setup topic
        // topics on m2m.io are in the form <domain>/<stuff>/<thing>
        String root = "com.nimbits";
        String myTopic = root + "/" + session + "/" + point.getId();
        System.out.println(myTopic);
        MqttTopic topic = myClient.getTopic(myTopic);


        int pubQoS = 0;
        Gson gson = GsonFactory.getInstance(true);
        MqttMessage message = new MqttMessage(gson.toJson(value).getBytes());
        message.setQos(pubQoS);
        message.setRetained(false);


        MqttDeliveryToken token = topic.publish(message);
        // Wait until the message has been delivered to the broker
        token.waitForCompletion();


    }
}


