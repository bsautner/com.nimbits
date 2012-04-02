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

package com.nimbits.server.transactions.orm.legacy;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 1:25 PM
 */

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.intelligence.IntelligenceModelFactory;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.transactions.orm.CalcEntity;
import com.nimbits.server.transactions.orm.IntelligenceEntity;

import javax.jdo.annotations.*;
import java.util.Date;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
@SuppressWarnings("unused")
@Deprecated
public class DataPoint {
    public static final long serialVersionUID = 2L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Long id;


    @Persistent(defaultFetchGroup = "true")
    public IntelligenceEntity dataPointIntelligenceEntity;

    @Persistent(defaultFetchGroup = "true")
    public CalcEntity calculationEntity;


    @Persistent
    public String uuid;
    @Persistent
    public Date LastChecked;

    @Persistent
    public Date lastAlarmSent;

    @Persistent
    public String host;
    @Persistent
    public Long catID;
    @Persistent
    public boolean isSystemPoint;
    @Persistent
    public Long userFK;
    @Persistent
    public String name;
    @Persistent
    public Date createDate;
    @Persistent
    public Long X;
    @Persistent
    public Long Y;
    @Persistent
    public Long Z;
    @Persistent
    public Long target;
    @Persistent
    public String formula;
    @Persistent
    public Double highAlarm = 0.0;
    @Persistent
    public int expire = 90;
    @Persistent
    public String unit;
    @Persistent
    public Double compression = 0.1;
    @Persistent
    public Double lowAlarm = 0.0;
    @Persistent
    public Boolean highAlarmOn;
    @Persistent
    public Boolean lowAlarmOn;
    @Persistent
    public int alarmDelay = 1;
    @Persistent
    public String description;
    @Persistent
    public Boolean isPublic;
    @Persistent
    public Boolean postToFacebook;
    @Persistent
    public Boolean alarmToFacebook;
    @Persistent
    public Boolean alarmToEmail;
    @Persistent
    public Boolean sendIM;
    @Persistent
    public Boolean sendAlarmIM;
    @Persistent
    public Boolean sendTweet;
    @Persistent
    public Boolean sendAlarmTweet;
    @Persistent
    public String tag;

    @Persistent
    public Boolean idleAlarmOn;

    @Persistent
    public Boolean sendAlertsAsJson;

    @Persistent
    public Integer idleSeconds = 0;

    //reset on any data write
    @Persistent
    public Boolean idleAlarmSent;

    //reset on any data write
    @Persistent
    public Boolean ignoreIncomingCompressedValues;

    //reset on any data write
    @NotPersistent
    public boolean readOnly;

    @NotPersistent
    public AlertType alertState;



    // Constructors
    public DataPoint() {
    }





    @Persistent
    public Double TargetValue;

    @NotPersistent
    public List<Value> values;
    @NotPersistent
    public Value value;


    public Intelligence getIntelligence() {

        return dataPointIntelligenceEntity != null ? IntelligenceModelFactory.createIntelligenceModel(dataPointIntelligenceEntity) : null;

    }




    public int getAlarmDelay() {
        return alarmDelay;
    }


    public boolean getAlarmToFacebook() {
        return (alarmToFacebook != null) && alarmToFacebook;
    }


    public long getCatID() {
        return (catID == null) ? 0 : catID;

    }


    public double getCompression() {
        return (compression == null) ? 0.0 : compression;

    }


    public Date getCreateDate() {
        return this.createDate != null ? this.createDate : new Date(0);
    }


    public String getDescription() {
        return this.description;
    }


    public int getExpire() {
        return expire;
    }


    public String getFormula() {
        return this.formula;
    }


    public double getHighAlarm() {
        return (highAlarm == null) ? 0.0 : highAlarm;

    }



    public String getHost() {
        return host;
    }


    public long getId() {
        return (id == null) ? 0 : id;
    }


    public Date getLastChecked() {

        return LastChecked != null ? LastChecked : new Date(0);
    }


    public double getLowAlarm() {
        return lowAlarm;
    }





    public boolean getSendAlarmIM() {
        return (sendAlarmIM == null) ? false : sendAlarmIM;
    }


    public boolean getSendAlarmTweet() {
        return (sendAlarmTweet == null) ? false : sendAlarmTweet;

    }


    public boolean getSendIM() {
        return (sendIM == null) ? false : sendIM;

    }


    public boolean getSendTweet() {
        return (sendTweet == null) ? false : sendTweet;

    }

    public String getTag() {
        return tag;
    }


    public long getTarget() {
        return (target == null) ? 0 : target;

    }


    public String getUnit() {
        return unit;
    }


    public long getUserFK() {
        return (userFK == null) ? 0 : userFK;

    }


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


    public long getX() {
        return X == null ? 0 : X;
    }


    public long getY() {
        return Y == null ? 0 : Y;
    }


    public long getZ() {
        return Z == null ? 0 : Z;
    }


    public boolean isHighAlarmOn() {
        return (highAlarmOn == null) ? false : highAlarmOn;

    }


    public boolean isLowAlarmOn() {
        return (lowAlarmOn == null) ? false : lowAlarmOn;
    }


    public boolean isPostToFacebook() {
        return (postToFacebook == null) ? false : postToFacebook;
    }


    public boolean isPublic() {
        return (isPublic == null) ? false : isPublic;

    }


    public boolean isSystemPoint() {

        return isSystemPoint;
    }


    public void setAlarmDelay(final int alarmDelay) {
        this.alarmDelay = alarmDelay;
    }


    public void setAlarmToFacebook(final boolean alarmToFacebook) {
        this.alarmToFacebook = alarmToFacebook;
    }


    public void setCatID(final long catID) {
        this.catID = catID;
    }


    public void setCompression(final double compression) {
        this.compression = compression;
    }


    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }


    public void setDescription(final String value) {
        this.description = value;

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


    //
//    public void setHighestRecordedValue(final double highestRecordedValue) {
//        this.highestRecordedValue = highestRecordedValue;
//    }

    public void setHost(final String host) {
        this.host = host;
    }


    public void setId(final long id) {
        this.id = id;
    }


    public void setLastChecked(final Date lastChecked) {
        LastChecked = lastChecked;
    }


    public void setLowAlarm(final double lowAlarm) {
        this.lowAlarm = lowAlarm;
    }


    public void setLowAlarmOn(final boolean lowAlarmOn) {
        this.lowAlarmOn = lowAlarmOn;
    }


    public void setName(final PointName name) {
        this.name = name.getValue();
    }


    public void setPostToFacebook(final boolean postToFacebook) {
        this.postToFacebook = postToFacebook;
    }


    public void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }


    public void setSendAlarmIM(final boolean sendAlarmIM) {
        this.sendAlarmIM = sendAlarmIM;
    }


    public void setSendAlarmTweet(final boolean sendAlarmTweet) {
        this.sendAlarmTweet = sendAlarmTweet;
    }


    public void setSendIM(final boolean sendIM) {
        this.sendIM = sendIM;
    }


    public void setSendTweet(final boolean sendTweet) {
        this.sendTweet = sendTweet;
    }


    public void setSystemPoint(final boolean isSystemPoint) {
        this.isSystemPoint = isSystemPoint;
    }


    public void setTag(final String tag) {
        this.tag = tag;
    }

//
//    public void setTarget(final long long1) {
//        this.target = long1;
//    }


    public void setUnit(final String unit) {
        this.unit = unit;
    }


    public void setUserFK(final long userFK) {
        this.userFK = userFK;
    }


    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }


    public void setValue(final Value value) {
        this.value = value;
    }


    public void setValues(final List<com.nimbits.client.model.value.Value> values) {
        this.values = values;
    }

//
//    public void setX(long x) {
//        X = x;
//    }
//
//
//    public void setY(long y) {
//        Y = y;
//    }
//
//
//    public void setZ(long z) {
//        Z = z;
//    }


    public boolean isAlarmToEmail() {
        if (alarmToEmail == null) {
            alarmToEmail = true;
        }

        return alarmToEmail;

    }


    public void setAlarmToEmail(boolean alarmToEmail) {
        this.alarmToEmail = alarmToEmail;
    }


    public String toString() {
        return name;
    }



    public void setTargetValue(final double targetValue) {
        TargetValue = targetValue;
    }


    public double getTargetValue() {
        double retVal = 0;
        if (TargetValue != null) {
            retVal = TargetValue;

        }
        return retVal;
    }


    public void setIgnoreIncomingCompressedValues(
            final boolean ignoreIncomingCompressedValues) {
        this.ignoreIncomingCompressedValues = ignoreIncomingCompressedValues;
    }


    public boolean getIgnoreIncomingCompressedValues() {
        return (ignoreIncomingCompressedValues == null) ? true : ignoreIncomingCompressedValues;

    }


    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }


    public boolean getReadOnly() {
        return readOnly;
    }


    public void setClientType(final ClientType clientType) {
        //not implementented
    }


    public ClientType getClientType() {
        return null;
    }


    public AlertType getAlertState() {
        return alertState;
    }


    public void setAlertState(final AlertType alertState) {
        this.alertState = alertState;
    }



    public Date getLastAlarmSent() {
        if (lastAlarmSent == null) {
            lastAlarmSent = new Date(0);
        }
        return new Date(lastAlarmSent.getTime());
    }

    public boolean getSendAlertsAsJson() {
        return sendAlertsAsJson == null ? false : sendAlertsAsJson;
    }

    public void setSendAlertsAsJson(boolean sendAlertsAsJson) {
        this.sendAlertsAsJson = sendAlertsAsJson;
    }



    public int getEntityType() {
        return 0;
    }





}