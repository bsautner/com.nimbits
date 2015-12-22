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

package com.nimbits.client.model.value;


import com.google.gson.annotations.Expose;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;

import java.io.Serializable;
import java.util.Date;


public class Value implements Serializable, Comparable<Value> {

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
    protected Value() {

    }



    protected Value(Double lat, Double lng, Double v, Date time, String data, String metadata, Integer alert) {
        this.d = v;
        this.t = time == null ? new Date().getTime() : time.getTime();
        this.dx = data;
        this.m = metadata;
        this.lg = lng;
        this.lt = lat;
        this.st = alert;
    }


    public String getData() {
        return dx;
    }

    public Double getLatitude() {
        return this.lt;
    }


    public Double getLongitude() {
        return this.lg;
    }


    public Double getDoubleValue() {
        return this.d;

    }


    public String getValueWithData() {
        StringBuilder sb = new StringBuilder(INT);
        if (this.d != null && this.d != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(this.d);
        }
        if (this.dx != null && this.dx.trim().length() > 0) {
            sb.append(' ');
            sb.append(this.dx);
        }
        return sb.toString().trim();

    }


    public Date getTimestamp() {
        return this.t == null ? new Date() : new Date(this.t);
    }


    public void initTimestamp() {
        if (this.t == null) {
            this.t = System.currentTimeMillis();
        }
    }


    public AlertType getAlertState() {
        return this.st == null ? AlertType.OK : AlertType.get(this.st);
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

        Value value = (Value) o;

        if (lt != null ? !lt.equals(value.lt) : value.lt != null) return false;
        if (lg != null ? !lg.equals(value.lg) : value.lg != null) return false;
        if (d != null ? !d.equals(value.d) : value.d != null) return false;
        if (!t.equals(value.t)) return false;
        if (dx != null ? !dx.equals(value.dx) : value.dx != null) return false;
        return !(m != null ? !m.equals(value.m) : value.m != null);

    }

    @Override
    public int hashCode() {
        int result = lt != null ? lt.hashCode() : 0;
        result = 31 * result + (lg != null ? lg.hashCode() : 0);
        result = 31 * result + (d != null ? d.hashCode() : 0);
        result = 31 * result + t.hashCode();
        result = 31 * result + (dx != null ? dx.hashCode() : 0);
        result = 31 * result + (m != null ? m.hashCode() : 0);
        return result;
    }

    public String getMetaData() {
        return m;
    }

    @Override
    public String toString() {
        return "Value{" +
                "lt=" + lt +
                ", lg=" + lg +
                ", d=" + d +
                ", t=" + t +
                ", dx='" + dx + '\'' +
                ", m='" + m + '\'' +
                ", st=" + st +
                '}';
    }

    public static class Builder {

        Double lat;

        Double lng;

        Double doubleValue;

        Date timestamp;

        String data;

        String meta;

        Integer alertType;

        public Value create() {

            return new Value(lat, lng, doubleValue, timestamp, data, meta, alertType );
        }

        public Builder lat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Builder alertType(AlertType alertType) {
            this.alertType = alertType.getCode();
            return this;
        }


        public Builder lng(Double lng) {
            this.lng = lng;
            return this;
        }


        public Builder doubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
            return this;
        }

        public Builder initValue(Value value) {
            this.doubleValue = value.getDoubleValue();
            this.timestamp = value.getTimestamp();
            this.lng = value.getLongitude();
            this.lat = value.getLatitude();
            this.data = value.getData();
            this.meta = value.getMetaData();
            return this;
        }


        public Builder data(String data) {
            this.data = data;
            return this;
        }


        public Builder meta(String meta) {
            this.meta = meta;
            return this;
        }

        public Builder doubleWithData(final String valueAndNote) {

            if (valueAndNote != null && valueAndNote.trim().length() > 0) {

                if (valueAndNote.contains(" ")) {
                    String a[] = valueAndNote.split(" ");
                    try {
                        this.doubleValue = Double.parseDouble(a[0]);
                        this.data  = valueAndNote.replace(a[0], "").trim();
                    } catch (NumberFormatException ex) {
                        this.data = valueAndNote;

                    }
                } else {
                    try {
                        this.doubleValue = Double.parseDouble(valueAndNote);
                        this.data = null;
                    } catch (NumberFormatException ex) {
                        this.data = valueAndNote;

                    }
                }
            }

            return this;
        }

        public Builder timestamp(Date time) {
            this.timestamp = time;
            return this;
        }
    }
}
