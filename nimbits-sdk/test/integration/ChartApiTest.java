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

package integration;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/3/11
 * Time: 11:05 AM
 */
public class ChartApiTest {


    @Test
    public void testChartApi() throws InterruptedException, IOException, NimbitsException {
        EntityName name = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        Point point = ClientHelper.createSeedPoint(name);
        assertNotNull(point);
        Point result = ClientHelper.client().getPoint(name);
        assertNotNull(result);
        String params = "points=" + name +
                "&cht=lc&chs=200x200" +
                "&chxt=y&autoscale=true" +
                "&chco=000000,00FF00,0000FF,FF0000,FF0066,FFCC33,663333,003333" +
                "&chtt=" + "TEST" +
                "&chdl=" + name;

        byte[] bytes = ClientHelper.client().getChartImage(ClientHelper.url, params);
        assertTrue(bytes.length > 0);


        ClientHelper.client().deletePoint(name);

        point = ClientHelper.client().getPoint(name);
        assertNull(point);

    }
}
