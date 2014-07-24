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
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueData;

import java.io.Serializable;
import java.util.Date;


public class ValueModel implements Serializable, Comparable<Value>, Value {

    private static final int INT = 64;
    /**
     *
     */

    @Expose
    Double lt;
    @Expose
    Double lg;
    @Expose
    Double d;
    @Expose
    Long t;
    @Expose
    String n;
    @Expose
    String dx;

    Integer st;


    @SuppressWarnings("unused")
    protected ValueModel() {

    }


    public static Value getInstance(final SimpleValue<String> valueAndNote, final Date timestamp) {
        return ValueFactory.createValueFromString(valueAndNote, timestamp);

    }
    public static Value getInstance(final Value value, final Date timestamp) {
        return new ValueModel(value.getLocation(), value.getDoubleValue(), timestamp, value.getNote(), value.getData(), value.getAlertState());

    }
    public static Value getInstance(Value value, double v) {
        return new ValueModel(value.getLocation(), v, new Date(), value.getNote(), value.getData(), AlertType.OK);

    }







    @Override
    public ValueData getData() {
        return ValueDataModel.getInstance(SimpleValue.getInstance(dx));
    }

    @Override
    public Location getLocation() {
        if (lt == null || lg == null) {
            return LocationFactory.createEmptyLocation();
        }
        else {
            return LocationFactory.createLocation(lt, lg);
        }
    }


    public ValueModel(final Value v) {

        setValues(v);

        this.dx = v.getData().getContent();

        this.st = v.getAlertState().getCode();

    }

    private void setValues(Value v) {
        this.lt = v.getLocation().getLat();
        this.lg = v.getLocation().getLng();
        this.d = v.getDoubleValue();
        this.t = v.getTimestamp().getTime();
        this.n = v.getNote();
    }

    public ValueModel(final Value v, final String dataOverride) {

        setValues(v);
        this.dx = dataOverride;
        this.st = v.getAlertState().getCode();

    }

    public ValueModel(final Location location,
                      final Double d,
                      final Date timestamp,
                      final String note,
                      final ValueData data,
                      final AlertType alert) {

        if (location.isEmpty()) {
            this.lg = null;
            this.lt = null;
        }
        else {
            this.lt = location.getLat();
            this.lg = location.getLng();
        }

        if (d != null) {
            this.d = d;
        }
        if (alert != null) {
            this.st = alert.getCode();
        }
        else {
            this.st = AlertType.OK.getCode();
        }

        this.t = timestamp.getTime();

        this.n = note;
        this.dx = data.getContent();
    }


    //todo replace with an object to allow n to be null like dx
    @Override
    public String getNote() {
        return n == null ? "" : n;
    }

    @Override
    public double getDoubleValue() {
        return this.d == null ? 0.0 : this.d;

    }

    @Override
    public String getValueWithNote() {
        StringBuilder sb = new StringBuilder(INT);
        if (this.d != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(this.d);
        }
        if (this.n != null && !this.n.isEmpty()) {
            sb.append(' ');
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
    public int compareTo(Value that) {
        return this.t < that.getTimestamp().getTime()
                ? 1
                : this.t > that.getTimestamp().getTime()
                ? -1
                : 0;


    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueModel that = (ValueModel) o;

        if (d != null ? !d.equals(that.d) : that.d != null) return false;
        if (dx != null ? !dx.equals(that.dx) : that.dx != null) return false;
        if (lg != null ? !lg.equals(that.lg) : that.lg != null) return false;
        if (lt != null ? !lt.equals(that.lt) : that.lt != null) return false;
        if (n != null ? !n.equals(that.n) : that.n != null) return false;
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
        result = 31 * result + (n != null ? n.hashCode() : 0);
        result = 31 * result + (dx != null ? dx.hashCode() : 0);
        result = 31 * result + (st != null ? st.hashCode() : 0);
        return result;
    }
}
