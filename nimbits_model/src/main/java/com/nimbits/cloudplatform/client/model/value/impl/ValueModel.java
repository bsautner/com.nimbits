/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.cloudplatform.client.model.value.impl;


import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.model.location.Location;
import com.nimbits.cloudplatform.client.model.location.LocationFactory;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.ValueData;

import java.io.Serializable;
import java.util.Date;


public class ValueModel implements Serializable, Comparable<Value>, Value {

    private static final int INT = 64;
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
        return LocationFactory.createLocation(lt, lg);
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
                      final double d,
                      final Date timestamp,
                      final String note,
                      final ValueData data,
                      final AlertType alert) {

        this.lt = location.getLat();
        this.lg = location.getLng();
        this.d = d;
        this.st = alert.getCode();

        this.t = timestamp.getTime();
        this.n = note;
        this.dx = data.getContent();
    }


    @Override
    public String getNote() {
        return n == null ? "" : n;
    }

    @Override
    public double getDoubleValue() {
        return this.d;

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


    @SuppressWarnings({"InstanceofInterfaces", "CastToConcreteClass", "NonFinalFieldReferenceInEquals"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueModel)) return false;

        ValueModel that = (ValueModel) o;

        if (Double.compare(that.d, d) != 0) return false;
        if (Double.compare(that.lg, lg) != 0) return false;
        if (Double.compare(that.lt, lt) != 0) return false;
        if (st != that.st) return false;
        if (t != that.t) return false;
        if (dx != null ? !dx.equals(that.dx) : that.dx != null) return false;
        if (n != null ? !n.equals(that.n) : that.n != null) return false;

        return true;
    }


}
