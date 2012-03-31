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

package com.nimbits.server.task;

import com.nimbits.client.enums.SummaryType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:53 PM
 */
public class TestSummaryTask {
    double[] v ={1,2,3};
    SummaryTask sm = new SummaryTask();

    @Test
    public void testAverage() {


        assertEquals(2, sm.getValue(SummaryType.average, v), 0.0);
    }

    @Test
    public void testMax() {

        assertEquals(3, sm.getValue(SummaryType.max, v), 0.0);
    }

    @Test
    public void testMin() {

        assertEquals(1, sm.getValue(SummaryType.min, v), 0.0);
    }

    @Test
    public void testVariance() {

        assertEquals(1, sm.getValue(SummaryType.variance, v), 0.0);
    }
    @Test
    public void testStDev() {

        assertEquals(1, sm.getValue(SummaryType.standardDeviation, v), 0.0);
    }
    @Test
    public void testSum() {

        assertEquals(6, sm.getValue(SummaryType.sum, v), 0.0);
    }

    @Test
    public void testSkewness() {

        assertEquals(0, sm.getValue(SummaryType.skewness, v), 0.0);
    }
}
