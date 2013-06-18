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

package com.nimbits.cloudplatform.client.model.mqtt;

import com.nimbits.cloudplatform.client.model.email.EmailAddress;

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
