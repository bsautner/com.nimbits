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

package com.nimbits.cloudplatform.client.model.calculation;

import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.trigger.TargetEntity;
import com.nimbits.cloudplatform.client.model.trigger.TriggerEntity;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 10:04 AM
 */
public class CalculationModelFactory {

    private CalculationModelFactory() {
    }

    public static Calculation createCalculation(Calculation calculation)  {
        return new CalculationModel(calculation);
    }

    public static Calculation createCalculation(Entity entity, final TriggerEntity trigger, final boolean enabled, final String f, final TargetEntity target, final String x, final String y, final String z)  {
        return new CalculationModel(entity, trigger,  enabled, f, target,  x,y, z);

    }
}
