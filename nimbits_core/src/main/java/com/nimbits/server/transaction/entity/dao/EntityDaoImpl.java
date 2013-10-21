/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transaction.entity.dao;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionFactory;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.summary.SummaryModelFactory;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.orm.SubscriptionEntity;
import com.nimbits.server.orm.validation.RecursionValidation;
import com.nimbits.server.transaction.entity.EntityHelper;
import com.nimbits.shared.Utils;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import javax.jdo.*;
import java.util.*;


@SuppressWarnings("unchecked")
public class EntityDaoImpl implements EntityDao {

    private static final int INT = 1024;
    private final PersistenceManagerFactory pmf;
    private final NimbitsEngine engine;

    public EntityDaoImpl(NimbitsEngine engine) {
        this.pmf = engine.getPmf();
        this.engine = engine;
    }


    @Override
    public List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Query q = pmf.getPersistenceManager().newQuery(SubscriptionEntity.class);
            q.setFilter("subscribedEntity==p && enabled==e");
            q.declareParameters("String p, Boolean e");
            final Collection<Entity> results = (Collection<Entity>) q.execute(subscribedEntity.getKey(), true);
            return createModels(user, results);
        } finally {
            pm.close();
        }
    }

    @Override
    public List<Entity> getIdleEntities(User admin) {
        final PersistenceManager pm = pmf.getPersistenceManager();

        try {

            final Query q = pm
                    .newQuery(PointEntity.class);
            q.setFilter("idleAlarmOn == k && idleAlarmSent  == c");
            q.declareParameters("Long k, Long c");

            final Collection<Entity> points = (Collection<Entity>) q.execute(true, false);
            return createModels(admin, points);
        } finally {
            pm.close();
        }
    }


    @Override
    public List<Entity> getEntityByTrigger(final User user, final Entity entity, final Class<?> cls)  {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Query q = pm.newQuery(cls);
            q.setFilter("trigger == k && enabled == true");
            q.declareParameters("String k");
            final Collection<Entity> results = (Collection<Entity>) q.execute(entity.getKey());
            return createModels(user, results);
        } finally {
            pm.close();
        }
    }


    @Override
    public  Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) {


        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Query q1;
            try {
                q1 = pm.newQuery(Class.forName(type.getClassName()));
            } catch (ClassNotFoundException e) {

                return Collections.emptyMap();
            }

            q1.setFilter("owner==b && entityType==t");
            q1.declareParameters("String b, Integer t");
            q1.setRange(0, limit);


            final Collection<Entity> result = (Collection<Entity>) q1.execute(user.getKey(), type.getCode());

            final Map<String, Entity> retObj = new HashMap<String, Entity>(result.size());
            for (final Entity e : result) {
                final List<Entity> models = createModel(user, e);
                if (!models.isEmpty()) {
                    Entity model = models.get(0);
                    retObj.put(model.getKey(), model);
                }
            }
            return retObj;


        } finally {
            pm.close();
        }


    }



    @Override
    public List<Entity> getChildren(final User user, final List<Entity> parents)   {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final List<Entity> r = new ArrayList<Entity>(INT);

            final List<Entity> result = getEntityChildren(pm, parents);
            r.addAll(result);

            return createModels(user, r);
        } finally {
            pm.close();
        }

    }


    @Override
    public List<Entity> addUpdateEntity(final User user, final List<Entity> sample)   {

        final PersistenceManager pm = pmf.getPersistenceManager();

        try {

            final List<Entity> retObj;
            if (! sample.isEmpty()) {

                Entity entity = sample.get(0);
                if (Utils.isEmptyString(entity.getKey())) {

                    retObj = addEntity(user, sample, pm);
                } else {


                    final Class cls = Class.forName(entity.getEntityType().getClassName());
                    final Entity result = (Entity) pm.getObjectById(cls, entity.getKey());

                    if (result != null) {
                        final Transaction tx = pm.currentTransaction();
                        tx.begin();

                        result.update(entity);
                        result.validate(user);

                        tx.commit();

                        final List<Entity> model = createModel(user, result);
                        if (model.isEmpty()) {

                            throw new IllegalArgumentException("error creating model");

                        } else {
                            retObj = model;
                        }
                    } else {

                        retObj = addEntity(user, Arrays.asList(entity), pm);
                    }

                }


                return retObj;
            }
            else {
                return Collections.emptyList();
            }
        } catch (JDOObjectNotFoundException e) {

            return addEntity(user, sample, pm);

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } finally {
            pm.close();
        }


    }

    private List<Entity> addEntity(final User user, final List<Entity> sample, final PersistenceManager pm)  {
        if (! sample.isEmpty()) {
            Entity entity = sample.get(0);
            if (entity.getEntityType().isUniqueNameFlag()) {
                checkDuplicateEntity(user, Arrays.asList(entity));

            }
            final Entity commit = EntityHelper.downcastEntity(entity);
            if (!commit.getEntityType().equals(EntityType.user)) {
                commit.validate(user);
                if (commit.getEntityType().isTrigger()) {
                    new RecursionValidation(engine).validate(user, (Trigger) commit);
                }

            }

            if (Utils.isEmptyString(commit.getUUID())) {
                commit.setUUID(UUID.randomUUID().toString());
            }

            commit.setDateCreated(new Date());
            pm.makePersistent(commit);

//            if (entity.getEntityType().equals(EntityType.user)) {
//                final Transaction tx = pm.currentTransaction();
//                tx.begin();
//                entity.setParent(entity.getKey());
//                entity.setOwner(entity.getKey());
//
//                //entity.validate();
//                tx.commit();
//
//            }
            final List<Entity> r = createModel(user, commit);

            if (r.isEmpty()) {
                throw new IllegalArgumentException("Error creating model");
            } else {
                return r;
            }
        }
        else {
            return Collections.emptyList();
        }
    }


    @Override
    public List<Entity> getEntities(final User user) {

        final PersistenceManager pm = pmf.getPersistenceManager();


        final Collection<String> ownerKeys = new ArrayList<String>(1);
        ownerKeys.add(user.getKey());


        try {
            final List<Entity> retObj = new ArrayList<Entity>(INT);


            for (final EntityType type : EntityType.values()) {
                if (type.isTreeGridItem()) {
                    final Query q1;
                    try {
                        q1 = pm.newQuery(Class.forName(type.getClassName()), ":p.contains(owner)");
                    } catch (ClassNotFoundException e) {
                        return Collections.emptyList();
                    }
                    final Collection<Entity> result = (Collection<Entity>) q1.execute(ownerKeys);

                    final List<Entity> entities = createModels(user, result);
                    for (final Entity entity1 : entities) {


                        retObj.add(entity1);

                    }


                }

            }

            return retObj;


        } finally {
            pm.close();
        }


    }

    private static List<Entity> getEntityChildren(PersistenceManager pm, final List<Entity> parents) {


        final List<Entity> retObj = new ArrayList<Entity>(INT);


        for (String type : EntityType.classList()) {
            Query q1;

            for (Entity entity : parents) {
                try {

                    q1 = pm.newQuery(Class.forName(type));

                    // q1 = pm.newQuery(Class.forName(EntityType.point.getClassName()));
                    q1.setFilter("parent==b");
                    q1.declareParameters("String b");
                    String key = entity.getKey();
                    if (q1 != null && key != null) {
                        final Object sample  = q1.execute(key);
                        if (sample != null) {
                            List<Entity> result = (List<Entity>) sample;
                            if (!result.isEmpty()) {
                                retObj.addAll(result);
                                //  for (final Entity e : result) {
                                List<Entity> children = getEntityChildren(pm, result);
                                retObj.addAll(children);
                                //}
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    return Collections.emptyList();
                }
            }



        }


        return retObj;

    }


    @Override
    public List<Entity> deleteEntity(final User user, final Entity entity, final Class<?> cls) {
        final PersistenceManager pm = pmf.getPersistenceManager();

        try {
            final Entity c = (Entity) pm.getObjectById(cls, entity.getKey());
            if (c != null) {
                List<Entity> list = new ArrayList<Entity>(1);
                list.add(c);
                final List<Entity> entities = getEntityChildren(pm, list);
                entities.add(c);
                final List<Entity> deleted = createModels(user, entities);
                pm.deletePersistentAll(entities);
                return deleted;
            } else {
                return new ArrayList<Entity>(0);
            }

        } catch (NucleusObjectNotFoundException exception) {
            return Collections.emptyList();
        } finally {
            pm.close();
        }
    }




    @Override
    public List<Entity> getEntityByKey(final User user, final String uuid, final Class<?> cls)  {
        final PersistenceManager pm = pmf.getPersistenceManager();
        final List<Entity> retObj = new ArrayList<Entity>(1);

        try {
            if (!Utils.isEmptyString(uuid)) {
                final Entity result = (Entity) pm.getObjectById(cls, uuid);
                final List<Entity> r = createModel(user, result);
                retObj.addAll(r);
            }
            return retObj;
        } catch (JDOObjectNotFoundException ex) {

            return getEntityByUUID(user, uuid, cls); //maybe they gave us a uuid
        }catch (Exception ex) {
            return Collections.emptyList();
        } finally {
            pm.close();
        }
    }

    @Override
    public List<Entity> getEntityByUUID(final User user, final String uuid, final Class<?> cls)  {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Query q1 = pm.newQuery(cls);
            q1.setFilter("uuid==u");
            q1.declareParameters("String u");
            q1.setRange(0, 1);

            final Collection<Entity> result = (Collection<Entity>) q1.execute(uuid);
            return result.isEmpty() ? Collections.<Entity>emptyList() : createModel(user, result.iterator().next());
        } finally {
            pm.close();
        }

    }


    @Override
    public List<Entity> getEntityByName(final User user, final EntityName name, final Class<?> cls) {
        final PersistenceManager pm = pmf.getPersistenceManager();

        try {
            final Query q1 = pm.newQuery(cls);

            final List<Entity> c;
            if (user != null) {
                q1.setFilter("name==b && owner==o");
                q1.declareParameters("String b, String o");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue(), user.getKey());
            } else {
                q1.setFilter("name==b");
                q1.declareParameters("String b");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue());
            }
            if (c.isEmpty()) {
                return Collections.emptyList();
            } else {

                final Entity result = c.get(0);
                return createModel(user, result);

            }

        } finally {
            pm.close();
        }
    }


    @Override
    public Map<String, Entity> getSystemWideEntityMap(final User admin, final EntityType type) {


        final PersistenceManager pm = pmf.getPersistenceManager();

        try {

            final Query q1 = pm.newQuery(Class.forName(type.getClassName()));

            final List<Entity> result = (List<Entity>) q1.execute(type.getCode());

            final List<Entity> models = createModels(admin, result);
            final Map<String, Entity> retObj = new HashMap<String, Entity>(models.size());

            for (final Entity e : models) {
                retObj.put(e.getKey(), e);
            }
            return retObj;

        } catch (ClassNotFoundException e) {
            return Collections.emptyMap();
        } finally {
            pm.close();
        }


    }


    private void checkDuplicateEntity(final User user, final List<Entity> entityList)  {
        final PersistenceManager pm = pmf.getPersistenceManager();


        try {
            for (Entity entity : entityList) {

                if (entity.getEntityType().equals(EntityType.user)) {
                    final Query q1 = pm.newQuery(Class.forName(entity.getEntityType().getClassName()));
                    q1.setFilter("name==b && entityType==t");
                    q1.declareParameters("String b, Integer t");
                    q1.setRange(0, 1);
                    final Collection<Entity> c = (Collection<Entity>) q1.execute(
                            entity.getName().getValue(),
                            entity.getEntityType().getCode());
                    if (!c.isEmpty()) {

                        throw new IllegalArgumentException("A User with the email address " + entity.getName().getValue() +
                                " already exists. Entities of type [" + entity.getEntityType().name() + "] must have a " +
                                "unique name on your account");


                    }
                } else {


                    final Query q1 = pm.newQuery(Class.forName(entity.getEntityType().getClassName()));
                    q1.setFilter("name==b && owner==o && entityType==t");
                    q1.declareParameters("String b, String o, Integer t");
                    q1.setRange(0, 1);
                    final Collection<Entity> c = (Collection<Entity>) q1.execute(
                            entity.getName().getValue(),
                            user.getKey(),
                            entity.getEntityType().getCode());
                    if (!c.isEmpty()) {

                        throw new IllegalArgumentException("An Entity with the name " + entity.getName().getValue() +
                                " already exists. Entities of type [" + entity.getEntityType().name() + "] must have a " +
                                "unique name on your account");

                    }
                }
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } finally {
            pm.close();
        }
    }

    private List<Entity> createModels(final User user, final Collection<Entity> entity) {
        final List<Entity> retObj = new ArrayList<Entity>(entity.size());
        for (final Entity e : entity) {
            if (e.getEntityType() != null && e.getEntityType().isTreeGridItem()) {
                final List<Entity> r;

                r = createModel(user, e);
                retObj.addAll(r);



            }
        }
        return retObj;

    }

    private List<Entity> createModel(final User user, final Entity entity) {
        final Entity model;

        if (entity.getEntityType() != null) {
            switch (entity.getEntityType()) {

                case user:
                    model = UserModelFactory.createUserModel((User) entity);
                    break;
                case point:
                    model = PointModelFactory.createPointModel((Point) entity);
                    break;
                case category:
                    model = CategoryFactory.createCategory((Category) entity);
                    break;
                case subscription:
                    model = SubscriptionFactory.createSubscription((Subscription) entity);
                    break;
                case calculation:
                    model = CalculationModelFactory.createCalculation((Calculation) entity);
                    break;

                case summary:
                    model = SummaryModelFactory.createSummary((Summary) entity);
                    break;
                case accessKey:
                    model = AccessKeyFactory.createAccessKey((AccessKey) entity);
                    break;
                default:
                    model = null;
                    break;

            }
            final List<Entity> retObj = new ArrayList<Entity>(1);
            if (model != null && model.entityIsReadable(user)) {
                final boolean isOwner = model.isOwner(user);
                model.setReadOnly(!isOwner);
                retObj.add(model);
            }
            return retObj;
        } else {

            return Collections.emptyList();
        }


    }


}
