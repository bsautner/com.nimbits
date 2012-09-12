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

package com.nimbits.server.transactions.memcache.value;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.value.ValueTransactionFactory;
import org.junit.Test;

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
    private MemcacheService buffer;

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

            }
            else {
             value = ValueFactory.createValueModel(r.nextDouble(),now);

            }
           values.add(value);
        }


        List<Value> result = ValueMemCacheImpl.getClosestMatchToTimestamp(values, now);
        assertFalse(result.isEmpty());
        assertEquals(result.get(0).getTimestamp(),now );

        Value sample = values.get(r.nextInt(99));
        List<Value> result2 = ValueMemCacheImpl.getClosestMatchToTimestamp(values, new Date(sample.getTimestamp().getTime() + 1000));
        assertFalse(result2.isEmpty());
        assertEquals(sample.getTimestamp() , result2.get(0).getTimestamp());

    }


    @Test
    public void splitListTest() {

        Random random = new Random();
        int size = 1000007;

        double sum = 0.0;

        List<Value> list = new ArrayList<Value>(size);
        for (int i = 0; i < size; i ++) {
             double v = random.nextDouble();
             sum += v;
             list.add(ValueFactory.createValueModel(v));

        }
        assertEquals(list.size(), size);
        List<List<Value>> split = new ValueMemCacheImpl(point).splitUpList(list);
        int expectedSize = list.size() / Const.CONST_QUERY_CHUNK_SIZE;
        if ( list.size()  % Const.CONST_QUERY_CHUNK_SIZE > 0) {
            expectedSize++;
        }

        assertEquals(expectedSize, split.size());





    }

    @Test
    public void testSafeNamespace1() {
        String sample = "valueCachenoguchi@-~!@##$%^^&*()_--.tatsu-gmail.com-テスト2";
        ValueMemCacheImpl impl = new ValueMemCacheImpl(user);
        String safe = MemCacheKey.getSafeNamespaceKey(sample);

        final String bufferNamespace = MemCacheKey.valueCache + safe;

        String currentValueCacheKey = MemCacheKey.currentValueCache + safe;
         buffer = MemcacheServiceFactory.getMemcacheService(bufferNamespace);

    }

    @Test
    public void testGetPrevValue() throws NimbitsException, InterruptedException {
        ValueMemCacheImpl impl = new ValueMemCacheImpl(point);
        Value v = ValueFactory.createValueModel(D);
        ValueTransactionFactory.getInstance(point).recordValue(v);
        Thread.sleep(1000);
        List<Value> vr = ValueTransactionFactory.getInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        List<Value> dv = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        assertTrue(dv.isEmpty());


        assertNotNull(vr);
        assertFalse(vr.isEmpty());
        assertEquals(v.getDoubleValue(), vr.get(0).getDoubleValue(), DELTA);
        List<Value> vz = impl.getBuffer();
        assertEquals(vz.size(), 1);
        impl.moveValuesFromCacheToStore();
        vz = impl.getBuffer();
        assertEquals(vz.size(), 0);
        dv = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        assertEquals(v.getDoubleValue(), dv.get(0).getDoubleValue(), DELTA);

    }

}
