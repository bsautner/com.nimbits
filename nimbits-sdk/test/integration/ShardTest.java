package integration;/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/12/11
 * Time: 11:32 AM
 */
public class ShardTest {

    @Test
    public void testShard() throws UnsupportedEncodingException, InterruptedException {

//        EntityName categoryName = CommonFactory.createName(UUID.randomUUID().toString());
//        EntityName pointName = CommonFactory.createName(UUID.randomUUID().toString());
//        ClientHelper.client().addCategory(categoryName);
//        Point point = ClientHelper.client().addPoint(categoryName, pointName);
//        assertNotNull(point);
//        assertTrue(point.getId() > 0);
//        assertTrue(point.getHighestRecordedValue() == 0);
//        Thread.sleep(3000);
//        int count = 100;
//
//
//        for (int run = 0; run < 10; run++) {
//            StringBuilder builder = new StringBuilder();
//            double r = 0.0;
//            for (int i = 0; i < count; i++) {
//
//                builder.append("p").append(i).append("=").append(pointName.getValue()).append("&");
//                builder.append("v").append(i).append("=").append(i).append("&");
//                r += i;
//            }
//
//            String param = builder.toString().substring(0, builder.toString().length() - 1);
//
//
//            ClientHelper.client().recordBatch(param);
//
//
//            Thread.sleep(5000);
//            List<Value> values = ClientHelper.client().getSeries(pointName, count);
//            double d = 0.0;
//
//            for (Value v : values) {
//                d += v.getValue();
//
//            }
//            Point result = ClientHelper.client().getPoint(pointName);
//            assertEquals(Double.valueOf(count - 1), result.getHighestRecordedValue(), 0);
//
//
//            Thread.sleep(2000);
//
//        }
//        ClientHelper.client().deleteCategory(categoryName);


    }

    @Test
    public void testShardCounter() throws IOException, InterruptedException {
//        EntityName categoryName = CommonFactory.createName(UUID.randomUUID().toString());
//        EntityName pointName = CommonFactory.createName(UUID.randomUUID().toString());
//        ClientHelper.client().addCategory(categoryName);
//        Point point = ClientHelper.client().addPoint(categoryName, pointName);
//
//        assertTrue(ClientHelper.client().isLoggedIn());
//        point.setCompression(-1);
//        ClientHelper.client().updatePoint(point);
//
//        assertEquals(point.getCounter(), 0);
//        Random r = new Random();
//        int count = 1000;
//
//        double h = 0.0;
//        double l = 0.0;
//        for (int i = 0; i < count; i++) {
//            double v = r.nextDouble();
//            if (v > h) {
//                h = v;
//            }
//            if (v < l) {
//                l = v;
//            }
//
//            System.out.println(v);
//            ClientHelper.client().recordValue(pointName, v, new Date());
//
//        }
//        Thread.sleep(5000);
//        Point x = ClientHelper.client().getPoint(pointName);
//
//
//        assertEquals(count, x.getCounter());
//        assertEquals(l, x.getLowestRecordedValue(), 0);
//        assertEquals(h, x.getHighestRecordedValue(), 0);
//
//        ClientHelper.client().deleteCategory(categoryName);


    }


}
