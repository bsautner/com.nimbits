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
 * Time: 11:53 AM
 */
@RemoteServiceRelativePath("intel")
public interface IntelligenceService extends RemoteService {
    String getRawResult(final String query, final String podId, final boolean htmlOutput) throws NimbitsException;

    Value processInput(final Intelligence intelligence, final Point targetPoint, final String processedInput) throws NimbitsException;

    Map<String, String> getHTMLContent(final String responseXML);

    String addDataToInput(final User user, final Intelligence intelligence) throws NimbitsException;

    Intelligence getIntelligence(final Entity entity);

    Entity addUpdateIntelligence(final Entity entity, final EntityName name, final Intelligence update) throws NimbitsException;

    void processIntelligence(final User u, final Point point) throws NimbitsException;

    Value processInput(final Intelligence update) throws NimbitsException;

    void deleteIntelligence(final User u, final Entity entity);
}
