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

package com.nimbits.server.data;

import com.google.common.collect.Range;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;

@Component
public class DataProcessor {

    public long getExpireTime(final Point point) {
        return (System.currentTimeMillis() - (86400000 * point.getExpire()));
    }

    public boolean ignoreDataByExpirationDate(final Point p, final Value value, final boolean ignored) {
        boolean retVal = ignored;

        if (p.getExpire() > 0) {
            long exp = getExpireTime(p);

            if (value.getLTimestamp() >= exp) {
                retVal = true;
            }
        }
        return retVal;
    }

    public boolean ignoreByFilter(final Point point, final Value previousValue, final Value incomingValue) {


        if (incomingValue.getData() != null && !incomingValue.getData().equals(previousValue.getData())) {
            return false;
        }
        if (previousValue.getDoubleValue() == null && incomingValue.getDoubleValue() != null) {
            return false;
        }

        if (point.getPointType().equals(PointType.flag)) {
            Integer prevWhole = BigDecimal.valueOf(previousValue.getDoubleValue()).intValue();
            Integer newWhole = BigDecimal.valueOf(incomingValue.getDoubleValue()).intValue();
            return ((prevWhole != 0) && (newWhole != 0)) || ((prevWhole == 0) && (newWhole == 0));

        } else {
            double current = previousValue.getDoubleValue() == null ? 0.0 : previousValue.getDoubleValue();
            switch (point.getFilterType()) {

                case fixedHysteresis:

                    double min = current - point.getFilterValue();
                    double max = current + point.getFilterValue();
                    double newValue = incomingValue.getDoubleValue();
                    Range<Double> range = Range.closed(min, max);
                    return range.contains(newValue);
//                    return newValue <= max
//                            && newValue >= min;

                case percentageHysteresis:
                    if (point.getFilterValue() > 0) {
                        final double p = current * point.getFilterValue() / 100;
                        return incomingValue.getDoubleValue() <= current + p
                                && incomingValue.getDoubleValue() >= current - p;


                    } else {

                        return false;
                    }

                case ceiling:
                    return incomingValue.getDoubleValue() >= point.getFilterValue();

                case floor:
                    return incomingValue.getDoubleValue() <= point.getFilterValue();

                case none:
                    return false;
                default:
                    return false;
            }


        }

    }


}
