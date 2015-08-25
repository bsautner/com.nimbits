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


import com.google.gson.annotations.Expose;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueData;

import java.io.Serializable;
import java.util.Date;


public class ValueModel implements Serializable, Comparable<Value>, Value {

    private static final int INT = 64;

    @Expose
    Double lt;
    @Expose
    Double lg;
    @Expose
    Double d;
    @Expose
    Long t;
    @Expose
    String dx;
    @Expose
    String m;

    Integer st;


    @SuppressWarnings("unused")
    protected ValueModel() {

    }

    public ValueModel(double v, Date time, String data, String metadata) {
        this.d = v;
        this.t = time.getTime();
        this.dx = data;
        this.m = metadata;
    }



    public static Value getInstance(final SimpleValue<String> valueAndNote, final Date timestamp) {
        return ValueFactory.createValueFromString(valueAndNote, timestamp);

    }

    public static Value getInstance(final Value value, final Date timestamp) {
        return new ValueModel(value.getLocation(), value.getDoubleValue(), timestamp, value.getData(), value.getAlertState());

    }

    public static Value getInstance(Value value, double v) {
        return new ValueModel(value.getLocation(), v, new Date(), value.getData(), AlertType.OK);

    }

    public static Value getInstance(String data) {
        return new ValueModel(null, null, new Date(), ValueDataModel.getInstance(SimpleValue.getInstance(data)), null);
    }


    @Override
    public ValueData getData() {
        return ValueDataModel.getInstance(SimpleValue.getInstance(dx));
    }

    @Override
    public Location getLocation() {
        if (lt == null || lg == null) {
            return LocationFactory.createEmptyLocation();
        } else {
            return LocationFactory.createLocation(lt, lg);
        }
    }


    public ValueModel(final Value v) {

        setValues(v);

        this.dx = v.getData().getContent();

        this.st = v.getAlertState().getCode();

        this.m = v.getMetaData();

    }

    private void setValues(Value v) {
        this.lt = v.getLocation().getLat();
        this.lg = v.getLocation().getLng();
        this.d = v.getDoubleValue();
        this.t = v.getTimestamp().getTime();

    }

    public ValueModel(final Value v, final String dataOverride) {

        setValues(v);
        this.dx = dataOverride;
        this.st = v.getAlertState().getCode();
        this.m = v.getMetaData();

    }

    public ValueModel(final Location location,
                      final Double d,
                      final Date timestamp,
                      final ValueData data,
                      final AlertType alert) {

        if (location == null || location.isEmpty()) {
            this.lg = null;
            this.lt = null;
        } else {
            this.lt = location.getLat();
            this.lg = location.getLng();
        }



        if (d != null) {
            this.d = d;
        }
        if (alert != null) {
            this.st = alert.getCode();
        } else {
            this.st = AlertType.OK.getCode();
        }

        this.t = timestamp.getTime();

        this.dx = data.getContent();
    }

    @Override
    public double getDoubleValue() {
        return this.d == null ? 0.0 : this.d;

    }


    @Override
    public String getValueWithData() {
        StringBuilder sb = new StringBuilder(INT);
        if (this.d != null && this.d != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(this.d);
        }
        if (this.dx != null && !this.dx.isEmpty()) {
            sb.append(' ');
            sb.append(this.dx);
        }
        return sb.toString().trim();

    }


    @Override
    public Date getTimestamp() {
        return this.t == null ? new Date() : new Date(this.t);
    }

    @Override
    public void initTimestamp() {
        if (this.t == null) {
            this.t = System.currentTimeMillis();
        }
    }

    @Override
    public AlertType getAlertState() {
        return AlertType.get(this.st);
    }

    @Override
    public int compareTo(Value that) {
        return this.getTimestamp().getTime() < that.getTimestamp().getTime()
                ? 1
                : this.getTimestamp().getTime() > that.getTimestamp().getTime()
                ? -1
                : 0;


    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueModel that = (ValueModel) o;

        if (d != null ? !d.equals(that.d) : that.d != null) return false;
        if (m != null ? !m.equals(that.m) : that.m != null) return false;

        if (dx != null ? !dx.equals(that.dx) : that.dx != null) return false;
        if (lg != null ? !lg.equals(that.lg) : that.lg != null) return false;
        if (lt != null ? !lt.equals(that.lt) : that.lt != null) return false;

        if (st != null ? !st.equals(that.st) : that.st != null) return false;
        if (t != null ? !t.equals(that.t) : that.t != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lt != null ? lt.hashCode() : 0;
        result = 31 * result + (lg != null ? lg.hashCode() : 0);
        result = 31 * result + (d != null ? d.hashCode() : 0);
        result = 31 * result + (t != null ? t.hashCode() : 0);
        result = 31 * result + (m != null ? m.hashCode() : 0);

        result = 31 * result + (dx != null ? dx.hashCode() : 0);
        result = 31 * result + (st != null ? st.hashCode() : 0);
        return result;
    }

    @Override
    public String getMetaData() {
        return m;
    }

    @Override
    public String toString() {
        return "ValueModel{" +
                "lt=" + lt +
                ", lg=" + lg +
                ", d=" + d +
                ", t=" + t +
                ", dx='" + dx + '\'' +
                ", m='" + m + '\'' +
                ", st=" + st +
                '}';
    }
}
