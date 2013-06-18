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

package com.nimbits.client.service.intelligence;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/29/11
 * Time: 11:54 AM
 */
public interface IntelligenceServiceAsync {


    void getRawResult(final User user, final String query, final String podId, final boolean htmlOutput, AsyncCallback<String> async);

    void getHTMLContent(final String responseXML, AsyncCallback<Map<String, String>> async);

    void processIntelligence(User u, Entity point, AsyncCallback<Void> async);

    void processInput(final User user, final Intelligence intelligence, final Point targetPoint, final String processedInput, AsyncCallback<Value> async);

    void addDataToInput(User user, Intelligence intelligence, AsyncCallback<String> async);

    void processInput(final User user, Intelligence update, AsyncCallback<Value> async);


}
