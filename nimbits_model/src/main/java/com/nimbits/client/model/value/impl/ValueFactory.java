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


import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueData;

import java.util.Date;


public class ValueFactory {
    private ValueFactory() {
    }

    @Deprecated
    public static Value createValueFromString(final SimpleValue<String> valueAndNote, final Date timestamp) {
        double d = 0;
        String dx = null;
        String sample = valueAndNote.getValue().trim();
        if (!sample.isEmpty()) {

            if (sample.contains(" ")) {
                String a[] = sample.split(" ");
                try {
                    d = Double.parseDouble(a[0]);
                    dx = sample.replace(a[0], "").trim();
                } catch (NumberFormatException ex) {
                    dx = sample;
                    d = Const.CONST_IGNORED_NUMBER_VALUE;
                }
            } else {
                try {
                    d = Double.parseDouble(sample);
                    dx = "";
                } catch (NumberFormatException ex) {
                    dx = sample;
                    d = Const.CONST_IGNORED_NUMBER_VALUE;
                }
            }
        }
        if (dx == null) {
            dx = "";
        }
        return new ValueModel(LocationFactory.createEmptyLocation(), d, timestamp, ValueDataModel.getInstance(SimpleValue.getInstance(dx)), AlertType.OK);
    }

    public static ValueModel createValueModel(final Value v) {

        return new ValueModel(v);

    }


    public static ValueModel createValueModel(final Value v, final AlertType alertType) {

        return new ValueModel(v.getLocation(),
                v.getDoubleValue(),
                v.getTimestamp(),
                v.getData(),
                alertType);

    }


    public static ValueModel createValueModel(final Value v, final String value) {

        return new ValueModel(v, value);

    }

    public static ValueModel createValueModel(final Location location,
                                              final Double d,
                                              final Date timestamp,
                                              final ValueData data,
                                              final AlertType alert) {

        return new ValueModel(location, d, timestamp, data, alert);

    }


    public static ValueModel createValueModel(final double d) {

        return new ValueModel(LocationFactory.createEmptyLocation(), d, new Date(), ValueDataModel.getEmptyInstance(), null);

    }

    public static ValueModel createValueModel(final Date date, final String data) {

        ValueData valueData = ValueDataModel.getInstance(SimpleValue.getInstance(data));
        return new ValueModel(LocationFactory.createEmptyLocation(), null, date, valueData, null);

    }


    public static ValueModel createValueModel(final double d, final Date timestamp) {

        return new ValueModel(LocationFactory.createEmptyLocation(), d, timestamp, ValueDataModel.getEmptyInstance(), null);

    }


    public static Value createValueModel(Value value, Date date) {
        return ValueModel.getInstance(value, date);
    }

    public static Value createValueModel(Double d, Date date, AlertType alertType) {
        return new ValueModel(LocationFactory.createEmptyLocation(), d, date, ValueDataModel.getEmptyInstance(), alertType);
    }

    public static Value createValueModel(double v, String data, String metadata, Date time) {

        return new ValueModel(  v, time, data, metadata );
    }
}
