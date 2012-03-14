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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.value;


import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 2:27 PM
 */
public class ValueModelFactory {
    public static ValueModel createValueModel(final Value v) {

        return new ValueModel(v);

    }

    public static ValueModel createValueModel(final Value v, final String value) {

        return new ValueModel(v, value);

    }
    public static ValueModel createValueModel(final double lat,
                                              final double lng,
                                              final double d,
                                              final Date timestamp,
                                              final String pointUUID,
                                              final String note,
                                              final String data) {

        return new ValueModel(lat, lng, d, timestamp, pointUUID, note, data);

    }

    public static ValueModel createValueModel(final long id,
                                              final double lat,
                                              final double lng,
                                              final double d,
                                              final Date timestamp,
                                              final String pointUUID,
                                              final String note,
                                              final String data) {

        return new ValueModel(lat, lng, d, timestamp, pointUUID, note, data);

    }

    public static ValueModel createValueModel(final double lat,
                                              final double lng,
                                              final double d,
                                              final Date timestamp,
                                              final String pointUUID,
                                              final String note) {

        return new ValueModel(lat, lng, d, timestamp, pointUUID, note, "");

    }

    public static ValueModel createValueModel(final double d) {

        return new ValueModel(0.0, 0.0, d, new Date(), null, "", "");

    }

    public static ValueModel createValueModel(final double d, final String note) {

        return new ValueModel(0.0, 0.0, d, new Date(), null, note, "");

    }

    public static ValueModel createValueModel(final double d, final Date timestamp) {

        return new ValueModel(0.0, 0.0, d, timestamp, null, "", "");

    }
    public static ValueModel createValueModel(final double d, final String note, final Date timestamp) {

        return new ValueModel(0.0, 0.0, d, timestamp, null,note, "");

    }
    public static ValueModel createValueModel(final String valueAndNote, final Date timestamp, final String uuid) {
        return createValueFromString(valueAndNote, timestamp, uuid);

    }
    private static ValueModel createValueFromString(final String valueAndNote, final Date timestamp, final String uuid) {
        double d = 0;
        String note = null;
        String sample = valueAndNote.trim();
        if (sample != null && sample.length() > 0) {

            if (sample.contains(" ")) {
                String a[] = sample.split(" ");
                try {
                    d =  Double.parseDouble(a[0]);
                    note = sample.replace(a[0], "").trim();
                }
                catch (NumberFormatException ex) {
                    note = sample;
                    d = Const.CONST_IGNORED_NUMBER_VALUE;
                }
            }
            else {
                try {
                    d =  Double.parseDouble(sample);
                    note = null;
                }
                catch (NumberFormatException ex) {
                    note = sample;
                    d = Const.CONST_IGNORED_NUMBER_VALUE;
                }
            }
        }


        return new ValueModel(0.0, 0.0, d, timestamp, uuid,note, "");
    }
    public static List<Value> createValueModels(final List<Value> values) {
        final LinkedList<Value> retObj = new LinkedList<Value>();

        for (final Value v : values) {
            retObj.add(createValueModel(v));

        }
        return retObj;

    }


    public static Value createValueModel(final String valueStr,
                                         final String note,
                                         final String lat,
                                         final String lng,
                                         final String data) throws NimbitsException {


        final double value;
        final Date timestamp;
        final double latitude;
        final double longitude;
        try {
            value = (Double.valueOf(valueStr));
            timestamp = (new Date());
            latitude = (lat != null) ? Double.valueOf(lat) : 0;
            longitude = (lng != null) ? Double.valueOf(lng) : 0;
        } catch (NumberFormatException e) {
            throw new NimbitsException(e.getMessage());
        }


        return createValueModel(latitude, longitude, value, timestamp, null, note, data);

    }


}
