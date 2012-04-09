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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.admin;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class PointEntity extends EntityStore  {
    private static final int DEFAULT_EXPIRE = 90;
    private static final double DEFAULT_FILTER_VALUE = 0.1;

//    @PrimaryKey
//    @Persistent
//    private com.google.appengine.api.datastore.Key key;

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
    public Boolean idleAlarmOn;

    @Persistent
    private Integer idleSeconds = 0;

    //reset on any data write
    @Persistent
    private Boolean idleAlarmSent;

    @Persistent
    private String legacyKey;




    public boolean isIdleAlarmOn() {
        return (idleAlarmOn == null) ? false : idleAlarmOn;
    }


    public void setIdleAlarmOn(final boolean idleAlarmOn) {
        this.idleAlarmOn = idleAlarmOn;
    }


    public int getIdleSeconds() {
        return (idleSeconds == null) ? 0 : idleSeconds;
    }


    public void setIdleSeconds(final int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }


    public boolean getIdleAlarmSent() {
        return (idleAlarmSent == null) ? false : idleAlarmSent;
    }


    public void setIdleAlarmSent(final boolean idleAlarmSent) {
        this.idleAlarmSent = idleAlarmSent;
    }

    // Constructors
    protected PointEntity() {
    }

    public PointEntity(final Entity entity) throws NimbitsException {
        super(PointEntity.class, entity);

    }
    public PointEntity(final Entity entity, final com.nimbits.server.admin.PointEntity point) throws NimbitsException {
        super(PointEntity.class, entity);
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

    public PointEntity(final Point point) throws NimbitsException {
        super(PointEntity.class, point);
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


    @Persistent
    private Double target;

    @NotPersistent
    private List<Value> values;
    @NotPersistent
    private Value value;



    public int getExpire() {
        return expire;
    }



    public double getHighAlarm() {
        return (highAlarm == null) ? 0.0 : highAlarm;

    }


    public double getLowAlarm() {
        return lowAlarm;
    }



    public String getUnit() {
        return unit;
    }

    @NotPersistent
    public Value getValue() {
        return value;
    }

    @NotPersistent
    public List<Value> getValues() {
        return values;
    }


    public boolean isHighAlarmOn() {
        return (highAlarmOn == null) ? false : highAlarmOn;

    }


    public boolean isLowAlarmOn() {
        return (lowAlarmOn == null) ? false : lowAlarmOn;
    }

    public void setExpire(final int expire) {
        this.expire = expire;
    }

//
//    public void setFormula(final String formula) {
//        this.formula = formula;
//    }


    public void setHighAlarm(final double highAlarm) {
        this.highAlarm = highAlarm;
    }


    public void setHighAlarmOn(final boolean highAlarmOn) {
        this.highAlarmOn = highAlarmOn;
    }


    public void setLowAlarm(final double lowAlarm) {
        this.lowAlarm = lowAlarm;
    }


    public void setLowAlarmOn(final boolean lowAlarmOn) {
        this.lowAlarmOn = lowAlarmOn;
    }


    public void setUnit(final String unit) {
        this.unit = unit;
    }


    public void setValue(final Value value) {
        this.value = value;
    }


    public void setValues(final List<Value> values) {
        this.values = values;
    }


    public FilterType getFilterType() {
        return filterType == null ? FilterType.fixedHysteresis : FilterType.get(filterType);
    }


    public void setFilterType(final FilterType filterType) {
        this.filterType = filterType.getCode();
    }


    public double getFilterValue() {
        return filterValue == null ? 0.0 : filterValue;
    }


    public void setFilterValue(final double value) {
        this.filterValue = value;
    }


    public String getKey() {
        return super.getKey();
    }

    public void setLegacyKey(String legacyKey) {
        this.legacyKey = legacyKey;
    }

    public String getLegacyKey() {
        return legacyKey;
    }
}
