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
import com.nimbits.cloudplatform.client.service.value.ValueService;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import junit.framework.Assert;
import org.junit.Test;

import javax.annotation.Resource;
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


    @Resource(name = "valueService")
    ValueService valueService;

    @Test
    public void ignoreByCompressionTest() throws Exception {
        point.setFilterValue(VALUE);
        EntityServiceImpl.addUpdateSingleEntity(point);


        Value value = ValueFactory.createValueModel(D);
        Value value2 = ValueFactory.createValueModel(D);
        Value value3 = ValueFactory.createValueModel(D1);

        ValueTransaction.recordValue(user, point, value);


        assertTrue(ValueTransaction.ignoreByFilter(point, value2));
        assertFalse(ValueTransaction.ignoreByFilter(point, value3));

        point.setFilterValue(10);
        point.setFilterType(FilterType.ceiling);

        assertFalse(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(D1)));
        assertTrue(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.floor);
        assertTrue(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(D1)));
        assertFalse(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.none);
        assertFalse(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.percentageHysteresis);
        EntityServiceImpl.addUpdateSingleEntity(point);
        //  pointService.updatePoint(point);
        ValueTransaction.recordValue(user, point, ValueFactory.createValueModel(100));
        Thread.sleep(10);
        assertTrue(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(105)));
        assertTrue(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(95)));
        assertFalse(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(111)));
        assertFalse(ValueTransaction.ignoreByFilter(point, ValueFactory.createValueModel(80)));

    }

    @Test
    public void ignoreByCompressionTest2() throws Exception {
        point.setFilterValue(0.01);
        EntityServiceImpl.addUpdateSingleEntity(point);


        Value value = ValueFactory.createValueModel(0.01);
        Value value2 = ValueFactory.createValueModel(0.02);
        Value value3 = ValueFactory.createValueModel(0.03);

        ValueTransaction.recordValue(user, point, value);
        assertTrue(ValueTransaction.ignoreByFilter(point, value2));
        Thread.sleep(10);
        ValueTransaction.recordValue(user, point, value2);
        Thread.sleep(10);
        ValueTransaction.recordValue(user, point, value3);
        Thread.sleep(10);
        assertTrue(ValueTransaction.ignoreByFilter(point, value3));
        List<Value> series = ValueTransaction.getTopDataSeries(point, 100);
        assertEquals(2, series.size());






    }
    @Test
    public void ignoreByCompressionTest3() throws Exception {
        point.setFilterValue(0.001);
        EntityServiceImpl.addUpdateSingleEntity(point);


        Value value = ValueFactory.createValueModel(0.001);
        Value value2 = ValueFactory.createValueModel(0.002);
        Value value3 = ValueFactory.createValueModel(0.003);

        ValueTransaction.recordValue(user, point, value);
        Thread.sleep(10);
        ValueTransaction.recordValue(user, point, value2);
        Thread.sleep(10);
        ValueTransaction.recordValue(user, point, value3);
        List<Value> series = ValueTransaction.getTopDataSeries(point, 100);
        assertEquals(2, series.size());

        assertTrue(ValueTransaction.ignoreByFilter(point, value2));
        assertTrue(ValueTransaction.ignoreByFilter(point, value3));
        assertFalse(ValueTransaction.ignoreByFilter(point, value));


    }
    @Test
    public void testDelta() throws Exception {




        point.setDeltaAlarm(0.01);
        point.setDeltaAlarmOn(true);

        EntityServiceImpl.addUpdateEntity(user, point);


        double b = ValueTransaction.calculateDelta(point);
        Assert.assertEquals(b, 0.0, 0.0001);


        List<Value> vx = ValueTransaction.getTopDataSeries(point, 10);


        double b2 = ValueTransaction.calculateDelta(point);
        Assert.assertEquals(0.01, b2, 0.0001);
//        for (int i = 0; i < 10; i++) {
//            List<Value> sample = ValueServiceImpl.getCurrentValue(accountBalance);
//            assertFalse(sample.isEmpty());
//            Value balance = sample.get(0);
//            assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
//
//        }


    }
}
