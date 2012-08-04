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

package com.nimbits.client.model.point;


import com.nimbits.client.enums.FilterType;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.value.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PointModel extends EntityModel implements Serializable, Point {

    private static final int DEFAULT_EXPIRE = 90;

    private double highAlarm = 0.0;

    private int expire = DEFAULT_EXPIRE;

    private String unit;

    private double lowAlarm = 0.0;

    private boolean highAlarmOn;

    private boolean lowAlarmOn;

    private boolean idleAlarmOn;

    private Integer idleSeconds = 0;

    //reset on any data write
    private boolean idleAlarmSent;

    private int filterType;

    private double filterValue;

    private boolean inferLocation;



    public PointModel(final Entity entity,
                      final double highAlarm,
                      final int expire,
                      final String unit,
                      final double lowAlarm,
                      final boolean highAlarmOn,
                      final boolean lowAlarmOn,
                      final boolean idleAlarmOn,
                      final Integer idleSeconds,
                      final boolean idleAlarmSent,
                      final FilterType filterType,
                      final double filterValue,
                      final boolean inferLocation) throws NimbitsException {
        super(entity);
        this.highAlarm = highAlarm;
        this.expire = expire;
        this.unit = unit;
        this.lowAlarm = lowAlarm;
        this.highAlarmOn = highAlarmOn;
        this.lowAlarmOn = lowAlarmOn;
        this.idleAlarmOn = idleAlarmOn;
        this.idleSeconds = idleSeconds;
        this.idleAlarmSent = idleAlarmSent;
        this.filterType = filterType.getCode();
        this.filterValue = filterValue;
        this.inferLocation = inferLocation;
    }

    // Constructors
    public PointModel(final Point point) throws NimbitsException {
        super(point);
        this.highAlarm = point.getHighAlarm();
        this.expire = point.getExpire();
        this.unit = point.getUnit();

        this.lowAlarm = point.getLowAlarm();
        this.highAlarmOn = point.isHighAlarmOn();
        this.lowAlarmOn = point.isLowAlarmOn();
        this.idleAlarmOn = point.isIdleAlarmOn();
        this.idleSeconds = point.getIdleSeconds();
        this.idleAlarmSent = point.getIdleAlarmSent();

        this.values = point.getValues();
        this.value = point.getValue();
        this.filterType = point.getFilterType().getCode();
        this.filterValue = point.getFilterValue();

    }

    protected PointModel() {
    }


    private List<Value> values;

    private Value value;



    @Override
    public int getExpire() {
        return expire;
    }

    @Override
    public double getHighAlarm() {
        return highAlarm;
    }

     @Override
    public double getLowAlarm() {
        return lowAlarm;
    }


    @Override
    public String getUnit() {
        return unit;
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
    public void setLowAlarm(final double lowAlarm) {
        this.lowAlarm = lowAlarm;
    }

    @Override
    public void setLowAlarmOn(final boolean lowAlarmOn) {
        this.lowAlarmOn = lowAlarmOn;
    }

    @Override
    public void setUnit(final String unit) {
        this.unit = unit;
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
    @Override
    public boolean inferLocation() {
        return inferLocation;
    }
    @Override
    public void setInferLocation(boolean inferLocation) {
        this.inferLocation = inferLocation;
    }
}
