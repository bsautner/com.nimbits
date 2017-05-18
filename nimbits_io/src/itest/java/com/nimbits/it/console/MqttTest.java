package com.nimbits.it.console;

import com.nimbits.client.io.mqtt.MqttPublisher;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;

import java.util.UUID;

public class MqttTest {

    
        public static void main(String[] args) throws Exception {

            MqttPublisher publisher = MqttPublisher.create(UUID.randomUUID().toString(), "mqtt.nimbits.com");
            publisher.publish(new PointModel.Builder().id("FOO").create(), new Value.Builder().data("hello").create());
            System.out.println("done");

        }

}
