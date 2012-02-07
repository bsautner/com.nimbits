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
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;
import java.util.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class DataPoint implements Point {

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
    private Long target;
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

    @NotPersistent
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

    public DataPoint(final long userFK, final EntityName pointName, final Long catID, final String uuid) {
        this();
        this.userFK = userFK;
        this.name = pointName.getValue();
        this.catID = catID;
        this.uuid = uuid;
    }


    public DataPoint(final Point p) {

        this.uuid = p.getUUID();
        this.LastChecked = p.getLastChecked();
        this.host = p.getHost();
        this.catID = p.getCatID();
        this.isSystemPoint = p.isSystemPoint();
        this.userFK = p.getUserFK();
        this.name = p.getName().getValue();
        this.createDate = p.getCreateDate();
        this.X = p.getX();
        this.Y = p.getY();
        this.Z = p.getZ();
        this.target = p.getTarget();
        this.formula = p.getFormula();
        this.highAlarm = p.getHighAlarm();
        this.expire = p.getExpire();
        this.unit = p.getUnit();
        this.compression = p.getCompression();
        this.lowAlarm = p.getLowAlarm();
        this.highAlarmOn = p.isHighAlarmOn();
        this.lowAlarmOn = p.isLowAlarmOn();
        this.alarmDelay = p.getAlarmDelay();
        this.description = p.getDescription();
        this.isPublic = p.isPublic();
        this.postToFacebook = p.isPostToFacebook();
        this.alarmToFacebook = p.getAlarmToFacebook();
        this.sendIM = p.getSendIM();
        this.sendAlarmIM = p.getSendAlarmIM();
        this.sendTweet = p.getSendTweet();
        this.sendAlarmTweet = p.getSendAlarmTweet();
        this.tag = p.getTag();
        this.idleAlarmOn = p.isIdleAlarmOn();
        this.idleSeconds = p.getIdleSeconds();
        this.idleAlarmSent = p.getIdleAlarmSent();
        this.ignoreIncomingCompressedValues = p.getIgnoreIncomingCompressedValues();
        this.readOnly = p.getReadOnly();
        this.TargetValue = p.getTargetValue();
        this.values = p.getValues();
        this.value = p.getValue();
        this.sendAlertsAsJson = p.getSendAlertsAsJson();
    }

    @Persistent
    private Double TargetValue;

    @NotPersistent
    private List<Value> values;
    @NotPersistent
    private Value value;

    @Override
    public Intelligence getIntelligence() {

        return dataPointIntelligenceEntity != null ? IntelligenceModelFactory.createIntelligenceModel(dataPointIntelligenceEntity) : null;

    }

    @Override
    public void setIntelligence(Intelligence intelligence) {
        this.dataPointIntelligenceEntity = new DataPointIntelligenceEntity(intelligence);

    }

    @Override
    public int getAlarmDelay() {
        return alarmDelay;
    }

    @Override
    public boolean getAlarmToFacebook() {
        return (alarmToFacebook != null) && alarmToFacebook;
    }

    @Override
    public long getCatID() {
        return (catID == null) ? 0 : catID;

    }

    @Override
    public double getCompression() {
        return (compression == null) ? 0.0 : compression;

    }

    @Override
    public Date getCreateDate() {
        return this.createDate != null ? this.createDate : new Date(0);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getExpire() {
        return expire;
    }

    @Override
    public String getFormula() {
        return this.formula;
    }

    @Override
    public double getHighAlarm() {
        return (highAlarm == null) ? 0.0 : highAlarm;

    }


    @Override
    public String getHost() {
        return host;
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


    @Override
    public EntityName getName() {
        return CommonFactoryLocator.getInstance().createName(this.name);
    }


    @Override
    public boolean getSendAlarmIM() {
        return (sendAlarmIM == null) ? false : sendAlarmIM;
    }

    @Override
    public boolean getSendAlarmTweet() {
        return (sendAlarmTweet == null) ? false : sendAlarmTweet;

    }

    @Override
    public boolean getSendIM() {
        return (sendIM == null) ? false : sendIM;

    }

    @Override
    public boolean getSendTweet() {
        return (sendTweet == null) ? false : sendTweet;

    }

    public String getTag() {
        return tag;
    }

    @Override
    public long getTarget() {
        return (target == null) ? 0 : target;

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
    public long getX() {
        return X == null ? 0 : X;
    }

    @Override
    public long getY() {
        return Y == null ? 0 : Y;
    }

    @Override
    public long getZ() {
        return Z == null ? 0 : Z;
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
    public boolean isPostToFacebook() {
        return (postToFacebook == null) ? false : postToFacebook;
    }

    @Override
    public boolean isPublic() {
        return (isPublic == null) ? false : isPublic;

    }

    @Override
    public boolean isSystemPoint() {

        return isSystemPoint;
    }

    @Override
    public void setAlarmDelay(final int alarmDelay) {
        this.alarmDelay = alarmDelay;
    }

    @Override
    public void setAlarmToFacebook(final boolean alarmToFacebook) {
        this.alarmToFacebook = alarmToFacebook;
    }

    @Override
    public void setCatID(final long catID) {
        this.catID = catID;
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
    public void setDescription(final String value) {
        this.description = value;

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


    //
//    public void setHighestRecordedValue(final double highestRecordedValue) {
//        this.highestRecordedValue = highestRecordedValue;
//    }
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
    public void setName(final EntityName name) {
        this.name = name.getValue();
    }

    @Override
    public void setPostToFacebook(final boolean postToFacebook) {
        this.postToFacebook = postToFacebook;
    }

    @Override
    public void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public void setSendAlarmIM(final boolean sendAlarmIM) {
        this.sendAlarmIM = sendAlarmIM;
    }

    @Override
    public void setSendAlarmTweet(final boolean sendAlarmTweet) {
        this.sendAlarmTweet = sendAlarmTweet;
    }

    @Override
    public void setSendIM(final boolean sendIM) {
        this.sendIM = sendIM;
    }

    @Override
    public void setSendTweet(final boolean sendTweet) {
        this.sendTweet = sendTweet;
    }

    @Override
    public void setSystemPoint(final boolean isSystemPoint) {
        this.isSystemPoint = isSystemPoint;
    }

    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

//    @Override
//    public void setTarget(final long long1) {
//        this.target = long1;
//    }

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

//    @Override
//    public void setX(long x) {
//        X = x;
//    }
//
//    @Override
//    public void setY(long y) {
//        Y = y;
//    }
//
//    @Override
//    public void setZ(long z) {
//        Z = z;
//    }

    @Override
    public boolean isAlarmToEmail() {
        if (alarmToEmail == null) {
            alarmToEmail = true;
        }

        return alarmToEmail;

    }

    @Override
    public void setAlarmToEmail(boolean alarmToEmail) {
        this.alarmToEmail = alarmToEmail;
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

    @Override
    public void setIgnoreIncomingCompressedValues(
            final boolean ignoreIncomingCompressedValues) {
        this.ignoreIncomingCompressedValues = ignoreIncomingCompressedValues;
    }

    @Override
    public boolean getIgnoreIncomingCompressedValues() {
        return (ignoreIncomingCompressedValues == null) ? true : ignoreIncomingCompressedValues;

    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean getReadOnly() {
        return readOnly;
    }

    @Override
    public void setClientType(final ClientType clientType) {
        //not implementented
    }

    @Override
    public ClientType getClientType() {
        return null;
    }

    @Override
    public AlertType getAlertState() {
        return alertState;
    }

    @Override
    public void setAlertState(final AlertType alertState) {
        this.alertState = alertState;
    }


    @Override
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

    @Override
    public Calculation getCalculation() {
        return calculationEntity != null ? PointModelFactory.createCalculation(this.calculationEntity) : null;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(this.entityType);
    }

    @Override
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType.getCode();
    }


    @Override
    public void setCalculation(Calculation calculation) {
        this.calculationEntity = new CalculationEntity(calculation);

    }



    public void updateCalculation(final Calculation calculation) {
        if (this.calculationEntity != null) {
            this.calculationEntity.setEnabled(calculation.getEnabled());
            this.calculationEntity.setFormula(calculation.getFormula());
            this.calculationEntity.setTarget(calculation.getTarget());
            this.calculationEntity.setX(calculation.getX());
            this.calculationEntity.setY(calculation.getY());
            this.calculationEntity.setZ(calculation.getZ());
        }
    }

    public void updateIntelligence(Intelligence intelligence) {
        if (this.dataPointIntelligenceEntity != null) {
            this.dataPointIntelligenceEntity.setEnabled(intelligence.getEnabled());
            this.dataPointIntelligenceEntity.setInput(intelligence.getInput());
            this.dataPointIntelligenceEntity.setNodeId(intelligence.getNodeId());
            this.dataPointIntelligenceEntity.setResultsInPlainText(intelligence.getResultsInPlainText());
            this.dataPointIntelligenceEntity.setResultTarget(intelligence.getResultTarget());
            this.dataPointIntelligenceEntity.setTargetPointId(intelligence.getTargetPointId());
        }
        //To change body of created methods use File | Settings | File Templates.
    }


}
