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

package com.nimbits.cloudplatform.server.transactions.calculation;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.service.calculation.CalculationService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 12:21 PM
 */
@Service("calculationService")
public class CalculationServiceImpl extends RemoteServiceServlet implements CalculationService {

    @Override
    public List<Value> solveEquationRpc(final User user, final Calculation calculation)  {
        return CalculationTransaction.solveEquation(user, calculation);
    }


}
