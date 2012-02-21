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

package com.nimbits.client.model.point;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.intelligence.IntelligenceModelFactory;
import com.nimbits.client.model.value.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PointModel implements Serializable, Point {
    private static  long serialVersionUID = 10L;

    private int entityType = EntityType.point.getCode();

    private long id;

    private String uuid;

    private long lastChecked;

    private long createDate;

    private long lastAlarmSent;

    private String host;

    private long userFK;


    private double highAlarm = 0.0;

    private int expire = 90;

    private String unit;

    private double compression = 0.1;

    private double lowAlarm = 0.0;

    private boolean highAlarmOn;

    private boolean lowAlarmOn;

    private String description;

    private String tag;

    private boolean idleAlarmOn;

    private Integer idleSeconds = 0;

    //reset on any data write
    private boolean idleAlarmSent;

    private boolean ignoreIncomingCompressedValues;


    private Intelligence intelligence;


    // Constructors
    public PointModel(final Point p) {
        this.id = p.getId();
        this.uuid = p.getUUID();

        this.lastChecked = p.getLastChecked().getTime();
        this.host = p.getHost();

        this.userFK = p.getUserFK();
        this.createDate = p.getCreateDate().getTime();
        this.highAlarm = p.getHighAlarm();
        this.expire = p.getExpire();
        this.unit = p.getUnit();
        this.compression = p.getCompression();
        this.lowAlarm = p.getLowAlarm();
        this.highAlarmOn = p.isHighAlarmOn();
        this.lowAlarmOn = p.isLowAlarmOn();
        this.tag = p.getTag();
        this.idleAlarmOn = p.isIdleAlarmOn();
        this.idleSeconds = p.getIdleSeconds();
        this.idleAlarmSent = p.getIdleAlarmSent();
        this.ignoreIncomingCompressedValues = p.getIgnoreIncomingCompressedValues();

        this.targetValue = p.getTargetValue();
        this.values = p.getValues();
        this.value = p.getValue();


    }

    public PointModel() {
    }


    // End Constructors

    private double targetValue;

    private List<Value> values;

    private Value value;

    @Override
    public double getCompression() {
        return this.compression;
    }

    @Override
    public Date getCreateDate() {
        return new Date(this.createDate);
    }



    @Override
    public int getExpire() {
        return expire;
    }

    @Override
    public double getHighAlarm() {
        return highAlarm;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Date getLastChecked() {

        return new Date(lastChecked);
    }

    @Override
    public double getLowAlarm() {
        return lowAlarm;
    }



    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getUnit() {
        return unit;
    }


    @Override
    public long getUserFK() {
        return userFK;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public Value getValue() {
        return value;
    }


    @Override
    public List<Value> getValues() {
        if (values == null) {
            values = new ArrayList<Value>();

        }
        return values;
    }

    @Override
    public boolean isHighAlarmOn() {
        return highAlarmOn;
    }

    @Override

    public boolean isLowAlarmOn() {
        return lowAlarmOn;
    }



    @Override
    public void setCompression(final double compression) {
        this.compression = compression;
    }

    @Override
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate.getTime();
    }

    @Override
    public void setExpire(final int expire) {
        this.expire = expire;
    }

    @Override
    public void setHighAlarm(double highAlarm) {
        this.highAlarm = highAlarm;
    }

    @Override
    public void setHighAlarmOn(final boolean highAlarmOn) {
        this.highAlarmOn = highAlarmOn;
    }

    @Override
    public void setHost(final String host) {
        this.host = host;
    }

    @Override
    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public void setLastChecked(final Date lastChecked) {
        this.lastChecked = lastChecked.getTime();
    }

    @Override
    public void setLowAlarm(final double lowAlarm) {
        this.lowAlarm = lowAlarm;
    }

    @Override
    public void setLowAlarmOn(final boolean lowAlarmOn) {
        this.lowAlarmOn = lowAlarmOn;
    }

    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    @Override
    public void setUnit(final String unit) {
        this.unit = unit;
    }

    @Override
    public void setUserFK(final long userFK) {
        this.userFK = userFK;
    }

    @Override
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setValue(final Value value) {
        this.value = value;
    }

    @Override
    public void setValues(final List<Value> values) {
        this.values = values;
    }


    @Override
    public void setTargetValue(final double targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public double getTargetValue() {

        return targetValue;
    }

    @Override
    public void setIgnoreIncomingCompressedValues(
            final boolean ignoreIncomingCompressedValues) {
        this.ignoreIncomingCompressedValues = ignoreIncomingCompressedValues;
    }

    @Override
    public boolean getIgnoreIncomingCompressedValues() {

        return ignoreIncomingCompressedValues;
    }


    @Override
    public boolean isIdleAlarmOn() {
        return idleAlarmOn;
    }


    @Override
    public void setIdleAlarmOn(final boolean idleAlarmOn) {
        this.idleAlarmOn = idleAlarmOn;
    }

    @Override
    public int getIdleSeconds() {
        return idleSeconds;
    }

    @Override
    public void setIdleSeconds(final int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }

    @Override
    public boolean getIdleAlarmSent() {
        return idleAlarmSent;
    }

    @Override
    public void setIdleAlarmSent(final boolean idleAlarmSent) {
        this.idleAlarmSent = idleAlarmSent;
    }

    public EntityType getEntityType() {
        return EntityType.get(this.entityType);
    }



}
