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

package com.nimbits.cloudplatform.server.transactions.value;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.client.service.value.ValueService;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.Assert.*;



/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/5/12
 * Time: 1:49 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class ValueMemCacheImplTest extends NimbitsServletTest {

    private static final double D = 1.23;
    private static final double DELTA = 0.001;
    private MemcacheService buffer;




    @Resource(name = "valueService")
    ValueService valueService;


    @Test
    public void testBuildID() {

        Date now =  new Date();

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

            }
            else {
             value = ValueFactory.createValueModel(r.nextDouble(),now);

            }
           values.add(value);
        }


        List<Value> result = ValueMemCache.getClosestMatchToTimestamp(values, now);
        assertFalse(result.isEmpty());
        assertEquals(result.get(0).getTimestamp(),now );

        Value sample = values.get(r.nextInt(99));
        List<Value> result2 = ValueMemCache.getClosestMatchToTimestamp(values, new Date(sample.getTimestamp().getTime() + 1000));
        assertFalse(result2.isEmpty());
        assertEquals(sample.getTimestamp() , result2.get(0).getTimestamp());

    }


    @Test
    public void splitListTest()  {

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
        List<List<Value>> split = ValueMemCache.splitUpList(list);
        int expectedSize = list.size() / Const.CONST_QUERY_CHUNK_SIZE;
        if ( list.size()  % Const.CONST_QUERY_CHUNK_SIZE > 0) {
            expectedSize++;
        }

        assertEquals(expectedSize, split.size());





    }

    @Test
    public void testSafeNamespace1() {
        String sample = "ValueMemCacheImplnoguchi@-~!@##$%^^&*()_--.tatsu-gmail.com-テスト2";

        String safe = MemCacheKey.getSafeNamespaceKey(sample);

      //  final String bufferNamespace = com.nimbits.cloudplatform.server.transactions.memcache.value.ValueMemCacheImpl+ safe;

        String currentValueMemCacheImplKey = MemCacheKey.currentValueCache + safe;
         buffer = MemcacheServiceFactory.getMemcacheService();

    }

    @Test
    public void testGetPrevValue() throws Exception, InterruptedException {

        Value v = ValueFactory.createValueModel(D);
       ValueTransaction.recordValue(user, point, v);
        Thread.sleep(1000);
        List<Value> vr = ValueMemCache.getRecordedValuePrecedingTimestamp(point, new Date());
        List<Value> dv = ValueMemCache.getRecordedValuePrecedingTimestamp(pointChild, new Date());
        assertTrue(dv.isEmpty());


        assertNotNull(vr);
        assertFalse(vr.isEmpty());
        assertEquals(v.getDoubleValue(), vr.get(0).getDoubleValue(), DELTA);
        List<Value> vz = ValueMemCache.getBuffer(point);
        assertEquals(vz.size(), 1);
        ValueMemCache.moveValuesFromCacheToStore(point);
        vz = ValueMemCache.getBuffer(point);
        assertEquals(vz.size(), 0);
        dv = valueDao.getRecordedValuePrecedingTimestamp(point, new Date());
        assertEquals(v.getDoubleValue(), dv.get(0).getDoubleValue(), DELTA);

    }

}
