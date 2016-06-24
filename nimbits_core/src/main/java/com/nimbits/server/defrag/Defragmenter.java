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

import com.google.common.collect.ImmutableMap;
import com.nimbits.client.model.value.Value;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Defragmenter {
    private Logger logger = LoggerFactory.getLogger(Defragmenter.class.getName());


    /**
     * splits values up into individual days
     *
     * @param values
     * @return
     */
    public Map<Long, ValueDayHolder> getLongValueDayHolderMap(List<Value> values) {
        final Map<Long, ValueDayHolder> individualDaysValueMap = new HashMap<>(values.size());
        logger.info("Storing" + values.size());

        for (final Value value : values) {
            if (valueHealthy(value)) {
                //zero out the date of the current value we're working with
                final Date zero = zeroOutDateToStart(value.getTimestamp());
                if (individualDaysValueMap.containsKey(zero.getTime())) {

                    individualDaysValueMap.get(zero.getTime()).addValue(value);

                } else {
                    //create a new list for a new day
                    ValueDayHolder holder = new ValueDayHolder(zero, value);
                    individualDaysValueMap.put(zero.getTime(), holder);

                }
            } else {
                logger.warn("Value Rejected - not healthy");
            }
        }
        int count = 0;
        for (ValueDayHolder holder : individualDaysValueMap.values()) {
            count += holder.getValues().size();

        }
        logger.info("Values Arranged " + count + " into segments of " + individualDaysValueMap.values().size());
        return ImmutableMap.copyOf(individualDaysValueMap);
    }


    private boolean valueHealthy(final Value value) {
        //value can be null but if not it must be a number
        return value.getDoubleValue() == null ||
        (! Double.isInfinite(value.getDoubleValue())
                && ! Double.isNaN(value.getDoubleValue()));


    }

    public Date zeroOutDateToStart(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND) * -1);
        c.add(Calendar.SECOND, c.get(Calendar.SECOND) * -1);
        c.add(Calendar.MINUTE, c.get(Calendar.MINUTE) * -1);
        c.add(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) * -1);
        return c.getTime();
    }
}
