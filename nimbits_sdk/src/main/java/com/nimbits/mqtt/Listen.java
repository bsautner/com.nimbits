
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


import com.nimbits.cloudplatform.client.NimbitsClient;
import com.nimbits.cloudplatform.client.NimbitsClientFactory;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.exception.NimbitsException;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.mqtt.MqttHelper;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.user.NimbitsUser;
import com.nimbits.user.UserFactory;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;


public class Listen implements MqttCallback {
    private MqttClient client;

    private MqttConnectOptions conOpt;
    private String topic;
    private int qos = 2;

    public Listen( String clientId ) throws NimbitsException {


        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = null;
        try {
            dataStore = new MqttDefaultFilePersistence(tmpDir);
        } catch (MqttPersistenceException e) {
            throw  new NimbitsException(e);
        }

        try {
            // Construct the object that contains connection parameters
            // such as cleansession and LWAT
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(false);

            // Construct the MqttClient instance
            client = new MqttClient(MqttHelper.url, clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
            log("Unable to set up client: "+e.toString());
            System.exit(1);
        }
    }


    public void subscribe(String topicName) throws NimbitsException {
         topic = topicName;
        // Connect to the server
        try {
            client.connect();

            log("Connected to "+MqttHelper.url);

            // Subscribe to the topic
            log("Subscribing to topic \""+topicName+"\" qos "+qos);
            client.subscribe(topicName, qos);

            // Block until Enter is pressed
            log("Press <Enter> to exit");
            try {
                System.in.read();
            } catch (IOException e) {
                //If we can't read we'll just exit
            }

            // Disconnect the client
            client.disconnect();
            log("Disconnected");
        } catch (MqttException e) {
            throw new NimbitsException(e);
        }
    }

    /**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     * @param message the message to log
     */
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
        log("Connection to " + MqttHelper.url + " lost!");
        try {
            subscribe(topic);
        } catch (NimbitsException e) {
            e.printStackTrace();
        }
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

    /**
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(org.eclipse.paho.client.mqttv3.MqttTopic, org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    public void messageArrived(MqttTopic topic, MqttMessage message) throws MqttException, NimbitsException {
        // Called when a message arrives from the server.

        System.out.println("Time:\t" +System.currentTimeMillis() +
                "  Topic:\t" + topic.getName() +
                "  Message:\t" + new String(message.getPayload()) +
                "  QoS:\t" + message.getQos());

        String a[] = topic.getName().split("/");
        if (a.length == 4) {
            EmailAddress email = CommonFactory.createEmailAddress(a[1]);
            String accessKey = a[3];
            String id = a[2];
            String appId = a[0];
            NimbitsUser user = UserFactory.createNimbitsUser(email, accessKey);
            NimbitsClient client = NimbitsClientFactory.getInstance(user, "http://" + appId + ".appspot.com");
            if (client.isLoggedIn()) {
                String json = new String(message.getPayload());
                Value v = GsonFactory.getInstance().fromJson(json, ValueModel.class);
                EntityName name = CommonFactory.createName(id, EntityType.point);
                Value vx = client.recordValue(name, v);
                if (vx == null) {
                    System.out.println("Value received but not recorded");
                }
                else {
                    System.out.println("Success");
                }

            }
        }



    }



}