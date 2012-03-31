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

package com.nimbits.client.service.summary;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.user.User;

public interface SummaryServiceAsync {
    void readSummary(Entity entity, AsyncCallback<Summary> async);

    void addUpdateSummary(Entity entity, Summary update, EntityName name, AsyncCallback<Entity> asyncCallback);

    void updateLastProcessed(Entity entity, AsyncCallback<Void> async);

    void deleteSummary(final User u, final Entity entity, AsyncCallback<Void> async);
}
