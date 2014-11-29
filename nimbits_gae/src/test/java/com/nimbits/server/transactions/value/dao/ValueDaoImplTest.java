/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.value.dao;

import com.google.common.collect.Range;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transaction.value.dao.ValueDayHolder;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class ValueDaoImplTest extends NimbitsServletTest {
    public static final int INT = 1000;

    @Test
    public void recordValuesTest() throws IOException {

        List<Value> values = new ArrayList<Value>(INT);
        List<Value> values2 = new ArrayList<Value>(INT);
        List<Value> valuesCombined = new ArrayList<Value>(INT);

        Random r = new Random();
        Date s = new Date();
        Date e = new Date();
        for (int i = 0; i < INT; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, r.nextInt(365) * -1);
            c.add(Calendar.SECOND, r.nextInt(1000) * -1);
            if (s.getTime() > c.getTimeInMillis()) {
                s = c.getTime();

            }
            if (c.getTimeInMillis() > e.getTime()) {
                e = c.getTime();

            }

            Value value = ValueFactory.createValueModel(r.nextDouble() * 100, c.getTime());

            values.add(value);

        }
        valueDao.recordValues(point, values);


        for (int i = 0; i < INT; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, r.nextInt(365) * -1);
            c.add(Calendar.SECOND, r.nextInt(1000) * -1);
            if (s.getTime() > c.getTimeInMillis()) {
                s = c.getTime();

            }
            if (c.getTimeInMillis() > e.getTime()) {
                e = c.getTime();

            }

            Value value = ValueFactory.createValueModel(r.nextDouble() * 100, c.getTime());

            values2.add(value);

        }
        valueDao.recordValues(point, values2);

        valuesCombined.addAll(values);
        valuesCombined.addAll(values2);

        List<Value> sample = valueDao.getTopDataSeries(point, INT * 2);
        Collections.sort(valuesCombined);
        Collections.sort(sample);
        TestCase.assertEquals(valuesCombined.size(), sample.size());
        for (int i = 0; i < valuesCombined.size(); i++) {
            System.out.println(i);
            TestCase.assertEquals(valuesCombined.get(i).getTimestamp(), sample.get(i).getTimestamp());
        }
        assertEquals(valuesCombined.size(), sample.size());
        int blobs = blobStore.getAllStores(point).size();
        TestCase.assertTrue(blobs > 100);

        valueDao.consolidateBlobs(point);
        int blobC = blobStore.getAllStores(point).size();
        TestCase.assertTrue(blobC < blobs);
    }

    @Test
    public void testTimeRange() throws InterruptedException {

        List<Value> values = getValues();
        ValueDayHolder holder = new ValueDayHolder(new Date(), values.get(0));
        for (int i = 1; i < values.size(); i++) {
            holder.addValue(values.get(i));
        }
        Range<Date> range = holder.getTimeRange();
        Collections.sort(values);
        assertTrue(range.lowerEndpoint().equals(values.get(9).getTimestamp()));
        assertTrue(range.upperEndpoint().equals(values.get(0).getTimestamp()));


    }

    private List<Value> getValues() {
        List<Value> values = new ArrayList<Value>(10);
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < 10; i++) {
            c.add(Calendar.MINUTE, i * -1);
            Value value = ValueFactory.createValueModel(1.0, c.getTime());

            values.add(value);

        }
        return values;
    }
}
