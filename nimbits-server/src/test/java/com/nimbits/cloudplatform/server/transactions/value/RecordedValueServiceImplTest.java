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

package com.nimbits.cloudplatform.server.transactions.value;


import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.service.ValueService;
import junit.framework.Assert;
import org.junit.Test;

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
    private ValueService valueService = ValueServiceFactory.getInstance();

 
    @Test
    public void ignoreByCompressionTest() throws Exception {
        point.setFilterValue(VALUE);
        EntityServiceFactory.getInstance().addUpdateSingleEntity(point);


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
        EntityServiceFactory.getInstance().addUpdateSingleEntity(point);
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
        point.setFilterValue(0.01);
        EntityServiceFactory.getInstance().addUpdateSingleEntity(point);


        Value value = ValueFactory.createValueModel(0.01);
        Value value2 = ValueFactory.createValueModel(0.02);
        Value value3 = ValueFactory.createValueModel(0.03);

        valueService.recordValue(user, point, value);
        assertTrue(valueService.ignoreByFilter(point, value2));
        Thread.sleep(10);
        valueService.recordValue(user, point, value2);
        Thread.sleep(10);
        valueService.recordValue(user, point, value3);
        Thread.sleep(10);
        assertTrue(valueService.ignoreByFilter(point, value3));
        List<Value> series = valueService.getTopDataSeries(point, 100);
        assertEquals(2, series.size());






    }
    @Test
    public void ignoreByCompressionTest3() throws Exception {
        point.setFilterValue(0.001);
        EntityServiceFactory.getInstance().addUpdateSingleEntity(point);


        Value value = ValueFactory.createValueModel(0.001);
        Value value2 = ValueFactory.createValueModel(0.002);
        Value value3 = ValueFactory.createValueModel(0.003);

        valueService.recordValue(user, point, value);
        Thread.sleep(10);
        valueService.recordValue(user, point, value2);
        Thread.sleep(10);
        valueService.recordValue(user, point, value3);
        List<Value> series = valueService.getTopDataSeries(point, 100);
        assertEquals(2, series.size());

        assertTrue(valueService.ignoreByFilter(point, value2));
        assertTrue(valueService.ignoreByFilter(point, value3));
        assertFalse(valueService.ignoreByFilter(point, value));


    }
    @Test
    public void testDelta() throws Exception {




        point.setDeltaAlarm(0.01);
        point.setDeltaAlarmOn(true);

        EntityServiceFactory.getInstance().addUpdateEntity(user, point);


        double b = valueService.calculateDelta(point);
        Assert.assertEquals(b, 0.0, 0.0001);


        List<Value> vx = valueService.getTopDataSeries(point, 10);


        double b2 = valueService.calculateDelta(point);
        Assert.assertEquals(0.01, b2, 0.0001);
//        for (int i = 0; i < 10; i++) {
//            List<Value> sample = valueServiceImpl.getCurrentValue(accountBalance);
//            assertFalse(sample.isEmpty());
//            Value balance = sample.get(0);
//            assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
//
//        }


    }
}
