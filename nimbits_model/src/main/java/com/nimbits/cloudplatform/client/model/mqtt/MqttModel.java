/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.model.mqtt;

import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/10/12
 * Time: 3:25 PM
 */
public class MqttModel  implements Serializable, Mqtt {


    private final String email;
    private final String uuid;
    private final String message;
    private final String host;



    public MqttModel(final String host, final EmailAddress email, final String uuid, final String message) {
        this.email = email.getValue();
        this.uuid = uuid;
        this.message = message;
        this.host = host;
    }


    @Override
    public EmailAddress getEmail()  {
        return CommonFactory.createEmailAddress(email);
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getHost() {
        return host;
    }
}
