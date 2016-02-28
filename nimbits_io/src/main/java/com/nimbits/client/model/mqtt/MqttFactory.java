/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.model.mqtt;

import com.nimbits.client.model.email.EmailAddress;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/10/12
 * Time: 3:49 PM
 */
public class MqttFactory {

    public static Mqtt createMqtt(final String host, final EmailAddress email, final String uuid, final String message) {
        String appId = host.replace(".appspot.com", "");
        return new MqttModel(appId, email, uuid, message);

    }

}
