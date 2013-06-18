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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.service.calculation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 12:20 PM
 */
@RemoteServiceRelativePath("calculationService")
public interface CalculationService  extends RemoteService {



    List<Value> solveEquationRpc(final User u, final Calculation calculation) throws Exception;



    static class App {
        private static CalculationServiceAsync ourInstance = GWT.create(CalculationService.class);

        public static synchronized CalculationServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
