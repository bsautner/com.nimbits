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

package com.nimbits.server.transactions.value;

import com.nimbits.client.constants.Const;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transaction.value.cache.ValueCache;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/5/12
 * Time: 1:49 PM
 */

public class ValueMemCacheImplTest extends NimbitsServletTest {

    private static final double D = 1.23;
    private static final double DELTA = 0.001;


    @Test
    public void testBuildID() {

        Date now = new Date();

        long v = pointEntity.hashCode() + now.getTime();
        long r = v - pointEntity.hashCode();
        assertEquals(now.getTime(), r);
        assertNotSame(pointEntity, pointChildEntity);
        assertNotSame(pointEntity.hashCode(), pointChildEntity.hashCode());

    }


    @Test
    public void testGetClosest() {

        List<Value> values = new ArrayList<Value>(100);
        Random r = new Random();

        Calendar c = Calendar.getInstance();
        Date now = new Date();
        int randomSpot = r.nextInt(99);
        for (int x = 0; x < 100; x++) {
            c.add(Calendar.MINUTE, r.nextInt(100) * -1);
            Value value;
            if (x != randomSpot) { //random spot put now
                value = ValueFactory.createValueModel(r.nextDouble(), c.getTime());

            } else {
                value = ValueFactory.createValueModel(r.nextDouble(), now);

            }
            values.add(value);
        }


        List<Value> result = valueService.getClosestMatchToTimestamp(values, now);
        assertFalse(result.isEmpty());
        assertEquals(result.get(0).getTimestamp(), now);

        Value sample = values.get(r.nextInt(99));
        List<Value> result2 = valueService.getClosestMatchToTimestamp(values, new Date(sample.getTimestamp().getTime() + 1000));
        assertFalse(result2.isEmpty());
        assertEquals(sample.getTimestamp(), result2.get(0).getTimestamp());

    }


    @Test
    public void splitListTest() {

        Random random = new Random();
        int size = 1000007;

        double sum = 0.0;

        List<Value> list = new ArrayList<Value>(size);
        for (int i = 0; i < size; i++) {
            double v = random.nextDouble();
            sum += v;
            list.add(ValueFactory.createValueModel(v));

        }
        assertEquals(list.size(), size);
        List<List<Value>> split = valueService.splitUpList(list);
        int expectedSize = list.size() / Const.CONST_QUERY_CHUNK_SIZE;
        if (list.size() % Const.CONST_QUERY_CHUNK_SIZE > 0) {
            expectedSize++;
        }

        assertEquals(expectedSize, split.size());


    }


    @Test
    public void testGetPrevValue() throws InterruptedException, ValueException, IOException {

        Value v = ValueFactory.createValueModel(D);
        valueService.recordValue(user, point, v, false);
        Thread.sleep(1000);
        List<Value> vr = valueService.getRecordedValuePrecedingTimestamp(point, new Date());
        List<Value> dv = valueService.getRecordedValuePrecedingTimestamp(pointChild, new Date());
        assertTrue(dv.isEmpty());


        assertNotNull(vr);
        assertFalse(vr.isEmpty());
        assertEquals(v.getDoubleValue(), vr.get(0).getDoubleValue(), DELTA);
        List<Value> vz = valueService.getBuffer(point);
        assertEquals(1, vz.size());
        valueService.moveValuesFromCacheToStore(point);
        vz = valueService.getBuffer(point);
        assertEquals(0, vz.size());
        dv = valueService.getRecordedValuePrecedingTimestamp(point, new Date());
        assertEquals(v.getDoubleValue(), dv.get(0).getDoubleValue(), DELTA);

    }

    @Test
    public void testAddToBuffer() throws InterruptedException {
        ValueCache cache = null;
        Value v = ValueFactory.createValueModel(3.4);
        cache.addValueToBuffer(point, v);
        List<Value> buffer = cache.getValueBuffer(point);

        assertFalse(buffer.isEmpty());
        Value v1 = ValueFactory.createValueModel(3.4);
        cache.addValueToBuffer(point, v1);
        Thread.sleep(100);
        assertEquals(2, cache.getValueBuffer(point).size());
        Value v2 = ValueFactory.createValueModel(3.4);
        cache.addValueToBuffer(point, v2);
        Thread.sleep(100);
        assertEquals(3, cache.getValueBuffer(point).size());
        Value v3 = ValueFactory.createValueModel(3.4);
        cache.addValueToBuffer(point, v3);
        assertEquals(4, cache.getValueBuffer(point).size());
        Thread.sleep(100);

        List<Value> buffer1 = cache.getValueBuffer(point);
        Collections.sort(buffer1);
        Value top = buffer1.get(0);
        Value bottom = buffer1.get(3);
        assertTrue(top.getTimestamp().getTime() > bottom.getTimestamp().getTime());


    }


}
