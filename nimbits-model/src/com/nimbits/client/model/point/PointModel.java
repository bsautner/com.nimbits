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
import com.nimbits.client.enums.FilterType;
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

    private long userFK;


    private double highAlarm = 0.0;

    private int expire = 90;

    private String unit;

    private double lowAlarm = 0.0;

    private boolean highAlarmOn;

    private boolean lowAlarmOn;

    private String tag;

    private boolean idleAlarmOn;

    private Integer idleSeconds = 0;

    //reset on any data write
    private boolean idleAlarmSent;

    private int filterType;

    private double filterValue;


    public PointModel(final String uuid) {
        this.uuid = uuid;
    }

    // Constructors
    public PointModel(final Point point) {
        this.id = point.getId();
        this.uuid = point.getUUID();
        this.lastChecked = point.getLastChecked().getTime();
        this.userFK = point.getUserFK();
        this.createDate = point.getCreateDate().getTime();
        this.highAlarm = point.getHighAlarm();
        this.expire = point.getExpire();
        this.unit = point.getUnit();

        this.lowAlarm = point.getLowAlarm();
        this.highAlarmOn = point.isHighAlarmOn();
        this.lowAlarmOn = point.isLowAlarmOn();
        this.tag = point.getTag();
        this.idleAlarmOn = point.isIdleAlarmOn();
        this.idleSeconds = point.getIdleSeconds();
        this.idleAlarmSent = point.getIdleAlarmSent();

        this.targetValue = point.getTargetValue();
        this.values = point.getValues();
        this.value = point.getValue();
        this.filterType = point.getFilterType().getCode();
        this.filterValue = point.getFilterValue();

    }

    protected PointModel() {
    }


    // End Constructors

    private double targetValue;

    private List<Value> values;

    private Value value;



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
            values = new ArrayList<Value>(0);

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
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate.getTime();
    }

    @Override
    public void setExpire(final int expire) {
        this.expire = expire;
    }

    @Override
    public void setHighAlarm(final double highAlarm) {
        this.highAlarm = highAlarm;
    }

    @Override
    public void setHighAlarmOn(final boolean highAlarmOn) {
        this.highAlarmOn = highAlarmOn;
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
    public FilterType getFilterType() {
        return FilterType.get(filterType);
    }

    @Override
    public void setFilterType(final FilterType filterType) {
       this.filterType = filterType.getCode();
    }

    @Override
    public double getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(final double value) {
      this.filterValue = value;
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
