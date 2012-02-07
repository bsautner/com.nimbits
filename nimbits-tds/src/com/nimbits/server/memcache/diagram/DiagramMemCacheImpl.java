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

package com.nimbits.server.memcache.diagram;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.diagram.DiagramTransactionFactory;
import com.nimbits.server.diagram.DiagramTransactions;
import com.nimbits.server.pointcategory.CategoryTransactionFactory;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/3/12
 * Time: 2:43 PM
 */
public class DiagramMemCacheImpl implements DiagramTransactions {
    private User user;

    public DiagramMemCacheImpl(User u) {
        this.user = u;
    }

    @Override
    public void addDiagram(final BlobKey blobKey,final EntityName name) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        DiagramTransactionFactory.getDaoInstance(user).addDiagram(blobKey, name);
    }

    @Override
    public List<Diagram> getDiagramsByCategory(final Category c) {
        return  DiagramTransactionFactory.getDaoInstance(user).getDiagramsByCategory(c);
    }

    @Override
    public void moveDiagram(final EntityName diagramName, final EntityName newEntityName) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        DiagramTransactionFactory.getDaoInstance(user).moveDiagram(diagramName, newEntityName);
    }

    @Override
    public void deleteDiagram(final Diagram diagram) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        DiagramTransactionFactory.getDaoInstance(user).deleteDiagram(diagram);
    }

    @Override
    public Diagram getDiagramByName(final EntityName name) {
        return  DiagramTransactionFactory.getDaoInstance(user).getDiagramByName(name);
    }

    @Override
    public Diagram updateDiagram(final Diagram diagram) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        return DiagramTransactionFactory.getDaoInstance(user).updateDiagram(diagram);
    }

    @Override
    public Diagram getDiagramByUuid(final String uuid) {
        return  DiagramTransactionFactory.getDaoInstance(user).getDiagramByUuid(uuid);
    }

    @Override
    public Diagram updateDiagram(final BlobKey blobKey, final EntityName diagramName, final long id) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        return  DiagramTransactionFactory.getDaoInstance(user).updateDiagram(blobKey, diagramName, id);
    }
}
