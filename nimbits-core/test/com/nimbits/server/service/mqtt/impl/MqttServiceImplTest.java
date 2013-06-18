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

import com.nimbits.server.service.mqtt.MqttService;
import com.nimbits.server.service.search.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/8/12
 * Time: 5:11 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-daos.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class MqttServiceImplTest {
    @Resource(name="mqttService")
    private MqttService service;


    @Test
    public void testReceiveMessage() throws Exception {

           //service.receiveMessage("hello world");
    }
}
