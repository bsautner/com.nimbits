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

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 1:25 PM
 */

import com.nimbits.client.enums.*;
import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;
import java.util.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
@SuppressWarnings("unused")
@Deprecated
public class DataPoint {
    public static final long serialVersionUID = 2L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;


    @Persistent(defaultFetchGroup = "true")
    private DataPointIntelligenceEntity dataPointIntelligenceEntity;

    @Persistent(defaultFetchGroup = "true")
    private CalculationEntity calculationEntity;


    @Persistent
    private String uuid;
    @Persistent
    private Date LastChecked;

    @Persistent
    private Date lastAlarmSent;

    @Persistent
    private String host;
    @Persistent
    private Long catID;
    @Persistent
    private boolean isSystemPoint;
    @Persistent
    private Long userFK;
    @Persistent
    private String name;
    @Persistent
    private Date createDate;
    @Persistent
    private Long X;
    @Persistent
    private Long Y;
    @Persistent
    private Long Z;

    @Persistent
    private String formula;
    @Persistent
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
    private Boolean lowAlarmOn;
    @Persistent
    private int alarmDelay = 1;
    @Persistent
    private String description;
    @Persistent
    private Boolean isPublic;
    @Persistent
    private Boolean postToFacebook;
    @Persistent
    private Boolean alarmToFacebook;
    @Persistent
    private Boolean alarmToEmail;
    @Persistent
    private Boolean sendIM;
    @Persistent
    private Boolean sendAlarmIM;
    @Persistent
    private Boolean sendTweet;
    @Persistent
    private Boolean sendAlarmTweet;
    @Persistent
    private String tag;
    @Persistent
    private String ver;
    @Persistent
    private Boolean idleAlarmOn;

    @Persistent
    private Boolean sendAlertsAsJson;

    @Persistent
    private Integer idleSeconds = 0;

    //reset on any data write
    @Persistent
    private Boolean idleAlarmSent;

    //reset on any data write
    @Persistent
    private Boolean ignoreIncomingCompressedValues;

    //reset on any data write
    @NotPersistent
    private boolean readOnly;

    @NotPersistent
    private AlertType alertState;



    // Constructors
    public DataPoint() {
    }





    @Persistent
    public Double TargetValue;

    @NotPersistent
    public List<Value> values;
    @NotPersistent
    public Value value;


    public DataPointIntelligenceEntity getIntelligence() {

        return dataPointIntelligenceEntity;

    }




    public int getAlarmDelay() {
        return alarmDelay;
    }


    public Boolean getAlarmToFacebook() {
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





    public Boolean getSendAlarmIM() {
        return (sendAlarmIM == null) ? false : sendAlarmIM;
    }


    public Boolean getSendAlarmTweet() {
        return (sendAlarmTweet == null) ? false : sendAlarmTweet;

    }


    public Boolean getSendIM() {
        return (sendIM == null) ? false : sendIM;

    }


    public Boolean getSendTweet() {
        return (sendTweet == null) ? false : sendTweet;

    }

    public String getTag() {
        return tag;
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


    public Boolean isHighAlarmOn() {
        return (highAlarmOn == null) ? false : highAlarmOn;

    }


    public Boolean isLowAlarmOn() {
        return (lowAlarmOn == null) ? false : lowAlarmOn;
    }


    public Boolean isPostToFacebook() {
        return (postToFacebook == null) ? false : postToFacebook;
    }


    public Boolean isPublic() {
        return (isPublic == null) ? false : isPublic;

    }


    public Boolean isSystemPoint() {

        return isSystemPoint;
    }


    public void setAlarmDelay(final int alarmDelay) {
        this.alarmDelay = alarmDelay;
    }


    public void setAlarmToFacebook(final Boolean alarmToFacebook) {
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


    public void setHighAlarmOn(final Boolean highAlarmOn) {
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


    public void setLowAlarmOn(final Boolean lowAlarmOn) {
        this.lowAlarmOn = lowAlarmOn;
    }


    public void setName(final String name) {
        this.name = name ;
    }


    public void setPostToFacebook(final Boolean postToFacebook) {
        this.postToFacebook = postToFacebook;
    }


    public void setPublic(final Boolean isPublic) {
        this.isPublic = isPublic;
    }


    public void setSendAlarmIM(final Boolean sendAlarmIM) {
        this.sendAlarmIM = sendAlarmIM;
    }


    public void setSendAlarmTweet(final Boolean sendAlarmTweet) {
        this.sendAlarmTweet = sendAlarmTweet;
    }


    public void setSendIM(final Boolean sendIM) {
        this.sendIM = sendIM;
    }


    public void setSendTweet(final Boolean sendTweet) {
        this.sendTweet = sendTweet;
    }


    public void setSystemPoint(final Boolean isSystemPoint) {
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


    public Boolean isAlarmToEmail() {
        if (alarmToEmail == null) {
            alarmToEmail = true;
        }

        return alarmToEmail;

    }


    public void setAlarmToEmail(Boolean alarmToEmail) {
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
            final Boolean ignoreIncomingCompressedValues) {
        this.ignoreIncomingCompressedValues = ignoreIncomingCompressedValues;
    }


    public Boolean getIgnoreIncomingCompressedValues() {
        return (ignoreIncomingCompressedValues == null) ? true : ignoreIncomingCompressedValues;

    }


    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }


    public Boolean getReadOnly() {
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

    public Boolean getSendAlertsAsJson() {
        return sendAlertsAsJson == null ? false : sendAlertsAsJson;
    }

    public void setSendAlertsAsJson(Boolean sendAlertsAsJson) {
        this.sendAlertsAsJson = sendAlertsAsJson;
    }



    public int getEntityType() {
        return 0;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public DataPointIntelligenceEntity getDataPointIntelligenceEntity() {
        return dataPointIntelligenceEntity;
    }

    public CalculationEntity getCalculationEntity() {
        return calculationEntity;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Boolean getHighAlarmOn() {
        return highAlarmOn == null ? false : highAlarmOn;
    }

    public Boolean getLowAlarmOn() {
        return lowAlarmOn == null ? false : lowAlarmOn;
    }

    public Boolean getPublic() {

        return isPublic == null ? false : isPublic;
    }

    public Boolean getPostToFacebook() {
        return postToFacebook == null ? false : postToFacebook;
    }

    public Boolean getAlarmToEmail() {
        return alarmToEmail == null ? false : alarmToEmail;
    }

    public Boolean getIdleAlarmOn() {
        return idleAlarmOn == null ? false : idleAlarmOn;
    }

    public Integer getIdleSeconds() {
        return idleSeconds == null ? 0 : idleSeconds;
    }

    public Boolean getIdleAlarmSent() {
        return idleAlarmSent == null ? false : idleAlarmSent;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
}