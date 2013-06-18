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

package com.nimbits.cloudplatform.client.model.point;

import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.entity.*;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:04 PM
 */
public interface Point extends Entity, Serializable {


    int getExpire();

    double getHighAlarm();

    boolean getIdleAlarmSent();

    int getIdleSeconds();

    double getLowAlarm();

    String getUnit();

    Value getValue();

    List<Value> getValues();

    boolean isHighAlarmOn();

    boolean isIdleAlarmOn();

    boolean isLowAlarmOn();

    void setExpire(final int expire);

    void setHighAlarm(final double highAlarm);

    void setHighAlarmOn(final boolean highAlarmOn);

    void setIdleAlarmOn(boolean idleAlarmOn);

    void setIdleAlarmSent(final boolean idleAlarmSent);

    void setIdleSeconds(int idleSeconds);

    void setLowAlarm(final double lowAlarm);

    void setLowAlarmOn(final boolean lowAlarmOn);

    void setUnit(final String unit);

    void setValue(final Value value);

    void setValues(final List<Value> values);

    FilterType getFilterType();

    void setFilterType(FilterType filterType);

    double getFilterValue();

    void setFilterValue(double value);

    boolean inferLocation();

    void setInferLocation(boolean inferLocation);

    PointType getPointType();

    void setPointType(PointType type);

    double getDeltaAlarm();

    void setDeltaAlarm(double deltaAlarm);

    boolean isDeltaAlarmOn();

    void setDeltaAlarmOn(boolean deltaAlarmOn);

    int getDeltaSeconds();

    void setDeltaSeconds(int deltaSeconds);
}
