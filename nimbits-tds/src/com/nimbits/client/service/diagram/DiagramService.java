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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.DiagramNotFoundException;
import com.nimbits.client.exceptions.ObjectProtectionException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.diagram.DiagramName;
import com.nimbits.client.model.user.User;

import java.util.Map;
import java.util.Set;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 5:38 PM
 */
@RemoteServiceRelativePath(Const.PARAM_DIAGRAM)
public interface DiagramService extends RemoteService {
    String getBlobStoreUrl(String url);

    void moveDiagram(final DiagramName diagramName, final CategoryName targetCategoryName) throws NimbitsException;

    void deleteDiagram(final Diagram diagram);

    Map<DiagramName, Diagram> getDiagramsByName(final long diagramOwnerId, final Set<DiagramName> names) throws NimbitsException;

    Diagram updateDiagram(final Diagram diagram) throws NimbitsException;

    Diagram getDiagramByUuid(final String diagramUUID) throws ObjectProtectionException, DiagramNotFoundException, NimbitsException;

    boolean checkDiagramProtection(final User loggedInUser, final User diagramOwner, final Diagram d);
}
