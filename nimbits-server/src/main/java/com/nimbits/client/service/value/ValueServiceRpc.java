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

package com.nimbits.client.service.value;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("valueService")
public interface ValueServiceRpc extends RemoteService {
    List<Value> solveEquationRpc(final User u, final Calculation calculation) throws Exception;
    Value recordValueRpc(final Entity entity, final Value value);
    Map<String, Entity> getCurrentValuesRpc(final Map<String, Point> entities) throws Exception;
    void createDataDumpRpc(Entity entity, Timespan timespan);

    static class App {
        private static ValueServiceRpcAsync ourInstance = GWT.create(ValueServiceRpc.class);

        public static synchronized ValueServiceRpcAsync getInstance() {
            return ourInstance;
        }
    }

}
