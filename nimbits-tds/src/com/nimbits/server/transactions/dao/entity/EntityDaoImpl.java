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

package com.nimbits.server.transactions.dao.entity;


import com.nimbits.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.accesskey.*;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.file.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.xmpp.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.server.orm.*;
import com.nimbits.shared.*;

import javax.jdo.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:46 AM
 */

@SuppressWarnings({"unchecked", "FeatureEnvy"})
public class EntityDaoImpl implements  EntityTransactions {

    private static final int INT = 1024;

    private final User user;
    private final Logger log = Logger.getLogger(EntityDaoImpl.class.getName());

    public EntityDaoImpl(final User user) {
        this.user = user;
    }

    @Override
    public List<Entity> getSubscriptionsToEntity(final Entity subscribedEntity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(SubscriptionEntity.class);
            q.setFilter("subscribedEntity==p && enabled==e");
            q.declareParameters("String p, Boolean e");
            final Collection<Entity> results = (Collection<Entity>) q.execute(subscribedEntity.getKey(), true);
            return createModels(results);
        }
        finally {
            pm.close();
        }
    }
    @Override
    public List<Entity> getIdleEntities() throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final Query q = pm
                    .newQuery(PointEntity.class);
            q.setFilter("idleAlarmOn == k && idleAlarmSent  == c");
            q.declareParameters("Long k, Long c");

            final Collection<Entity> points = (Collection<Entity>) q.execute(true, false);
            return  createModels( points);
        } finally {
            pm.close();
        }



    }

    @Override
    public List<Entity> getEntityByTrigger(final Entity entity, final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(cls);
            q.setFilter("trigger == k && enabled == true");
            q.declareParameters("String k");
            final Collection<Entity> results = (Collection<Entity>) q.execute(entity.getKey());
            return createModels(results);
        } finally {
            pm.close();
        }
    }


    @Override
    public Map<String, Entity> getEntityMap(final EntityType type, final int limit) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q1 = pm.newQuery(Class.forName(type.getClassName()));

            q1.setFilter("owner==b && entityType==t");
            q1.declareParameters("String b, Integer t");
            q1.setRange(0, limit);


            final Collection<Entity> result = (Collection<Entity>) q1.execute(user.getKey(), type.getCode());

            final Map<String, Entity> retObj = new HashMap<String, Entity>(result.size());
            for (final Entity e : result) {
                final List<Entity> models = createModel(e);
                if (! models.isEmpty()) {
                    Entity model = models.get(0);
                    retObj.put(model.getKey(), model);
                }
            }
            return retObj;


        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }

        finally {
            pm.close();
        }


    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException {



        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q1 = pm.newQuery(Class.forName(type.getClassName()));
            q1.setFilter("owner==b && entityType==t");
            q1.declareParameters("String b, Integer t");
            final Collection<Entity> result = (Collection<Entity>) q1.execute(user.getKey(), type.getCode());
            final List<Entity> models = createModels(result);
            final Map<EntityName, Entity> retObj = new HashMap<EntityName, Entity>(models.size());
            for (final Entity e : models) {

                retObj.put(e.getName(), e);
            }
            return retObj;

        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        } finally {
            pm.close();
        }


    }

    @Override
    public List<Entity> getChildren(final Entity parentEntity, final EntityType type) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final List<Entity> r =  getEntityChildren(pm, parentEntity, type);
            return createModels(r);
        }
        finally {
            pm.close();
        }



    }

    @Override
    public Entity addUpdateEntity(final Entity entity) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        log.info(entity.toString());
        try {

            final Entity retObj;
            if (Utils.isEmptyString(entity.getKey())) {
                retObj =  addEntity(entity, pm);
            } else {

                final Transaction tx = pm.currentTransaction();
                final Class cls = Class.forName(entity.getEntityType().getClassName());
                final Entity result = (Entity) pm.getObjectById(cls, entity.getKey());

                if (result != null) {
                    tx.begin();
                    result.update(entity);

                    tx.commit();
                    final List<Entity> model = createModel(result);
                    if (model.isEmpty()) {
                        throw new NimbitsException("error creating model");
                    }
                    else {
                        retObj = model.get(0);
                    }
                } else {

                    retObj= addEntity(entity, pm);
                }

            }
            return retObj;
        } catch (JDOObjectNotFoundException e) {
            return addEntity(entity, pm);
        } catch (ConcurrentModificationException e) {
            throw new NimbitsException(e);
        } catch (ClassNotFoundException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);


        } finally {
            pm.close();
        }



    }

    private Entity addEntity(final Entity entity, final PersistenceManager pm) throws NimbitsException {

        if (entity.getEntityType().isUniqueNameFlag()) {
            checkDuplicateEntity(entity);

        }
        final Entity commit = downcastEntity(entity);


        if (Utils.isEmptyString(commit.getUUID())) {
            commit.setUUID(UUID.randomUUID().toString());
        }
        if (! commit.getEntityType().equals(EntityType.user)) {
            commit.validate();
        }
        commit.setDateCreated(new Date());
        pm.makePersistent(commit);

        if (entity.getEntityType().equals(EntityType.user) ){
            final Transaction tx = pm.currentTransaction();
            tx.begin();
            entity.setParent(entity.getKey());
            entity.setOwner(entity.getKey());
            //entity.validate();
            tx.commit();

        }
        final List<Entity> r = createModel(commit);

        if (r.isEmpty()) {
            throw new NimbitsException("Error creating model");
        }
        else {
            return r.get(0);
        }
    }


    @Override
    public List<Entity> getEntities() throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        final Map<String, Entity> connections = getEntityMap(EntityType.userConnection, 1000);
        final Collection<String> ownerKeys = new ArrayList<String>(connections.size() + 1);
        for (Entity c : connections.values()) {
            ownerKeys.add(c.getName().getValue());
        }
        ownerKeys.addAll(connections.keySet());
        ownerKeys.add(user.getKey());


        final Map<String, Entity> relationshipMap = new HashMap<String, Entity>(connections.size());
        for (Entity e : connections.values()) {
            relationshipMap.put(e.getName().getValue(), e);
        }
        // final Collection<String> connectedUserKeys = getConnectedUserKeys(connections, uuids, relationshipMap);
        // connectedUserKeys.addAll(connections.keySet());
        try{
            final List<Entity> retObj = new ArrayList<Entity>(INT);


            for (final EntityType type : EntityType.values()) {
                try {
                    if (type.isTreeGridItem()) {
                        final Query q1 = pm.newQuery(Class.forName(type.getClassName()), ":p.contains(owner)");
                        final Collection<Entity> result = (Collection<Entity>) q1.execute(ownerKeys);
                        final List<Entity> entities =  createModels(result);
                        for (final Entity entity1 : entities) {

                            if (relationshipMap.containsKey(entity1.getParent())) {
                                // final Relationship rx = relationshipMap.get(entity1.getParent());
                                Entity c = relationshipMap.get(entity1.getParent());
                                entity1.setParent(c.getKey());
                            }

                            retObj.add(entity1);

                        }



                    }

                }
                catch (NullPointerException e) {
                    log.info(e.getMessage());
                    log.info("caused by type not existing in store");
                }
                catch (ClassNotFoundException e) {
                    LogHelper.logException(this.getClass(), e);
                }
            }

            return retObj;



        } finally {
            pm.close();
        }


    }

    private static List<Entity> getEntityChildren(final PersistenceManager pm, final Entity entity) {

        final Query q1 = pm.newQuery(PointEntity.class);
        q1.setFilter("parent==b");
        q1.declareParameters("String b");
        final List<Entity> retObj = new ArrayList<Entity>(INT);



        final Collection<Entity> result = (Collection<Entity>) q1.execute(entity.getKey());
        if (!result.isEmpty()) {
            retObj.addAll(result);
            for (final Entity e : result) {
                List<Entity> children = getEntityChildren(pm, e);
                retObj.addAll(children);
            }
        }

        return retObj;

    }

    private static List<Entity> getEntityChildren(final PersistenceManager pm, final Entity entity, final EntityType type) throws NimbitsException {

        try {
            final Class cls = Class.forName(type.getClassName());

            final Query q1 = pm.newQuery(cls);
            q1.setFilter("parent==b && entityType==t");
            q1.declareParameters("String b, Integer t");
            final List<Entity> retObj = new ArrayList<Entity>(INT);



            final Collection<Entity> result = (Collection<Entity>) q1.execute(entity.getKey(), type.getCode());
            if (!result.isEmpty()) {
                retObj.addAll(result);
                for (final Entity e : result) {
                    final List<Entity> children = getEntityChildren(pm, e);
                    retObj.addAll(children);
                }
            }

            return retObj;
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }

    }

    @Override
    public List<Entity> deleteEntity(final Entity entity, final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {




            final Entity c = (Entity) pm.getObjectById(cls, entity.getKey());
            if (c != null) {
                final List<Entity> entities = getEntityChildren(pm,c);
                entities.add(c);
                final List<Entity> deleted = createModels(entities);

                pm.deletePersistentAll(entities);
                return deleted;
            }
            else {
                return new ArrayList<Entity>(0);
            }
        } finally {
            pm.close();
        }
    }

    @Override
    public List<Entity> getEntitiesBySource(final Entity source, final Class<?>cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {

            Query q = pm.newQuery(cls);
            q.setFilter("source == s && enabled== e");
            q.declareParameters("String s, Boolean e");
            Collection<Entity> result = (Collection<Entity>) q.execute(source.getKey(), true);
            return createModels(result);

        }  finally {
            pm.close();
        }


    }

    @Override
    public List<Entity> getEntityByKey(final String uuid, final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Entity> retObj = new ArrayList<Entity>(1);

        try {
            if (! Utils.isEmptyString(uuid)) {

                final Entity result = (Entity) pm.getObjectById(cls, uuid);
                final List<Entity> r = createModel(result);
                retObj.addAll(r);


            }
            return retObj;
        } catch (JDOObjectNotFoundException ex) {

            return getEntityByUUID(uuid, cls); //maybe they gave us a uuid
        }  finally {
            pm.close();
        }
    }

    private List<Entity> getEntityByUUID(final String uuid, final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q1 = pm.newQuery(cls);
            q1.setFilter("uuid==u");
            q1.declareParameters("String u");
            q1.setRange(0, 1);

            final Collection<Entity> result = (Collection<Entity>) q1.execute(uuid);
            return result.isEmpty() ?  Collections.<Entity>emptyList() : createModel(result.iterator().next());
        } finally {
            pm.close();
        }

    }

    @Override
    public List<Entity> getEntityByName(final EntityName name,final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q1 = pm.newQuery(cls);
            final List<Entity> c;
            if (user != null) {
                q1.setFilter("name==b && owner==o");
                q1.declareParameters("String b, String o");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue(), user.getKey());
            }
            else {
                q1.setFilter("name==b && entityType==t");
                q1.declareParameters("String b, Integer t");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue());
            }
            if (c.isEmpty()) {
                return Collections.emptyList();
            } else {

                final Entity result = c.get(0);
                return createModel(result);

            }

        } finally {
            pm.close();
        }
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final Query q1 = pm.newQuery(Class.forName(type.getClassName()));

            final List<Entity> result = (List<Entity>) q1.execute(type.getCode());
            LogHelper.log(this.getClass(), "system wide search found " + result.size());
            final List<Entity> models = createModels(result);
            final Map<String, Entity> retObj = new HashMap<String, Entity>(models.size());

            for (final Entity e : models) {
                retObj.put(e.getKey(), e);
            }
            return retObj;

        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        } finally {
            pm.close();
        }


    }

    @Override
    public void removeEntityFromCache(final Entity entity) throws NimbitsException {
        throw new NimbitsException(UserMessages.ERROR_NOT_IMPLEMENTED);
    }

    private void checkDuplicateEntity(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {

            if (entity.getEntityType().equals(EntityType.user)) {
                final Query q1 = pm.newQuery(Class.forName(entity.getEntityType().getClassName()));
                q1.setFilter("name==b && entityType==t");
                q1.declareParameters("String b, Integer t");
                q1.setRange(0, 1);
                final Collection<Entity> c = (Collection<Entity>) q1.execute(
                        entity.getName().getValue(),
                        entity.getEntityType().getCode());
                if (!c.isEmpty()) {

                    throw new NimbitsException("A User with the email address " + entity.getName().getValue() +
                            " already exists. Entities of type [" + entity.getEntityType().name() + "] must have a " +
                            "unique name on your account");


                }
            }
            else
            {


                final Query q1 = pm.newQuery(Class.forName(entity.getEntityType().getClassName()));
                q1.setFilter("name==b && owner==o && entityType==t");
                q1.declareParameters("String b, String o, Integer t");
                q1.setRange(0, 1);
                final Collection<Entity> c = (Collection<Entity>) q1.execute(
                        entity.getName().getValue(),
                        user.getKey(),
                        entity.getEntityType().getCode());
                if (!c.isEmpty()) {

                    throw new NimbitsException("An Entity with the name " + entity.getName().getValue() +
                            " already exists. Entities of type [" + entity.getEntityType().name() + "] must have a " +
                            "unique name on your account");

                }
            }

        } catch(javax.jdo.JDOUserException ignored) {



        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        } finally {
            pm.close();
        }
    }

    private List<Entity> createModels(final Collection<Entity> entity) throws NimbitsException {
        final List<Entity> retObj = new ArrayList<Entity>(entity.size());
        for (final Entity e : entity) {
            final List<Entity> r =createModel(e);
            retObj.addAll(r);
        }
        return retObj;

    }

    private List<Entity> createModel(final Entity entity ) throws NimbitsException {
        final Entity model;
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
            case file:
                model = FileFactory.createFile((File) entity);
                break;
            case subscription:
                model = SubscriptionFactory.createSubscription((Subscription) entity);
                break;
            case userConnection:
                model = ConnectionFactory.createCreateConnection((Connection) entity);
                break;
            case calculation:
                model = CalculationModelFactory.createCalculation((Calculation) entity);
                break;
            case intelligence:
                model = IntelligenceModelFactory.createIntelligenceModel((Intelligence) entity);
                break;
            case feed:
                model = PointModelFactory.createPointModel((Point) entity);
                break;
            case resource:
                model = XmppResourceFactory.createXmppResource((XmppResource) entity);
                break;
            case summary:
                model = SummaryModelFactory.createSummary((Summary) entity);
                break;
            case accessKey:
                model = AccessKeyFactory.createAccessKey((AccessKey) entity);
                break;
            default:
                throw new NimbitsException("Can't create an entity model without a known type");

        }
        final List<Entity> retObj = new ArrayList<Entity>(1);
        if (model.entityIsReadable(user)) {
            final boolean isOwner = model.isOwner(user);
            model.setReadOnly(!isOwner );
            retObj.add(model);
        }
        else {
            log.info("did not return an entity because user " + user.getKey() + " could not read " + model.toString());
        }


        return retObj;


    }

    @SuppressWarnings({"OverlyCoupledMethod", "OverlyLongMethod", "OverlyComplexMethod"})
    private static Entity downcastEntity(final Entity entity) throws NimbitsException {
        final Entity commit;
        switch (entity.getEntityType()) {


            case user:
                commit = new UserEntity(entity);
                break;
            case point:
                commit = new PointEntity(entity);
                break;
            case category:
                commit = new CategoryEntity(entity);
                break;
            case file:
                commit = new FileEntity((File) entity);
                break;
            case subscription:
                commit = new SubscriptionEntity((Subscription) entity);
                break;
            case userConnection:
                commit = new ConnectionEntity(entity);
                break;
            case calculation:
                commit = new CalcEntity((Calculation) entity);
                break;
            case intelligence:
                commit = new IntelligenceEntity((Intelligence) entity);
                break;
            case feed:
                commit = new PointEntity(entity);
                break;
            case resource:
                commit = new XmppResourceEntity((XmppResource) entity);
                break;
            case summary:
                commit = new SummaryEntity((Summary) entity);
                break;
            case instance:
                commit = new CategoryEntity(entity);
                break;
            case accessKey:
                commit = new AccessKeyEntity((AccessKey) entity);
                break;
            default:
                commit = new CategoryEntity(entity);
        }
        return commit;
    }
}
