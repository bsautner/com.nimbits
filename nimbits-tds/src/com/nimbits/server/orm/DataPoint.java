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

package com.nimbits.server.orm;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;
import java.util.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class DataPoint implements Point {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent(defaultFetchGroup = "true")
    @Deprecated
    public DataPointIntelligenceEntity dataPointIntelligenceEntity;

    @Persistent(defaultFetchGroup = "true")
    @Deprecated
    public CalculationEntity calculationEntity;

    @Persistent
    private String uuid;
    @Persistent
    private Date LastChecked;

    @Persistent
    @Deprecated
    public Date lastAlarmSent;

    @Persistent
    @Deprecated
    private Long catID;
    @Persistent
    private boolean isSystemPoint;
    @Persistent
    private Long userFK;
    @Persistent
    @Deprecated
    public String name;
    @Persistent
    private Date createDate;

    private Double highAlarm = 0.0;
    @Persistent
    private int expire = 90;
    @Persistent
    private String unit;
    @Persistent
    private Double compression = 0.1;
    @Persistent
    private Double lowAlarm = 0.0;
    @Persistent
    private Boolean highAlarmOn;
    @Persistent
    @Deprecated
    private Boolean lowAlarmOn;
    @Persistent
    @Deprecated
    public int alarmDelay = 1;
    @Persistent
    @Deprecated
    public String description;
    @Persistent
    @Deprecated
    public Boolean isPublic;
    @Persistent
    @Deprecated
    public Boolean postToFacebook;
    @Persistent
    @Deprecated
    public Boolean alarmToFacebook;
    @Persistent
    @Deprecated
    public Boolean alarmToEmail;
    @Persistent
    @Deprecated
    public Boolean sendIM;
    @Persistent
    @Deprecated
    public Boolean sendAlarmIM;
    @Persistent
    @Deprecated
    public Boolean sendTweet;
    @Persistent
    @Deprecated
    public Boolean sendAlarmTweet;
    @Persistent
    private String tag;

    @Persistent
    public Boolean idleAlarmOn;

    @Persistent
    @Deprecated
    public Boolean sendAlertsAsJson;

    @Persistent
    private Integer idleSeconds = 0;

    //reset on any data write
    @Persistent
    private Boolean idleAlarmSent;

    @Deprecated
    @NotPersistent
    private boolean readOnly;

    @NotPersistent
    @Deprecated
    private AlertType alertState;

    @NotPersistent
    @Deprecated
    private int entityType = EntityType.point.getCode();

    @Override
    public boolean isIdleAlarmOn() {
        return (idleAlarmOn == null) ? false : idleAlarmOn;
    }

    @Override
    public void setIdleAlarmOn(final boolean idleAlarmOn) {
        this.idleAlarmOn = idleAlarmOn;
    }

    @Override
    public int getIdleSeconds() {
        return (idleSeconds == null) ? 0 : idleSeconds;
    }

    @Override
    public void setIdleSeconds(int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }

    @Override
    public boolean getIdleAlarmSent() {
        return (idleAlarmSent == null) ? false : idleAlarmSent;
    }

    @Override
    public void setIdleAlarmSent(final boolean idleAlarmSent) {
        this.idleAlarmSent = idleAlarmSent;
    }

    // Constructors
    public DataPoint() {
    }


    public DataPoint(User user, Entity entity) {
        this();
        this.userFK = user.getId();
        this.uuid = entity.getEntity();
        this.LastChecked= new Date();
        this.createDate = new Date();
     }


    @Persistent
    private Double TargetValue;

    @NotPersistent
    private List<Value> values;
    @NotPersistent
    private Value value;

    @Override
    public double getCompression() {
        return (compression == null) ? 0.0 : compression;

    }

    @Override
    public Date getCreateDate() {
        return this.createDate != null ? this.createDate : new Date(0);
    }

    @Override
    public int getExpire() {
        return expire;
    }


    @Override
    public double getHighAlarm() {
        return (highAlarm == null) ? 0.0 : highAlarm;

    }

    @Override
    public long getId() {
        return (id == null) ? 0 : id;
    }

    @Override
    public Date getLastChecked() {

        return LastChecked != null ? LastChecked : new Date(0);
    }

    @Override
    public double getLowAlarm() {
        return lowAlarm;
    }



    public String getTag() {
        return tag;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public long getUserFK() {
        return (userFK == null) ? 0 : userFK;

    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @NotPersistent
    public Value getValue() {
        return value;
    }

    @NotPersistent
    public List<Value> getValues() {
        return values;
    }

    @Override
    public boolean isHighAlarmOn() {
        return (highAlarmOn == null) ? false : highAlarmOn;

    }

    @Override
    public boolean isLowAlarmOn() {
        return (lowAlarmOn == null) ? false : lowAlarmOn;
    }


    @Override
    public void setCompression(final double compression) {
        this.compression = compression;
    }

    @Override
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
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
    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public void setLastChecked(final Date lastChecked) {
        LastChecked = lastChecked;
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
    public void setValues(final List<com.nimbits.client.model.value.Value> values) {
        this.values = values;
    }


    @Override
    public String toString() {
        return name;
    }


    @Override
    public void setTargetValue(final double targetValue) {
        TargetValue = targetValue;
    }

    @Override
    public double getTargetValue() {
        double retVal = 0;
        if (TargetValue != null) {
            retVal = TargetValue;

        }
        return retVal;
    }





}
