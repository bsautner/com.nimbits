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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.List;

public interface CalculationServiceAsync {
    void getCalculation(final Entity entity, final AsyncCallback<Calculation> async);

    void addUpdateCalculation(final Entity entity, final EntityName name, final Calculation calculation, final AsyncCallback<Entity> async);

    void solveEquation(final Calculation calculation, final AsyncCallback<Value> async);

    void getCalculations(final Entity entity, final AsyncCallback<List<Calculation>> async) throws NimbitsException;

    void processCalculations(final User u, final Entity point, final Value value, final AsyncCallback<Void> async);

    void addUpdateCalculation(final User u, final Entity entity, final EntityName name, final Calculation calculation, final AsyncCallback<Entity> async);

    void deleteCalculation(final User u, final Entity entity, final AsyncCallback<Void> async);
}
