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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.diagram;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.DiagramNotFoundException;
import com.nimbits.client.exceptions.ObjectProtectionException;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.diagram.DiagramName;
import com.nimbits.client.model.user.User;

import java.util.Map;
import java.util.Set;

public interface DiagramServiceAsync {
    void getBlobStoreUrl(final String url, final AsyncCallback<String> async);

    void moveDiagram(final DiagramName diagramName, final CategoryName targetCategoryName, final AsyncCallback<Void> asyncCallback) throws NimbitsException;

    void deleteDiagram(final Diagram diagram, final AsyncCallback<Void> asyncCallback);

    void getDiagramsByName(final long diagramOwnerId, final Set<DiagramName> names, final AsyncCallback<Map<DiagramName, Diagram>> async) throws NimbitsException;

    void updateDiagram(final Diagram diagram, final AsyncCallback<Diagram> asyncCallback) throws NimbitsException;

    void getDiagramByUuid(final String diagram, final AsyncCallback<Diagram> asyncCallback) throws ObjectProtectionException, DiagramNotFoundException, NimbitsException;

    void checkDiagramProtection(final User loggedInUser, final User diagramOwner, final Diagram d, final AsyncCallback<Boolean> async);
}
