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

package com.nimbits.client;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.user.NimbitsUser;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 7/27/12
 * Time: 4:57 PM
 *
 */
public class PowerLoaderTest {

    @Test
    public void loadData() throws NimbitsException, InterruptedException {

        NimbitsClient client;

        EntityName pointName;

        String email =  "tester@nimbits.com";
        String password = "key";
        String url ="http://nimbits-hrd1.appspot.com";
        String p = "app demo";
        EmailAddress em = CommonFactoryLocator.getInstance().createEmailAddress(email);
        NimbitsUser g = new NimbitsUser(em, password);
        pointName = CommonFactoryLocator.getInstance().createName(p, EntityType.point);
        client = NimbitsClientFactory.getInstance(g, url);

        Random r = new Random();

        for (int i = 0; i < 10; i++ ) {
            client.recordValue(pointName,r.nextDouble() * 100);
            Thread.sleep(10);
            System.out.println("Recorded value " + i);

        }



    }



}
