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

package com.nimbits.client.model.value.impl;

import com.nimbits.client.enums.SettingType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.simple.SimpleValue;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueContainer;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by benjamin on 9/9/13.
 */
public class ValueFactoryTest {
    @Test
    public void testCreateValueContainer() throws Exception {
        Value value = ValueFactory.createValueModel(3.12);

        ValueContainer container = ValueFactory.createValueContainer(
                CommonFactory.createEmailAddress(SettingType.admin.getDefaultValue()),
                SimpleValue.getInstance("foo"),
                SimpleValue.getInstance("bar"),
                value);
        assertNotNull(container);
        String j = GsonFactory.getInstance().toJson(container);
        ValueContainer copy = GsonFactory.getInstance().fromJson(j, ValueContainerModel.class);
        assertEquals(container, copy);

        assertEquals(copy.getValue().getDoubleValue(), 3.12, 0.00001);
        assertEquals(copy.getValue().getDoubleValue(), container.getValue().getDoubleValue(), 0.00001);

    }

    @Test
    public void testCreateValueContainer1() throws Exception {

    }

    @Test
    public void testCreateValueFromString() throws Exception {

    }

    @Test
    public void testCreateValueModel() throws Exception {

    }

    @Test
    public void testCreateValueModel1() throws Exception {

    }

    @Test
    public void testCreateValueModel2() throws Exception {

    }

    @Test
    public void testCreateValueModel3() throws Exception {

    }

    @Test
    public void testCreateValueModel4() throws Exception {

    }

    @Test
    public void testCreateValueModel5() throws Exception {

    }

    @Test
    public void testCreateValueModel6() throws Exception {

    }

    @Test
    public void testCreateValueModel7() throws Exception {

    }
}
