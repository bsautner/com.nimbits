/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.defrag;

import com.google.common.collect.Range;
import com.nimbits.client.model.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ValueDayHolder {

    private final Date startOfDay;
    private final List<Value> values;

    public ValueDayHolder(Date startOfDay, Value value) {
        this.startOfDay = startOfDay;
        this.values = new ArrayList<Value>();
        this.values.add(value);
    }

    public Date getStartOfDay() {
        return startOfDay;
    }

    public List<Value> getValues() {
        return values;
    }

    public void addValue(Value value) {
        this.values.add(value);
    }

    public Range<Date> getTimeRange() {
        Collections.sort(values);
        return Range.closed(values.get(values.size() - 1).getTimestamp(), values.get(0).getTimestamp());

    }

}
