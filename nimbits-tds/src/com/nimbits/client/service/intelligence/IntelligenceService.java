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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.enums.IntelligenceResultTarget;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/29/11
 * Time: 11:53 AM
 */
@RemoteServiceRelativePath("intel")
public interface IntelligenceService extends RemoteService {
    String getRawResult(final String query, final String podId, final boolean htmlOutput) throws NimbitsException;

    String processInput(Point point, String input, String value, IntelligenceResultTarget intelligenceResultTarget, PointName targetPointName, boolean getPlainText) throws NimbitsException;

    Value processInput(final Point point, final Point targetPoint, final String processedInput) throws NimbitsException;

    Map<String, String> getHTMLContent(final String responseXML);

    String addDataToInput(User u, Point point) throws NimbitsException;

}
