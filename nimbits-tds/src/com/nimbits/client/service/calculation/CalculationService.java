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

package com.nimbits.client.service.calculation;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 12:20 PM
 */
@RemoteServiceRelativePath("calculation")
public interface CalculationService  extends RemoteService {


    Calculation getCalculation(final Entity entity);

    Entity addUpdateCalculation(final Entity entity, final EntityName name, final Calculation calculation) throws NimbitsException;

    Value solveEquation(final Calculation calculation) throws NimbitsException;

    List<Calculation> getCalculations(final Entity entity);

    void processCalculations(final User u, final Entity point, final Value value) throws NimbitsException;

    Entity addUpdateCalculation(final User u, final Entity entity, final EntityName name, final Calculation calculation) throws NimbitsException;

    void deleteCalculation(final User u, final Entity entity);
}
