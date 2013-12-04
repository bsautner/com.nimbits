/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.sync;

import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.trigger.Trigger;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:59 AM
 */
public interface Sync  extends Trigger, Serializable {


    String getTargetInstance();

    void setTargetInstance(String targetInstance);

    String getTargetPoint();

    void setTargetPoint(String targetPoint);
}
