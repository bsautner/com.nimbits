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

package com.nimbits.cloudplatform.client.service.value;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("valueService")
public interface ValueService extends RemoteService {


    int preloadTimespanRpc(final Entity entity, final Timespan timespan) throws Exception;

    List<Value> getCacheRpc(final Entity entity) ;


    List<Value> getPieceOfDataSegmentRpc(final Entity entity, final Timespan timespan, final int start, final int end) throws Exception;


    Value recordValueRpc(final Entity entity, final Value value);



    Map<String, Entity> getCurrentValuesRpc(final Map<String, Point> entities) throws Exception;




    void createDataDumpRpc(Entity entity, Timespan timespan);

    List<Value> getTopDataSeriesRpc(Entity baseEntity, int i, Date date) throws Exception;


    static class App {
        private static ValueServiceAsync ourInstance = GWT.create(ValueService.class);

        public static synchronized ValueServiceAsync getInstance() {
            return ourInstance;
        }
    }

}
