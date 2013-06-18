
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

package com.nimbits.mqtt;


import com.nimbits.cloudplatform.client.exception.NimbitsException;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.mqtt.MqttHelper;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import org.eclipse.paho.client.mqttv3.*;


/**
 * This sample application demonstrates basic usage
 * of the MQTT v3 Client api.
 *
 * It can be run in one of two modes:
 *  - as a publisher, sending a single message to a topic on the server
 *  - as a subscriber, listening for messages from the server
 *
 */
public class Send implements MqttCallback {


    /**
     * The main entry point of the sample.
     *
     * This method handles parsing the arguments specified on the
     * command-line before performing the specified action.
     */
    public static void publish(String appId, Value value, EntityName name, EmailAddress email, String accessKey) throws NimbitsException {

        String message = GsonFactory.getInstance().toJson(value);
        int qos = 2;
        String topic = appId + "/" + email.getValue()  + "/" + name.getValue() + "/" + accessKey;



        try {
            // Create an instance of the Sample client wrapper
            Send sampleClient = new Send(MqttHelper.url,email.getValue());
            sampleClient.publish(topic,qos,message.getBytes());

        } catch(MqttException me) {
            me.printStackTrace();
        }
    }

    // Private instance variables
    private MqttClient client;
    private String brokerUrl;
    private boolean quietMode;
    private MqttConnectOptions conOpt;


    public Send(String brokerUrl, String clientId) throws MqttException {
        this.brokerUrl = brokerUrl;
        this.quietMode = quietMode;

        //This sample stores files in a temporary directory...
        //..a real application ought to store them somewhere
        //where they are not likely to get deleted or tampered with
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            // Construct the object that contains connection parameters
            // such as cleansession and LWAT
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(false);

            // Construct the MqttClient instance
            client = new MqttClient(this.brokerUrl,clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
            log("Unable to set up client: "+e.toString());
            System.exit(1);
        }
    }

    /**
     * Performs a single publish
     * @param topicName the topic to publish to
     * @param qos the qos to publish at
     * @param payload the payload of the message to publish
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public void publish(String topicName, int qos, byte[] payload) throws MqttException {

        // Connect to the server
        client.connect();
        log("Connected to "+brokerUrl);

        // Get an instance of the topic
        MqttTopic topic = client.getTopic(topicName);

        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Publish the message
        log("Publishing at: "+System.currentTimeMillis()+ " to topic \""+topicName+"\" qos "+qos);
        MqttDeliveryToken token = topic.publish(message);

        // Wait until the message has been delivered to the server
        token.waitForCompletion();

        // Disconnect the client
        client.disconnect();
        log("Disconnected");
    }


    private void log(String message) {

            System.out.println(message);

    }



    /****************************************************************/
    /* Methods to implement the MqttCallback interface              */
    /****************************************************************/

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        // An application may choose to implement reconnection
        // logic at this point.
        // This sample simply exits.
        log("Connection to " + brokerUrl + " lost!");
        System.exit(1);
    }

    @Override
    public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.MqttDeliveryToken)
     */
    public void deliveryComplete(MqttDeliveryToken token) {
        // Called when a message has completed delivery to the
        // server. The token passed in here is the same one
        // that was returned in the original call to publish.
        // This allows applications to perform asychronous
        // delivery without blocking until delivery completes.

        // This sample demonstrates synchronous delivery, by
        // using the token.waitForCompletion() call in the main thread.
    }



}