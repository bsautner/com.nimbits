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

package com.nimbits.server.zip.impl;


import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/27/12
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompressionImplTest {

    private List<Value> loadSomeData() {
        List<Value> values = new ArrayList<Value>();
        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            Value v = ValueFactory.createValueModel(r.nextDouble());
            values.add(v);
        }
        return values;
    }


    @Test
    public void testExtractBytes() throws Exception {

    }
}
