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


import com.nimbits.client.enums.AlertType;
import com.nimbits.client.constants.Const;
import java.io.Serializable;
import java.util.Date;


public class ValueModel implements Serializable, Comparable<Value>, Value {

    /**
     *
     */


    double lt;
    double lg;
    double d;
    long t;
    String n;
    String dx;
    int st;

    protected ValueModel() {

    }

    @Override
    public String getData() {
        return this.dx == null ? "" : this.dx;
    }


    public ValueModel(final Value v) {

        this.lt = v.getLatitude();
        this.lg = v.getLongitude();
        this.d = v.getDoubleValue();
        this.t = v.getTimestamp().getTime();
        this.n = v.getNote();
        this.dx = v.getData();
        this.st = v.getAlertState().getCode();

    }

    public ValueModel(final Value v, final String dataOverride) {

        this.lt = v.getLatitude();
        this.lg = v.getLongitude();
        this.d = v.getDoubleValue();
        this.t = v.getTimestamp().getTime();
        this.n = v.getNote();
        this.dx = dataOverride;
        this.st = v.getAlertState().getCode();

    }

    public ValueModel(final double lat,
                      final double lng,
                      final double d,
                      final Date timestamp,
                      final String note,
                      final String data,
                      final AlertType alert) {

        this.lt = lat;
        this.lg = lng;
        this.d = d;
        this.st = alert.getCode();
        this.t = timestamp.getTime();
        this.n = note;
        this.dx = data;
    }




    @Override
    public String getNote() {
        return n == null ? "" : n;
     }


    @Override
    public double getLatitude() {
        return lt;
    }


    @Override
    public double getLongitude() {
        return lg;
    }

    @Override
    @Deprecated
    public double getValue() {
        return this.d;
    }

    @Override
    public double getDoubleValue() {
        return this.d;

    }

    @Override
    public String getValueWithNote() {
        StringBuilder sb = new StringBuilder();
        if ( this.d != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(this.d);
        }
        if (this.n != null && this.n.length() > 0) {
            sb.append(" ");
            sb.append(this.n);
        }
        return sb.toString().trim();

    }

    @Override
    public Date getTimestamp() {
        return new Date(this.t);
    }

    @Override
    public AlertType getAlertState() {
        return AlertType.get(this.st);
    }

    @Override
    public int compareTo(Value value) {
       return Long.valueOf(t).compareTo(value.getTimestamp().getTime());
    }
}
