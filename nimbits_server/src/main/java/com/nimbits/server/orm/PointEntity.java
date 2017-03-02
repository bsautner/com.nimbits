/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;
import java.util.List;

@PersistenceCapable @Cacheable("false")
public class PointEntity extends EntityStore implements Point {
    private static final int DEFAULT_EXPIRE = 90;
    private static final double DEFAULT_FILTER_VALUE = 0.1;


    @Persistent
    private Double highAlarm = 0.0;

    @Persistent
    private int expire = DEFAULT_EXPIRE;

    @Persistent
    private String unit;

    @Persistent
    private Double filterValue = DEFAULT_FILTER_VALUE;

    @Persistent
    private Integer filterType = 0;

    @Persistent
    private Double lowAlarm = 0.0;

    @Persistent
    private Boolean highAlarmOn;

    @Persistent
    private Boolean lowAlarmOn;

    @Persistent
    private Boolean idleAlarmOn;

    @Persistent
    private Integer idleSeconds = 0;

    //reset on any data write
    @Persistent
    private Boolean idleAlarmSent;

    @Persistent
    private Boolean inferLocation;

    @Persistent
    private Integer pointType;

    @Persistent
    private Integer deltaSeconds;

    @Persistent
    private Boolean deltaAlarmOn;

    @Persistent
    private Double deltaAlarm;

    @Persistent
    private Integer precision;

    @Persistent
    private String batchId;


    @Persistent
    private Long processedTimestamp;


    @Override
    public boolean isIdleAlarmOn() {
        return idleAlarmOn == null ? false : idleAlarmOn;
    }

    @Override
    public void setIdleAlarmOn(final boolean idleAlarmOn) {
        this.idleAlarmOn = idleAlarmOn;
    }

    @Override
    public int getIdleSeconds() {
        return idleSeconds == null ? 0 : idleSeconds;
    }

    @Override
    public void setIdleSeconds(final int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }

    @Override
    public boolean idleAlarmSent() {
        return idleAlarmSent == null ? false : idleAlarmSent;
    }

    @Override
    public void setIdleAlarmSent(final boolean idleAlarmSent) {
        this.idleAlarmSent = idleAlarmSent;
    }

    // Constructors
    @SuppressWarnings("unused")
    protected PointEntity() {
    }


    public PointEntity(final Point point) {
        super(point);
        this.highAlarm = point.getHighAlarm();
        this.expire = point.getExpire();
        this.unit = point.getUnit();
        this.lowAlarm = point.getLowAlarm();
        this.highAlarmOn = point.isHighAlarmOn();
        this.lowAlarmOn = point.isLowAlarmOn();
        this.idleAlarmOn = point.isIdleAlarmOn();
        this.idleSeconds = point.getIdleSeconds();
        this.idleAlarmSent = point.idleAlarmSent();
        this.filterType = point.getFilterType().getCode();
        this.filterValue = point.getFilterValue();
        this.inferLocation = point.inferLocation();
        this.pointType = point.getPointType().getCode();
        this.deltaAlarm = point.getDeltaAlarm();
        this.deltaSeconds = point.getDeltaSeconds();
        this.deltaAlarmOn = point.isDeltaAlarmOn();
        this.precision = point.getPrecision();
        this.processedTimestamp = point.getProcessedTimestamp();
    }


    @NotPersistent
    private List<Value> values;
    @NotPersistent
    private Value value;


    @Override
    public int getExpire() {
        return expire;
    }


    @Override
    public double getHighAlarm() {
        return highAlarm == null ? 0.0 : highAlarm;

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
    public boolean isHighAlarmOn() {
        return highAlarmOn == null ? false : highAlarmOn;

    }

    @Override
    public boolean isLowAlarmOn() {
        return lowAlarmOn == null ? false : lowAlarmOn;
    }

    @Override
    public void setExpire(final int expire) {
        this.expire = expire;
    }

//    @Override
//    public void setFormula(final String formula) {
//        this.formula = formula;
//    }

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
    public FilterType getFilterType() {
        return filterType == null ? FilterType.none : FilterType.get(filterType);
    }

    @Override
    public void setFilterType(final FilterType filterType) {
        this.filterType = filterType.getCode();
    }

    @Override
    public double getFilterValue() {
        return filterValue == null ? 0.0 : filterValue;
    }

    @Override
    public void setFilterValue(final double value) {
        this.filterValue = value;
    }

    @Override
    public boolean inferLocation() {
        return this.inferLocation == null ? false : inferLocation;
    }

    @Override
    public void setInferLocation(boolean inferLocation) {
        this.inferLocation = inferLocation;
    }

    @Override
    public PointType getPointType() {
        return this.pointType == null ? PointType.basic : PointType.get(this.pointType);
    }

    @Override
    public void setPointType(PointType type) {
        this.pointType = type.getCode();
    }

    @Override
    public double getDeltaAlarm() {
        return this.deltaAlarm == null ? 0.0 : deltaAlarm;
    }

    @Override
    public void setDeltaAlarm(double deltaAlarm) {
        this.deltaAlarm = deltaAlarm;
    }

    @Override
    public boolean isDeltaAlarmOn() {
        return deltaAlarmOn == null ? false : deltaAlarmOn;
    }

    @Override
    public void setDeltaAlarmOn(boolean deltaAlarmOn) {
        this.deltaAlarmOn = deltaAlarmOn;
    }

    @Override
    public int getDeltaSeconds() {
        return this.deltaSeconds == null ? 0 : this.deltaSeconds;
    }

    @Override
    public void setDeltaSeconds(int deltaSeconds) {
        this.deltaSeconds = deltaSeconds;
    }


    @Override
    public int getPrecision() {
        return this.precision == null ? 10 : this.precision;
    }

    @Override
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    @Override
    public String getBatchId() {
        return batchId;
    }

    @Override
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public long getProcessedTimestamp() {
        return processedTimestamp == null ? 0 : processedTimestamp;
    }

    @Override
    public void setProcessedTimestamp(long timestamp) {
        this.processedTimestamp = timestamp;
    }


    @Override
    public void update(final Entity update) {
        super.update(update);
        final Point p = (Point) update;
        this.highAlarm = p.getHighAlarm();
        this.lowAlarm = p.getLowAlarm();
        this.unit = p.getUnit();
        this.expire = p.getExpire();
        this.idleAlarmOn = p.isIdleAlarmOn();
        this.highAlarmOn = p.isHighAlarmOn();
        this.lowAlarmOn = p.isLowAlarmOn();
        this.idleAlarmSent = p.idleAlarmSent();
        this.idleSeconds = p.getIdleSeconds();
        this.filterType = p.getFilterType().getCode();
        this.filterValue = p.getFilterValue();
        this.inferLocation = p.inferLocation();
        this.pointType = p.getPointType().getCode();
        this.deltaSeconds = p.getDeltaSeconds();
        this.deltaAlarm = p.getDeltaAlarm();
        this.deltaAlarmOn = p.isDeltaAlarmOn();
        this.precision = p.getPrecision();
        this.processedTimestamp = p.getProcessedTimestamp();
    }

    @Override
    public void validate(User user) {
        super.validate(user);

        if (this.lowAlarm > this.highAlarm && (lowAlarmOn && highAlarmOn)) {
            throw new IllegalArgumentException("Low Alarm setting is less than the high alarm setting");
        }
        if (this.expire < 0) {
            throw new IllegalArgumentException("Expiration date for data must be a positive integer");
        }

        if (this.deltaSeconds < 0 && this.deltaAlarmOn) {
            throw new IllegalArgumentException("delta alert time must be > 0");
        }


    }

    @Override
    public void init(Entity anEntity) {

    }
}
