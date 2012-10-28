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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.exception.NimbitsException;
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
 * Time: 11:53 AM
 */
@RemoteServiceRelativePath("intelligenceService")
public interface IntelligenceService extends RemoteService {
    String getRawResult(final User user, final String query, final String podId, final boolean htmlOutput) throws NimbitsException;

    Value processInput(final User user, final Intelligence intelligence, final Point targetPoint, final String processedInput) throws NimbitsException;

    Map<String, String> getHTMLContent(final String responseXML);

    String addDataToInput(final User user, final Intelligence intelligence) throws NimbitsException;

    void processIntelligence(final User user, final Entity point) throws NimbitsException;

    Value processInput(final User user, final Intelligence update) throws NimbitsException;

    static class App {
        private static IntelligenceServiceAsync ourInstance = GWT.create(IntelligenceService.class);

        public static synchronized IntelligenceServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
