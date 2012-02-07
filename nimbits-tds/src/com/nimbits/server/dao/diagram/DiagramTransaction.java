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

package com.nimbits.server.dao.diagram;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.entity.EntityName;

import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 4:13 PM
 */
public interface DiagramTransaction {


    void addDiagram(final BlobKey blobKey,
                    final EntityName name) throws NimbitsException;

    List<Diagram> getDiagramsByCategory(final Category c);

    void moveDiagram(final EntityName diagramName,
                     final EntityName newEntityName) throws NimbitsException;

    void deleteDiagram(final Diagram diagram) throws NimbitsException;

    Diagram getDiagramByName(final EntityName name);

    Diagram updateDiagram(final Diagram diagram) throws NimbitsException;

    Diagram getDiagramByUuid(final String uuid);

    Diagram updateDiagram(final BlobKey blobKey,
                          final EntityName diagramName,
                          final long id) throws NimbitsException;

}
