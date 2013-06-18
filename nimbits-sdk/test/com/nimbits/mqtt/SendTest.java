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
import com.nimbits.client.model.common.impl.CommonFactory;
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
        String pointName = "demo";
        String account = "tester@nimbits.com";
        String key = "demokey";
        String appid = "nimbits-02";



        EntityName name = CommonFactory.createName(pointName, EntityType.point);
        EmailAddress emailAddress = CommonFactory.createEmailAddress(account);

        for (int i = 0; i < 100; i++) {
            Value v = ValueFactory.createValueModel(r.nextDouble());
            Send.publish(appid,v,name,emailAddress, key);
        }


    }
}
