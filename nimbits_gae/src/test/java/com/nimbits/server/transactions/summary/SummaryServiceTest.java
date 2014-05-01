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

package com.nimbits.server.transactions.summary;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.summary.SummaryModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.summary.SummaryServiceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:53 PM
 */

public class SummaryServiceTest extends NimbitsServletTest {

    private static final int SUMMARY_INTERVAL_MS = 60000;
    private static final double DELTA = 0.001;
    private static final int INT = 50;
    double[] v = {1, 2, 3};


    SummaryService summaryService;
    @Before
    public void setup() {
        super.setup();
        summaryService = SummaryServiceFactory.getServiceInstance(engine, taskService);

    }

    @Test
    public void testProcessGet() throws Exception {

        EntityName n = CommonFactory.createName("summary test", EntityType.summary);
        Entity e = EntityModelFactory.createEntity(n, EntityType.summary);
        Summary summary = SummaryModelFactory.createSummary(e, EntityModelFactory.createTrigger(point.getKey()),
                EntityModelFactory.createTarget(pointChild.getKey()), true, SummaryType.average, SUMMARY_INTERVAL_MS, new Date(0));
        point.setFilterValue(0.0);
        entityService.addUpdateSingleEntity(point);
        entityService.addUpdateSingleEntity(summary);
        final List<Entity> r = entityService.getEntityByTrigger(user, point, EntityType.summary);


        Assert.assertFalse(r.isEmpty());

        int c = 100;
        double[] d = new double[c];
        Random rand = new Random();
        for (int i = 0; i < c; i++) {
            double dx = rand.nextDouble() * 100;
            Value value = ValueFactory.createValueModel(dx);
            Value vr = valueService.recordValue(req, user, point, value, false);
            Assert.assertNotNull(vr);
            Thread.sleep(INT);
            d[i] = dx;
        }
        double com = SummaryServiceFactory.getServiceInstance(engine, taskService).getValue(SummaryType.average, d);

        summaryService.process(req, user, point);

        List<Value> result = valueService.getCurrentValue(pointChild);
        Thread.sleep(100);
        Assert.assertNotNull(result);
        Assert.assertEquals(com, result.get(0).getDoubleValue(), DELTA);


    }


    @Test
    public void testAverage() {


        Assert.assertEquals(2, summaryService.getValue(SummaryType.average, v), 0.0);
    }

    @Test
    public void testMax() {

        Assert.assertEquals(3, summaryService.getValue(SummaryType.max, v), 0.0);
    }

    @Test
    public void testMin() {

        Assert.assertEquals(1, summaryService.getValue(SummaryType.min, v), 0.0);
    }

    @Test
    public void testVariance() {

        Assert.assertEquals(1, summaryService.getValue(SummaryType.variance, v), 0.0);
    }

    @Test
    public void testStDev() {

        Assert.assertEquals(1, summaryService.getValue(SummaryType.standardDeviation, v), 0.0);
    }

    @Test
    public void testSum() {

        Assert.assertEquals(6, summaryService.getValue(SummaryType.sum, v), 0.0);
    }

    @Test
    public void testSkewness() {

        Assert.assertEquals(0, summaryService.getValue(SummaryType.skewness, v), 0.0);
    }
}
