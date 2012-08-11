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

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import org.junit.Test;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/11/12
 * Time: 10:43 AM
 */
public class SendTest {
    @Test
    public void testPublish() throws Exception {

        Random r = new Random();
        Value v = ValueFactory.createValueModel(r.nextDouble());
        EntityName name = CommonFactoryLocator.getInstance().createName("mqtt", EntityType.point);
        EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress( "bsautner@gmail.com");
        Send.publish("nimbits1",v,name,emailAddress, "mqttkey" );
    }
}
