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

package com.nimbits.client.service.intelligence;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/29/11
 * Time: 11:54 AM
 */
public interface IntelligenceServiceAsync {


    void getRawResult(final String query, final String podId, final boolean htmlOutput, AsyncCallback<String> async);

    void getHTMLContent(final String responseXML, AsyncCallback<Map<String, String>> async);

    void getIntelligence(Entity entity, AsyncCallback<Intelligence> asyncCallback);

    void addUpdateIntelligence(Entity entity, EntityName name, Intelligence update, AsyncCallback<Entity> error);

    void processIntelligence(User u, Entity point, AsyncCallback<Void> async);

    void processInput(final Intelligence intelligence, final Point targetPoint, final String processedInput, AsyncCallback<Value> async);

    void addDataToInput(User user, Intelligence intelligence, AsyncCallback<String> async);

    void processInput(Intelligence update, AsyncCallback<Value> async);

    void deleteIntelligence(final User u, final Entity entity, AsyncCallback<Void> async);
}
