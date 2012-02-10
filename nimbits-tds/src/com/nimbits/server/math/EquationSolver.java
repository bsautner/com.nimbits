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

package com.nimbits.server.math;

import com.google.gwt.core.client.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.shared.*;


/**
 * @author BSautner uses http://lts.online.fr/dev/java/math.evaluator/
 *         MathEvaluator m = new MathEvaluator("-5-6/(-2) + sqr(15+x)");
 */
public class EquationSolver {

    public static double solveEquation(final Point point, final User u) throws NimbitsException {

        Double retVal;

        final MathEvaluator m = new MathEvaluator(point.getCalculation().getFormula());


        if (!(Utils.isEmptyString(point.getCalculation().getX())) && point.getCalculation().getFormula().contains("x")) {
            Point p = PointServiceFactory.getInstance().getPointByUUID(point.getCalculation().getX());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getNumberValue();

                m.addVariable("x", d);
            }
        }
        if (!(Utils.isEmptyString(point.getCalculation().getY())) && point.getCalculation().getFormula().contains("y")) {

            Point p = PointServiceFactory.getInstance().getPointByUUID(point.getCalculation().getY());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getNumberValue();
                m.addVariable("y", d);
            }

        }
        if (!(Utils.isEmptyString(point.getCalculation().getZ())) && point.getCalculation().getFormula().contains("z")) {
            Point p = PointServiceFactory.getInstance().getPointByUUID(point.getCalculation().getZ());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getNumberValue();
                m.addVariable("z", d);
            }
        }

        retVal = m.getValue();


        if (retVal == null) {
            GWT.log(point.getName() + " formula returned a null value: " + point.getCalculation().getFormula());
            throw new NimbitsException("Formula returned a null value: " + point.getCalculation().getFormula());


        }


        return retVal;
    }

}
