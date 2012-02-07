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

import com.nimbits.client.enums.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.value.*;

import java.io.*;
import java.util.*;


public class PointModel implements Serializable, Point {
    private static  long serialVersionUID = 10L;

    private int entityType = EntityType.point.getCode();

    private long id;

    private AlertType alertState;

    private String uuid;

    private long lastChecked;

    private long createDate;

    private long lastAlarmSent;

    private String host;

    private long catID;

    private boolean isSystemPoint;

    private long userFK;

    private String name;

    private long X;

    private long Y;

    private long Z;

    private long target;

    private String formula;

    private double highAlarm = 0.0;

    private int expire = 90;

    private String unit;

    private double compression = 0.1;

    private double lowAlarm = 0.0;

    private boolean highAlarmOn;

    private boolean lowAlarmOn;

    private int alarmDelay = 1;

    private String description;

    private boolean isPublic;

    private boolean postToFacebook;

    private boolean alarmToFacebook;

    private boolean alarmToEmail;

    private boolean sendIM;

    private boolean sendAlarmIM;

    private boolean sendTweet;

    private boolean sendAlarmTweet;

    private String tag;

    private boolean idleAlarmOn;

    private Integer idleSeconds = 0;

    //reset on any data write
    private boolean idleAlarmSent;
    //reset on any data write
    private boolean ignoreIncomingCompressedValues;
    //reset on any data write
    private boolean readOnly;

    private Intelligence intelligence;

    private Calculation calculation;

    private boolean sendAlertAsJson;

    private boolean isSubscription;

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    private ClientType clientType;

    // Constructors
    public PointModel(final Point p) {
        this.id = p.getId();
        this.uuid = p.getUUID();

        this.lastChecked = p.getLastChecked().getTime();
        this.host = p.getHost();
        this.catID = p.getCatID();
        this.isSystemPoint = p.isSystemPoint();
        this.userFK = p.getUserFK();
        this.name = p.getName().getValue();
        this.createDate = p.getCreateDate().getTime();
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
        this.targetValue = p.getTargetValue();
        this.values = p.getValues();
        this.value = p.getValue();
        this.sendAlertAsJson = p.getSendAlertsAsJson();
        this.alarmToEmail = p.isAlarmToEmail();


        this.lastAlarmSent = p.getLastAlarmSent().getTime();

        if (p.getIntelligence() != null) {
            this.intelligence = IntelligenceModelFactory.createIntelligenceModel(p.getIntelligence());
        }

        if (p.getCalculation() != null) {
            this.calculation = PointModelFactory.createCalculation(p.getCalculation());
        }


    }

    public PointModel() {
    }

    public PointModel(final long userFK, final EntityName pointName, final long catID, final String uuid) {
        this();
        this.userFK = userFK;
        this.name = pointName.getValue();
        this.catID = catID;
        this.uuid = uuid;


    }

    public PointModel(final long userFK, final EntityName pointName, final String uuid) {
        this();
        this.userFK = userFK;
        this.name = pointName.getValue();
        this.uuid = uuid;
    }

    public PointModel(final EntityName name, final String uuid) {
        this.name = name.getValue();
        this.uuid = uuid;

    }

    public PointModel(final long id, final long userFk) {
        this.id = id;
        this.userFK = userFk;

    }
    // End Constructors

    private double targetValue;

    private List<Value> values;

    private Value value;

    @Override
    public Intelligence getIntelligence() {
        return intelligence;
    }

    @Override
    public void setIntelligence(final Intelligence intelligence) {
        this.intelligence = intelligence;
    }

    @Override
    public void setCalculation(Calculation calculation) {
        this.calculation = calculation;
    }

    @Override
    public int getAlarmDelay() {
        return alarmDelay;
    }

    @Override
    public boolean getAlarmToFacebook() {

        return alarmToFacebook;

    }

    @Override
    public long getCatID() {
        return catID;
    }

    @Override
    public double getCompression() {
        return this.compression;
    }

    @Override
    public Date getCreateDate() {
        return new Date(this.createDate);
    }

    @Override
    public String getDescription() {

        return this.description == null ? "" : this.description;
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
    public EntityName getName() {
        return CommonFactoryLocator.getInstance().createName(this.name);
    }

    @Override
    public boolean getSendAlarmIM() {

        return sendAlarmIM;
    }

    @Override
    public boolean getSendAlarmTweet() {

        return sendAlarmTweet;
    }

    @Override
    public boolean getSendIM() {

        return sendIM;
    }

    @Override
    public boolean getSendTweet() {

        return sendTweet;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public long getTarget() {
        return target;
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
    public long getX() {
        return X;
    }

    @Override
    public long getY() {
        return Y;
    }

    @Override
    public long getZ() {
        return Z;
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
    public boolean isPostToFacebook() {
        return postToFacebook;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
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
        this.createDate = createDate.getTime();
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
    public String toString() {
        return name;

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
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean getReadOnly() {

        return readOnly;
    }

    public ClientType getClientType() {
        return clientType;
    }

    @Override
    public boolean isAlarmToEmail() {
        return alarmToEmail;
    }

    @Override
    public void setAlarmToEmail(boolean alarmToEmail) {
        this.alarmToEmail = alarmToEmail;
    }

    @Override
    public boolean getSendAlertsAsJson() {
        return this.sendAlertAsJson;
    }

    @Override
    public void setSendAlertsAsJson(boolean sendAlertsAsJson) {
        this.sendAlertAsJson = sendAlertsAsJson;
    }

    @Override
    public Calculation getCalculation() {
        return this.calculation;
    }

    @Override
    public Date getLastAlarmSent() {
        return new Date(this.lastAlarmSent);
    }

    @Override
    public boolean isIdleAlarmOn() {
        return idleAlarmOn;
    }

    @Override
    public AlertType getAlertState() {
        return this.alertState;
    }

    @Override
    public void setAlertState(final AlertType alertState) {
        this.alertState = alertState;
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

    @Override
    public void setEntityType(EntityType entityType) {
       this.entityType = entityType.getCode();
    }


}
