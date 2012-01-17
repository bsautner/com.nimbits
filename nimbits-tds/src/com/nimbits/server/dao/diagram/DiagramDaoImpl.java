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

package com.nimbits.server.dao.diagram;

import com.google.appengine.api.blobstore.*;
import com.nimbits.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.diagram.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.diagram.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.pointcategory.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 4:14 PM
 */
public class DiagramDaoImpl implements DiagramTransactions {
    private User user;

    public DiagramDaoImpl(User u) {
        this.user = u;
    }
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<Diagram> getDiagramsByCategory(final Category c ) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Diagram> diagrams;
        try {
            long userFK = user.getId();
            final Query q = pm.newQuery(DiagramEntity.class, "userFk == k && categoryFk  == c");
            q.declareParameters("Long k, Long c");
            q.setOrdering("name ascending");
            diagrams = (List<Diagram>) q.execute(userFK, c.getId());
            return DiagramModelFactory.createDiagramModels(diagrams);
        } finally {
            pm.close();
        }

    }

    @Override
    public void moveDiagram(final DiagramName diagramName,
                            final CategoryName newCategoryName) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Category c = CategoryServiceFactory.getInstance().getCategory(user, newCategoryName);

        if (!(c == null)) {
            Transaction tx = null;
            try {
                tx = pm.currentTransaction();
                tx.begin();
                Query q1 = pm.newQuery(DiagramEntity.class, "userFk==u && name==p");
                q1.declareParameters("Long u, String p");
                q1.setRange(0, 1);
                List<DiagramEntity> diagrams = (List<DiagramEntity>) q1.execute(user.getId(),
                        diagramName.getValue());
                if (diagrams.size() > 0) {
                    DiagramEntity diagram = diagrams.get(0);
                    diagram.setCategoryFk(c.getId());
                    tx.commit();
                } else {
                    tx.rollback();
                }
                //PointCacheManager.remove(point);
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                throw new NimbitsRuntimeException("Error Moving Diagram", e);

            } finally {
                pm.close();
            }
        }
    }

    @Override
    public void deleteDiagram(final Diagram diagram) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Transaction tx;

        try {

            tx = pm.currentTransaction();
            tx.begin();
            DiagramEntity e = pm.getObjectById(DiagramEntity.class, diagram.getId());
            if (e != null) {
                pm.deletePersistent(e);
            }

            tx.commit();


            //PointCacheManager.remove(point);

        } catch (Exception ignored) {


        } finally {
            pm.close();
        }

    }


    @Override
    public void addDiagram(final BlobKey blobKey, final DiagramName name) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        final CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(Const.CONST_HIDDEN_CATEGORY);
        Category targetCategory =CategoryServiceFactory.getInstance().getCategory(user, categoryName);

        if (targetCategory == null) {
            targetCategory = CategoryServiceFactory.getInstance().createHiddenCategory(user);
        }

        final DiagramEntity d = new DiagramEntity(user, blobKey, name, targetCategory);

        pm.makePersistent(d);
        pm.close();
    }


    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public Diagram getDiagramByName(DiagramName name) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<DiagramEntity> points;
        Diagram retObj = null;


        try {

            Query q = pm.newQuery(DiagramEntity.class, "userFk==u && name==p");
            q.declareParameters("Long u, String p");
            q.setRange(0, 1);
            points = (List<DiagramEntity>) q.execute(user.getId(), name.getValue());
            if (points.size() > 0) {
                DiagramEntity result = points.get(0);
                retObj = DiagramModelFactory.createDiagramModel(result);
                //DiagramCacheManager.put(retObj);

            }

        } catch (Exception e) {
            retObj = null;
        } finally {
            pm.close();
        }

        return retObj;
    }

    @Override
    public Diagram updateDiagram(Diagram diagram) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx;
        Diagram retObj = null;
        try {
            tx = pm.currentTransaction();
            tx.begin();
            DiagramEntity e = pm.getObjectById(DiagramEntity.class, diagram.getId());
            if (e != null) {
                e.setProtectionLevel(diagram.getProtectionLevel());
                tx.commit();
                retObj = DiagramModelFactory.createDiagramModel(e);
            } else {
                tx.rollback();
            }
        } finally {
            pm.close();
        }
        return retObj;

    }

    @Override
    public Diagram getDiagramByUuid(final String uuid) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<DiagramEntity> results;
        Diagram retObj = null;
        try {
            final Query q = pm.newQuery(DiagramEntity.class, "uuid==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            results = (List<DiagramEntity>) q.execute(uuid);
            if (results.size() > 0) {
                DiagramEntity result = results.get(0);
                retObj = DiagramModelFactory.createDiagramModel(result);

            }

        } catch (Exception e) {
            retObj = null;
        } finally {
            pm.close();
        }


        return retObj;
    }

    @Override
    public Diagram updateDiagram(final BlobKey blobKey, final DiagramName name, final long id) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx;
        Diagram retObj = null;
        try {

            tx = pm.currentTransaction();
            tx.begin();
            DiagramEntity e = pm.getObjectById(DiagramEntity.class, id);
            if (e != null) {
                e.setBlobKey(blobKey);
                e.setName(name);

                tx.commit();
                retObj = DiagramModelFactory.createDiagramModel(e);
            } else {
                tx.rollback();
            }

        } finally {
            pm.close();
        }
        return retObj;
    }

}
