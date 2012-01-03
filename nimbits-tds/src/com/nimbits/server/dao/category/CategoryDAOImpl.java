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

package com.nimbits.server.dao.category;

import com.nimbits.PMF;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModelFactory;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.server.diagram.DiagramModelFactory;
import com.nimbits.server.orm.DataPoint;
import com.nimbits.server.orm.DiagramEntity;
import com.nimbits.server.orm.PointCatagory;
import com.nimbits.server.pointcategory.CategoryTransactions;
import com.nimbits.server.task.TaskFactoryLocator;
import com.nimbits.shared.Utils;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.*;
import java.util.logging.Logger;

public class CategoryDAOImpl implements CategoryTransactions {

    private static final Logger log = Logger.getLogger(CategoryDAOImpl.class.getName());
    private final User user;

    public CategoryDAOImpl(User u) {
        user = u;
    }

    @Override
    public void purgeMemCache() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    /* (non-Javadoc)
    * @see com.nimbits.server.pointcategory.CategoryDAO#createHiddenCategory(com.nimbits.client.model.user.NimbitsUser)
    */
    @Override
    public Category createHiddenCategory() {


        PersistenceManager pm = PMF.get().getPersistenceManager();

        Category retObj;
        try {

            final CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(Const.CONST_HIDDEN_CATEGORY);
            Category c = new PointCatagory();
            c.setName(categoryName);
            c.setProtectionLevel(ProtectionLevel.onlyMe);
            c.setUUID(UUID.randomUUID().toString());
            c.setUserFK(user.getId());
            pm.makePersistent(c);

            retObj = CategoryModelFactory.createCategoryModel(c);
        } finally {
            pm.close();
        }


        return retObj;

    }

    /* (non-Javadoc)
      * @see com.nimbits.server.pointcategory.CategoryDAO#getCategories(com.nimbits.client.model.user.NimbitsUser, boolean)
      */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<Category> getCategories(final boolean includePoints, final boolean includeDiagrams) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final LinkedList<Category> retObj = new LinkedList<Category>();
        final Query q = pm.newQuery(PointCatagory.class, "userFK == u");

        List<Category> result;
        long userFK = user.getId();

        q.setOrdering("name ascending");
        q.declareParameters("Long u");

        try {
            result = (List<Category>) q.execute(userFK);
            final Map<Long, List<Point>> points = includePoints ? getPointsByCategoryList(result) : null;
            final Map<Long, List<Diagram>> diagrams = includeDiagrams ? getDiagramsByCategoryList(result) : null;

            for (final Category c : result) {
                if (points != null && includePoints) {
                    c.setPoints(points.get(c.getId()));
                }
                if (diagrams != null && includeDiagrams) {
                    c.setDiagrams(diagrams.get(c.getId()));
                }
            }


            for (final Category jdoCategory : result) {
                final Category r = CategoryModelFactory.createCategoryModel(jdoCategory);
                retObj.add(r);
            }

        } finally {
            pm.close();
        }
        return retObj;


    }


    private Map<Long, List<Point>> getPointsByCategoryList(final List<Category> categories) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        Map<Long, List<Point>> retObj = null;

        List<Long> ids = new ArrayList<Long>();
        for (Category c : categories) {
            ids.add(c.getId());
        }
        try {
            final Query q = pm.newQuery(DataPoint.class, ":p.contains(catID)");
            final List<Point> points = (List<Point>) q.execute(ids);

            List<Point> models = PointModelFactory.createPointModels(points);
            retObj = new HashMap<Long, List<Point>>();
            for (Point p : models) {
                if (!retObj.containsKey(p.getCatID())) {
                    retObj.put(p.getCatID(), new ArrayList<Point>());
                }
                retObj.get(p.getCatID()).add(p);
            }
        } finally {
            pm.close();
        }

        return retObj;
    }

    private Map<Long, List<Diagram>> getDiagramsByCategoryList(final List<Category> categories) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        Map<Long, List<Diagram>> retObj = null;

        List<Long> ids = new ArrayList<Long>();
        for (Category c : categories) {
            ids.add(c.getId());
        }
        try {
            final Query q = pm.newQuery(DiagramEntity.class, ":p.contains(categoryFk)");
            final List<Diagram> diagrams = (List<Diagram>) q.execute(ids);

            List<Diagram> models = DiagramModelFactory.createDiagramModels(diagrams);

            retObj = new HashMap<Long, List<Diagram>>();
            for (Diagram p : models) {
                if (!retObj.containsKey(p.getCategoryFk())) {
                    retObj.put(p.getCategoryFk(), new ArrayList<Diagram>());
                }
                retObj.get(p.getCategoryFk()).add(p);
            }
        } finally {
            pm.close();
        }

        return retObj;
    }

    /* (non-Javadoc)
    * @see com.nimbits.server.pointcategory.CategoryDAO#getCategory(java.lang.String, long)
    */
    @Override
    public Category getCategory(final CategoryName categoryName) {
        Category retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(PointCatagory.class, "name==u && userFK==l");
        q1.declareParameters("String u, Long l");
        q1.setRange(0, 1);

        try {
            @SuppressWarnings(Const.WARNING_UNCHECKED)
            final List<PointCatagory> c = (List<PointCatagory>) q1.execute(categoryName.getValue(),
                    user.getId());
            if (c.size() > 0) {
                retObj = CategoryModelFactory.createCategoryModel(c.get(0));
            }
        } finally {
            pm.close();
        }


        return retObj;
    }


    @Override
    public Category getCategory(final long id) {
        Category retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(PointCatagory.class, "id==i");
        q1.declareParameters("Long i");
        q1.setRange(0, 1);

        try {
            @SuppressWarnings(Const.WARNING_UNCHECKED)
            final List<PointCatagory> c = (List<PointCatagory>) q1.execute(id);
            if (c.size() > 0) {
                retObj = CategoryModelFactory.createCategoryModel(c.get(0));
            }
        } finally {
            pm.close();
        }


        return retObj;
    }

    /* (non-Javadoc)
      * @see com.nimbits.server.pointcategory.CategoryDAO#categoryExists(com.nimbits.client.model.user.NimbitsUser, java.lang.String)
      */
    @Override
    public boolean categoryExists(final CategoryName CategoryName) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }


    @Override
    public Category addCategory(final CategoryName categoryName) {
        Category retObj;

        PersistenceManager pm = PMF.get().getPersistenceManager();
        PointCatagory c;

        try {
            long userFK = user.getId();
            c = new PointCatagory(categoryName);
            c.setProtectionLevel(ProtectionLevel.everyone);
            c.setUserFK(userFK);
            c.setUUID(UUID.randomUUID().toString());
            pm.makePersistent(c);
            retObj = CategoryModelFactory.createCategoryModel(c);
        } finally {
            pm.close();
        }
        return retObj;
    }


    /* (non-Javadoc)
      * @see com.nimbits.server.pointcategory.CategoryDAO#deleteCategory(com.nimbits.client.model.PointCatagory)
      */
    @Override
    public void deleteCategory(final Category c) {
        List<PointCatagory> cats;

        if (c == null) {
            return;

        }
        Transaction tx;

        PersistenceManager pm = PMF.get().getPersistenceManager();
        long catID = 0;

        // List<String> symbols = new ArrayList<String>();
        try {
            tx = pm.currentTransaction();
            tx.begin();

            Query q = pm.newQuery(PointCatagory.class, "id==k");

            q.declareParameters("String k");
            q.setRange(0, 1);
            cats = (List<PointCatagory>) q.execute(c.getId());
            if (cats.size() > 0) {
                catID = cats.get(0).getId();
                pm.deletePersistent(cats.get(0));

            }


            tx.commit();

            //delete points
            if (catID > 0) {
                List<DataPoint> points;
                //	ArrayList<DataPoint> retObj = new ArrayList<DataPoint>();
                try {
                    Query q4 = pm.newQuery(DataPoint.class, "catID == c");
                    q4.declareParameters("Long c");
                    points = (List<DataPoint>) q4.execute(catID);
                    for (Point dp : points) {
                        //PointCacheManager.remove(dp);
                        TaskFactoryLocator.getInstance().startDeleteDataTask(dp.getId(), false, 0, dp.getName());

                    }
                    pm.deletePersistentAll(points);
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
            }


        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }


    }

    @Override
    public Category updateCategory(final Category update) {
        Transaction tx;

        PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            tx = pm.currentTransaction();
            tx.begin();

            PointCatagory original = pm.getObjectById(PointCatagory.class, update.getId());
            original.setDescription(update.getDescription());
            original.setName(update.getName());
            original.setProtectionLevel(update.getProtectionLevel());
            if (Utils.isEmptyString(original.getUUID())) {
                original.setUUID(UUID.randomUUID().toString());
            } else {
                original.setUUID(update.getUUID());
            }


            tx.commit();
            return CategoryModelFactory.createCategoryModel(original);
        } catch (Exception ex) {
            return null;
        } finally {
            pm.close();
        }

    }

    @Override
    public Category getCategoryByUUID(final String uuidParam) {
        Category retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(PointCatagory.class, "uuid==i");
        q1.declareParameters("String i");
        q1.setRange(0, 1);

        try {
            @SuppressWarnings(Const.WARNING_UNCHECKED)
            final List<PointCatagory> c = (List<PointCatagory>) q1.execute(uuidParam);
            if (c.size() > 0) {
                retObj = CategoryModelFactory.createCategoryModel(c.get(0));
            }
        } finally {
            pm.close();
        }


        return retObj;
    }


}
