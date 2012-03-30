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

package com.nimbits.server.value;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import helper.NimbitsServletTest;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/12
 * Time: 8:51 AM
 */
public class RecordedValueServiceImplTest extends NimbitsServletTest {


    @Test
    public void ignoreByCompressionTest() throws NimbitsException, InterruptedException {

        Value value = ValueModelFactory.createValueModel(1.23);
        Thread.sleep(10);
        Value value2 = ValueModelFactory.createValueModel(1.23);

        Value value3 = ValueModelFactory.createValueModel(2.23);

        RecordedValueServiceFactory.getInstance().recordValue(user, point, value, false);
        RecordedValueServiceImpl impl = new RecordedValueServiceImpl();
        assertTrue(impl.ignoreByCompression(point, value2));
        assertFalse(impl.ignoreByCompression(point, value3));

        point.setFilterValue(10);
        point.setFilterType(FilterType.ceiling);

        assertFalse(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(2.23)));
        assertTrue(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(11)));

        point.setFilterType(FilterType.floor);
        assertTrue(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(2.23)));
        assertFalse(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(11)));

        point.setFilterType(FilterType.none);
        assertFalse(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(11)));

        point.setFilterType(FilterType.percentageHysteresis);
        pointService.updatePoint(point);
        RecordedValueServiceFactory.getInstance().recordValue(user, point, ValueModelFactory.createValueModel(100), false);
        Thread.sleep(10);
        assertTrue(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(105)));
        assertTrue(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(95)));
        assertFalse(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(111)));
        assertFalse(impl.ignoreByCompression(point, ValueModelFactory.createValueModel(80)));

    }


}
