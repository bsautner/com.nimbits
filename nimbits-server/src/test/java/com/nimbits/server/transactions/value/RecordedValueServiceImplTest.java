/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.value;


import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/12
 * Time: 8:51 AM
 */
public class RecordedValueServiceImplTest extends NimbitsServletTest {


    private static final double D = 1.23;
    private static final double D1 = 2.23;
    private static final int D2 = 11;
    private static final double VALUE = 0.1;


 
    @Test
    public void ignoreByCompressionTest() throws Exception {
        point.setFilterValue(VALUE);
        entityService.addUpdateSingleEntity(point);


        Value value = ValueFactory.createValueModel(D);
        Value value2 = ValueFactory.createValueModel(D);
        Value value3 = ValueFactory.createValueModel(D1);

        valueService.recordValue(user, point, value);


        assertTrue(valueService.ignoreByFilter(point, value2));
        assertFalse(valueService.ignoreByFilter(point, value3));

        point.setFilterValue(10);
        point.setFilterType(FilterType.ceiling);

        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D1)));
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.floor);
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D1)));
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.none);
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.percentageHysteresis);
        entityService.addUpdateSingleEntity(point);
        //  pointService.updatePoint(point);
        valueService.recordValue(user, point, ValueFactory.createValueModel(100));
        Thread.sleep(10);
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(105)));
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(95)));
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(111)));
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(80)));

    }

    @Test
    public void ignoreByCompressionTest2() throws Exception {
        Point randomPoint = createRandomPoint();
        randomPoint.setFilterValue(0.01);
        entityService.addUpdateSingleEntity(randomPoint);

        Value value = ValueFactory.createValueModel(0.01);
        Value value2 = ValueFactory.createValueModel(0.02);
        Value value3 = ValueFactory.createValueModel(0.03);

        valueService.recordValue(user, randomPoint, value);
        assertTrue(valueService.ignoreByFilter(randomPoint, value2));
        Thread.sleep(100);
        valueService.recordValue(user, randomPoint, value2);
        Thread.sleep(100);
        valueService.recordValue(user, randomPoint, value3);
        Thread.sleep(100);
        assertTrue(valueService.ignoreByFilter(randomPoint, value3));
        List<Value> series = valueService.getTopDataSeries(randomPoint, 100);
        assertEquals(2, series.size());

    }
    @Test
    public void ignoreByCompressionTest3() throws Exception {

        Point randomPoint = createRandomPoint();
        randomPoint.setFilterValue(0.01);
        randomPoint.setFilterValue(0.001);
        entityService.addUpdateSingleEntity(randomPoint);


        Value value = ValueFactory.createValueModel(0.001);
        Value value2 = ValueFactory.createValueModel(0.002);
        Value value3 = ValueFactory.createValueModel(0.003);

        valueService.recordValue(user, randomPoint, value);
        Thread.sleep(100);
        valueService.recordValue(user, randomPoint, value2);
        Thread.sleep(100);
        valueService.recordValue(user, randomPoint, value3);
        List<Value> series = valueService.getTopDataSeries(randomPoint, 100);
        assertEquals(2, series.size());

        List<Value> h = valueService.getPrevValue(randomPoint, new Date());
        Value last = h.get(0);
        assertEquals(last.getDoubleValue(), value3.getDoubleValue(), .0001);

        assertTrue(valueService.ignoreByFilter(randomPoint, value2));
        assertTrue(valueService.ignoreByFilter(randomPoint, value3));
        assertTrue(valueService.ignoreByFilter(randomPoint, value)); //tries to insert into the last since we use the new value's timestamp


    }
//    @Test
//    public void testDelta() throws Exception {
//
//
//
//
//        point.setDeltaAlarm(0.01);
//        point.setDeltaAlarmOn(true);
//
//        entityService.addUpdateEntity(user, point);
//
//
//        double b = valueService.calculateDelta(point);
//        Assert.assertEquals(b, 0.0, 0.0001);
//
//
//        List<Value> vx = valueService.getTopDataSeries(point, 10);
//
//
//        double b2 = valueService.calculateDelta(point);
//        Assert.assertEquals(0.01, b2, 0.0001);
////        for (int i = 0; i < 10; i++) {
////            List<Value> sample = valueServiceImpl.getCurrentValue(accountBalance);
////            assertFalse(sample.isEmpty());
////            Value balance = sample.get(0);
////            assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
////
////        }
//
//
//    }
}
