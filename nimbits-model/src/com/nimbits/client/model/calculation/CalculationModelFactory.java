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

package com.nimbits.client.model.calculation;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 10:04 AM
 */
public class CalculationModelFactory {

    private CalculationModelFactory() {
    }

    public static Calculation createCalculation(Calculation calculation) throws NimbitsException {
        return new CalculationModel(calculation);
    }

    public static Calculation createCalculation(final String trigger, final boolean enabled, final String f, final String target, final String x, final String y, final String z) {


        return new CalculationModel(trigger,  enabled, f, target,  x,y, z);
    }

    public static List<Calculation> createCalculations(Collection<Calculation> calculations) throws NimbitsException {
        List<Calculation> retObj = new ArrayList<Calculation>(calculations.size());
        for (final Calculation c : calculations) {
            retObj.add(createCalculation(c));
        }
        return retObj;


    }


    public static Calculation createCalculation(Entity entity, final String trigger, final boolean enabled, final String f, final String target, final String x, final String y, final String z) throws NimbitsException {
        return new CalculationModel(entity, trigger,  enabled, f, target,  x,y, z);

    }
}
