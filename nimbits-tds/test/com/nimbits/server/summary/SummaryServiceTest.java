/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.summary;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.summary.*;
import com.nimbits.server.value.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:53 PM
 */
public class SummaryServiceTest extends NimbitsServletTest{
    private static final int SUMMARY_INTERVAL_MS = 60000;
    private static final double DELTA = 0.001;
    double[] v ={1,2,3};


    @Test
    public void testProcessGet() throws NimbitsException, InterruptedException {

        EntityName n = CommonFactoryLocator.getInstance().createName("summary test");
        Entity e = EntityModelFactory.createEntity(n, EntityType.summary);
        Summary summary = SummaryModelFactory.createSummary(e,point.getKey(),
                pointChild.getKey(),SummaryType.average, SUMMARY_INTERVAL_MS, new Date(0));
        point.setFilterValue(0.0);
        EntityServiceFactory.getInstance().addUpdateEntity(point);
        EntityServiceFactory.getInstance().addUpdateEntity(summary);
        List<Summary> r = SummaryTransactionFactory.getInstance(user).readSummariesToEntity(point);
        assertFalse(r.isEmpty());
         int x = 0;
        int c = 100;
        double[]  d = new double[c];
        Random rand = new Random();
        for (int i = 0; i < c; i++){
            double dx = rand.nextDouble() * 100;
            Value value = ValueModelFactory.createValueModel(dx);
            Value vr = RecordedValueServiceFactory.getInstance().recordValue(user, point, value, false);
                   x += i;
            assertNotNull(vr);
            Thread.sleep(10);
            d[i] = dx;
        }
        double com = SummaryServiceFactory.getInstance().getValue(SummaryType.average, d);

        SummaryServiceFactory.getInstance().processSummaries(user,  point);

        Value result = RecordedValueServiceFactory.getInstance().getCurrentValue(pointChild);
        Thread.sleep(100);
        assertNotNull(result);
        assertEquals(com, result.getDoubleValue(), DELTA);





    }


    @Test
    public void testAverage() {


        assertEquals(2,  SummaryServiceFactory.getInstance().getValue(SummaryType.average, v), 0.0);
    }

    @Test
    public void testMax() {

        assertEquals(3,  SummaryServiceFactory.getInstance().getValue(SummaryType.max, v), 0.0);
    }

    @Test
    public void testMin() {

        assertEquals(1,  SummaryServiceFactory.getInstance().getValue(SummaryType.min, v), 0.0);
    }

    @Test
    public void testVariance() {

        assertEquals(1,  SummaryServiceFactory.getInstance().getValue(SummaryType.variance, v), 0.0);
    }
    @Test
    public void testStDev() {

        assertEquals(1,  SummaryServiceFactory.getInstance().getValue(SummaryType.standardDeviation, v), 0.0);
    }
    @Test
    public void testSum() {

        assertEquals(6,  SummaryServiceFactory.getInstance().getValue(SummaryType.sum, v), 0.0);
    }

    @Test
    public void testSkewness() {

        assertEquals(0,  SummaryServiceFactory.getInstance().getValue(SummaryType.skewness, v), 0.0);
    }
}
