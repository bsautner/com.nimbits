/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.service.mqtt.impl;


import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.mqtt.Mqtt;
import com.nimbits.client.model.mqtt.MqttHelper;
import com.nimbits.server.service.mqtt.MqttService;
import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/8/12
 * Time: 4:54 PM
 */
public class MqttServiceImpl implements MqttService {
    private MqttClient client;

    private MqttConnectOptions conOpt;
    private String clientID = UUID.randomUUID().toString();
    private Logger log = Logger.getLogger(MqttServiceImpl.class.getName());
    @Override
    public void receiveMessage(Mqtt mqtt) throws NimbitsException {

        String topic =  mqtt.getHost() + "/" + mqtt.getUuid();

        log.info("SENDING TOPIC: " + topic);
        int qos = 2;

        try {
            // Create an instance of the Broker client wrapper
            Broker sampleClient = new Broker(MqttHelper.url,clientID);
            sampleClient.publish(topic,qos,mqtt.getMessage().getBytes());

        } catch(MqttException me) {
            me.printStackTrace();
        }
    }


    // Private instance variables



    class Broker
    {
        String brokerUrl;

        Broker(String brokerUrl, String clientId) throws MqttException{
            this.brokerUrl = brokerUrl;

            String tmpDir = System.getProperty("java.io.tmpdir");
            MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(false);

            client = new MqttClient(this.brokerUrl,clientId, dataStore);


        }






    /**
     * Performs a single publish
     * @param topicName the topic to publish to
     * @param qos the qos to publish at
     * @param payload the payload of the message to publish
     * @throws MqttException
     */
    public void publish(String topicName, int qos, byte[] payload) throws MqttException {

        // Connect to the server
        client.connect();


        // Get an instance of the topic
        MqttTopic topic = client.getTopic(topicName);

        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Publish the message
        MqttDeliveryToken token = topic.publish(message);

        // Wait until the message has been delivered to the server
        token.waitForCompletion();

        // Disconnect the client
        client.disconnect();

    }





    }


}

