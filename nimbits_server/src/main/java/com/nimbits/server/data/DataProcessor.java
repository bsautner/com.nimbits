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

import java.math.BigDecimal;
import java.util.Calendar;


public class DataProcessor {

    public boolean ignoreDataByExpirationDate(final Point p, final Value value, final boolean ignored) {
        boolean retVal = ignored;

        if (p.getExpire() > 0) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, p.getExpire() * -1);
            if (value.getTimestamp().getTime() < c.getTimeInMillis()) {
                retVal = true;
            }
        }
        return retVal;
    }

    public boolean ignoreByFilter(final Point point, final Value pv, final Value v) {


        if (v.getData() != null && ! v.getData().equals(pv.getData())) {
            return false;
        }

        if (point.getPointType().equals(PointType.flag)) {
            Integer prevWhole = BigDecimal.valueOf(pv.getDoubleValue()).intValue();
            Integer newWhole = BigDecimal.valueOf(v.getDoubleValue()).intValue();
            return ((prevWhole != 0) && (newWhole != 0)) || ((prevWhole == 0) && (newWhole == 0));

        } else {
            double current = pv.getDoubleValue() == null ? 0.0 : pv.getDoubleValue();
            switch (point.getFilterType()) {

                case fixedHysteresis:

                    double min = current - point.getFilterValue();
                    double max = current + point.getFilterValue();
                    double newValue = v.getDoubleValue();
                    Range<Double> range = Range.closed(min, max);
                    return range.contains(newValue);
//                    return newValue <= max
//                            && newValue >= min;

                case percentageHysteresis:
                    if (point.getFilterValue() > 0) {
                        final double p = current* point.getFilterValue() / 100;
                        return v.getDoubleValue() <= current + p
                                && v.getDoubleValue() >= current - p;


                    } else {

                        return false;
                    }

                case ceiling:
                    return v.getDoubleValue() >= point.getFilterValue();

                case floor:
                    return v.getDoubleValue() <= point.getFilterValue();

                case none:
                    return false;
                default:
                    return false;
            }


        }

    }


}
