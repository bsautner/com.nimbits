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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 10:04 AM
 */
public class CalculationModelFactory {

    public static Calculation createCalculation(Calculation calculation) {
        return new CalculationModel(calculation);
    }

    public static Calculation createCalculation(final String trigger, final String uuid, final boolean enabled, final String f, final String target, final String x, final String y, final String z) {


        return new CalculationModel(trigger, uuid, enabled, f, target,  x,y, z);
    }

    public static List<Calculation> createCalculations(List<Calculation> calculations) {
        List<Calculation> retObj = new ArrayList<Calculation>();
        for (final Calculation c : calculations) {
            retObj.add(createCalculation(c));
        }
        return retObj;


    }


}
