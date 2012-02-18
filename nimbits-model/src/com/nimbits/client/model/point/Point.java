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

import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.value.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:04 PM
 */
public interface Point extends Serializable {

    Intelligence getIntelligence();

    void setIntelligence(final Intelligence intelligence);

    double getCompression();

    Date getCreateDate();

    Date getLastChecked();

    int getExpire();

    double getHighAlarm();

    String getHost();

    long getId();

    boolean getIdleAlarmSent();

    int getIdleSeconds();

    boolean getIgnoreIncomingCompressedValues();

    double getLowAlarm();

    String getTag();

    double getTargetValue();

    String getUnit();

    long getUserFK();

    String getUUID();

    Value getValue();

    List<Value> getValues();

    boolean isHighAlarmOn();

    boolean isIdleAlarmOn();

    boolean isLowAlarmOn();

    void setCompression(final double compression);

    void setCreateDate(final Date createDate);

    void setExpire(final int expire);

    void setHighAlarm(final double highAlarm);

    void setHighAlarmOn(final boolean highAlarmOn);

    void setHost(final String host);

    void setId(final long id);

    void setIdleAlarmOn(boolean idleAlarmOn);

    void setIdleAlarmSent(final boolean idleAlarmSent);

    void setIdleSeconds(int idleSeconds);

    void setIgnoreIncomingCompressedValues(final boolean ignoreIncomingCompressedValues);

    void setLastChecked(final Date lastChecked);

    void setLowAlarm(final double lowAlarm);

    void setLowAlarmOn(final boolean lowAlarmOn);

    void setTag(final String tag);

    void setTargetValue(final double targetValue);

    void setUnit(final String unit);

    void setUserFK(final long userFK);

    void setUuid(final String uuid);

    void setValue(final Value value);

    void setValues(final List<Value> values);




}
