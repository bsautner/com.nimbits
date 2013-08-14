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

package com.nimbits.cloudplatform.helper;

import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.transaction.Transaction;
import org.apache.commons.lang3.Range;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 7:30 PM
 */
public class ValueHelper {

    public static Value recordValue(String name, double value)  {
        Value vx = ValueFactory.createValueModel(value);
        Point point = PointHelper.getPoint(name);

            List<Value> response = Transaction.postValue(point, vx);
            if (response.isEmpty()) {
                throw new RuntimeException("Record Value Failed");

            }
            else {
                return response.get(0);
            }

    }

    public static List<Value> getSeries(String name, int count)   {
        Point point = PointHelper.getPoint(name);

            return Transaction.getSeries(point, Range.between(0, count));

    }

}
