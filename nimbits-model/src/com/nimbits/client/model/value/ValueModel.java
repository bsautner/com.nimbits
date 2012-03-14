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
import com.nimbits.client.model.Const;
import java.io.Serializable;
import java.util.Date;


public class ValueModel implements Serializable, Comparable<Value>, Value {

    /**
     *
     */
    private static final long serialVersionUID = 1l;
    private long id;
    private double lat;
    private double lng;
    private double d;
    private long timestamp;
    private String pointUUID;
    private String note;
    private String data;
    private int alertState;

    public ValueModel() {
    }

    @Override
    public void setAlertType(final AlertType alertState) {
        this.alertState = alertState.getCode();
    }

    @Override
    public String getData() {
        return this.data == null ? "" : this.data;
    }


    public ValueModel(final Value v) {
        this.id = v.getId();
        this.lat = v.getLatitude();
        this.lng = v.getLongitude();
        this.d = v.getNumberValue();
        this.timestamp = v.getTimestamp().getTime();
        this.pointUUID = v.getPointUUID();
        this.note = v.getNote();
        this.data = v.getData();

    }

    public ValueModel(final Value v, final String dataOverride) {
        this.id = v.getId();
        this.lat = v.getLatitude();
        this.lng = v.getLongitude();
        this.d = v.getNumberValue();
        this.timestamp = v.getTimestamp().getTime();
        this.pointUUID = v.getPointUUID();
        this.note = v.getNote();
        this.data = dataOverride;

    }

    public ValueModel(final double lat,
                      final double lng,
                      final double d,
                      final Date timestamp,
                      final String pointUUID,
                      final String note,
                      final String data) {
        this.id = 0;
        this.lat = lat;
        this.lng = lng;
        this.d = d;
        this.timestamp = timestamp.getTime();
        this.pointUUID = pointUUID;
        this.note = note;
        this.data = data;
    }

    public ValueModel(final long id,
                      final double lat,
                      final double lng,
                      final double d,
                      final Date timestamp,
                      final String pointUUID,
                      final String note,
                      final String data) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.d = d;
        this.timestamp = timestamp.getTime();
        this.pointUUID = pointUUID;
        this.note = note;
        this.data = data;
    }

    //
    @Override
    public long getId() {
        return id;
    }


    @Override
    public String getNote() {
        return note == null ? "" : note;
     }


    @Override
    public double getLatitude() {
        return lat;
    }


    @Override
    public double getLongitude() {
        return lng;
    }


    @Override
    public String getPointUUID() {
        return pointUUID;
    }


    @Override
    @Deprecated
    public double getValue() {
        return this.d;
     }

    @Override
    public double getNumberValue() {
        return this.d;

    }

    @Override
    public String getValueWithNote() {
        StringBuilder sb = new StringBuilder();
        if ( this.d != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(this.d);
        }
        if (this.note != null && this.note.length() > 0) {
            sb.append(" ");
            sb.append(this.note);
        }
        return sb.toString().trim();

    }

    @Override
    public Date getTimestamp() {
        return new Date(this.timestamp);
    }

    @Override
    public AlertType getAlertState() {
        return AlertType.get(this.alertState);
    }

    @Override
    public int compareTo(Value value) {
       return Long.valueOf(timestamp).compareTo(value.getTimestamp().getTime());
    }
}
