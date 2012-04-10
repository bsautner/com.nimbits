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


import com.nimbits.PMF;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.relationship.Relationship;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.xmpp.*;
import com.nimbits.server.entity.EntityTransactions;
import com.nimbits.server.logging.LogHelper;
import com.nimbits.server.orm.*;
import com.nimbits.server.relationship.RelationshipTransactionFactory;
import com.nimbits.shared.Utils;

import javax.jdo.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:46 AM
 */

@SuppressWarnings("unchecked")
public class EntityDaoImpl implements  EntityTransactions {

    private static final int INT = 1024;
    private final User user;
    final Logger log = Logger.getLogger(EntityDaoImpl.class.getName());

    public EntityDaoImpl(final User user) {
        this.user = user;
    }

    @Override
    public Map<String, Entity> getEntityMap(final EntityType type, final int limit) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1;
        try {
            q1 = pm.newQuery(Class.forName(type.getClassName()));

            q1.setFilter("owner==b && entityType==t");
            q1.declareParameters("String b, Integer t");
            q1.setRange(0, limit);


            final Collection<Entity> result = (Collection<Entity>) q1.execute(user.getKey(), type.getCode());

            final Map<String, Entity> retObj = new HashMap<String, Entity>(result.size());
            for (final Entity e : result) {
                final Entity model = createModel(e);


                retObj.put(model.getKey(), model);
            }
            return retObj;


        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }  catch (NullPointerException e) {
            log.severe(e.getMessage());
            return new HashMap<String, Entity>(0);
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
            return getEntityChildren(pm, parentEntity, type);
        }
        finally {
            pm.close();
        }



    }


    @Override
    public Entity addUpdateEntity(final Entity entity) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            if (Utils.isEmptyString(entity.getKey())) {
                return addEntity(entity, pm);
            } else {

                final Transaction tx = pm.currentTransaction();
                Class cls = Class.forName(entity.getEntityType().getClassName());
                final Entity result = (Entity) pm.getObjectById(cls, entity.getKey());
                if (result != null) {
                    tx.begin();
                    result.update(entity);
                    tx.commit();
                    return createModel(result);
                } else {
                    return addEntity(entity, pm);
                }
            }
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
        final Entity commit;
        switch (entity.getEntityType()) {


            case user:
                commit = new UserEntity(entity);
                break;
            case point:
                commit = new PointEntity(entity);
                break;
            case category:
                commit = new SimpleEntity(entity);
                break;
            case file:
                commit = new SimpleEntity(entity);
                break;
            case subscription:
                commit = new SubscriptionEntity((Subscription) entity);
                break;
            case userConnection:
                commit = new SimpleEntity(entity);
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
                commit = new SimpleEntity(entity);
                break;
            default:
                commit = new SimpleEntity(entity);
        }


        if (Utils.isEmptyString(commit.getUUID())) {
            commit.setUUID(UUID.randomUUID().toString());
        }

        pm.makePersistent(commit);
        if (entity.getEntityType().equals(EntityType.user) ){
            final Transaction tx = pm.currentTransaction();
            tx.begin();
            entity.setParent(entity.getKey());
            entity.setOwner(entity.getKey());
            tx.commit();

        }
        return createModel(commit);
    }


    @Override
    public List<Entity> getEntities() throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        final Map<String, Entity> connections = getEntityMap(EntityType.userConnection, 1000);
        final Collection<String> uuids = new ArrayList<String>(connections.size() + 1);
        uuids.add(user.getKey());

        final Collection<String> connectedUserKeys = new ArrayList<String>(connections.size());
        final Map<String, Relationship> relationshipMap = new HashMap<String, Relationship>(connections.size());
        Relationship r;
        for (final Entity e : connections.values()) {
            r = RelationshipTransactionFactory.getInstance().getRelationship(e);

            if (r != null) {
                relationshipMap.put(r.getForeignKey(), r);
                uuids.add(r.getForeignKey());
                connectedUserKeys.add(r.getForeignKey());
            }

        }

        final Query q1 = pm.newQuery(PointEntity.class, ":p.contains(owner)");

//        final Query q2 = pm.newQuery(PointEntity.class, ":p.contains(owner)");


        try {
            final Collection<Entity> result = (Collection<Entity>) q1.execute(uuids);
            // final List<Point> result2 = (List<Point>) q2.execute(uuids);


            final List<Entity> entities =  createModels(result);
            // List<Point> points = PointModelFactory.createPointModels(result2);

            for (final Entity entity1 : entities) {

                if (connectedUserKeys.contains(entity1.getParent())) {
                    final Relationship rx = relationshipMap.get(entity1.getParent());
                    entity1.setParent(rx.getKey());
                }


            }
//            for (final Point p : points) {
//                if (! p.getName().getValue().equals(Const.TEXT_DATA_FEED)) {
//                    if (connectedUserKeys.contains(p.getParent()) ) {
//                        final Relationship rx = relationshipMap.get(p.getParent());
//                        p.setParent(rx.getKey());
//                    }
//                    entities.add(p);
//                }
//
//
//            }
            return entities;



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
            List<Entity> children;
            for (final Entity e : result) {
                children = getEntityChildren(pm, e);
                retObj.addAll(children);
            }
        }

        return retObj;

    }

    private static List<Entity> getEntityChildren(final PersistenceManager pm, final Entity entity, final EntityType type) throws NimbitsException {

        try {
            Class cls = Class.forName(type.getClassName());

            final Query q1 = pm.newQuery(cls);
            q1.setFilter("parent==b && entityType==t");
            q1.declareParameters("String b, Integer t");
            final List<Entity> retObj = new ArrayList<Entity>(INT);



            final Collection<Entity> result = (Collection<Entity>) q1.execute(entity.getKey(), type.getCode());
            if (!result.isEmpty()) {
                retObj.addAll(result);
                List<Entity> children;
                for (final Entity e : result) {
                    children = getEntityChildren(pm, e);
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
    public Entity getEntityByKey(final String uuid, final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            if (Utils.isEmptyString(uuid)) {
                return null;
            } else {
                final Entity result = (Entity) pm.getObjectById(cls, uuid);

                return createModel(result);


            }
        } catch (JDOObjectNotFoundException ex) {

            return getEntityByUUID(uuid, cls); //maybe they gave us a uuid
        } catch (JDOFatalUserException ex) {
            return null;
        } finally {
            pm.close();
        }
    }

    private Entity getEntityByUUID(final String uuid, final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q1 = pm.newQuery(cls);
            q1.setFilter("uuid==u");
            q1.declareParameters("String u");
            q1.setRange(0, 1);
            // final List<Entity> retObj = new ArrayList<Entity>(1);
            try {
                final Collection<Entity> result = (Collection<Entity>) q1.execute(uuid);
                return result.isEmpty() ? null : createModel(result.iterator().next());
            } catch (NimbitsException e) {
                return null;

            } catch (NullPointerException e) {
                return null;
            }
        } finally {
            pm.close();
        }

    }



    @Override
    public Entity getEntityByName(EntityName name, EntityType type) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            if (type.equals(EntityType.point)) {

                return getEntityByName(name, Class.forName(type.getClassName()));
            }
            else {


                final List<Entity> c;


                Class cls = Class.forName(type.getClassName());
                final Query q1 = pm.newQuery(cls);
                if (user != null) {
                    q1.setFilter("name==b && owner==o && entityType==t");
                    q1.declareParameters("String b, String o, Integer t");
                    q1.setRange(0, 1);
                    c = (List<Entity>) q1.execute(name.getValue(), user.getKey(), type.getCode());
                }
                else {
                    q1.setFilter("name==b && entityType==t");
                    q1.declareParameters("String b, Integer t");
                    q1.setRange(0, 1);
                    c = (List<Entity>) q1.execute(name.getValue(), type.getCode());
                }
                if (c.isEmpty()) {
                    return null;
                } else {

                    final Entity result = c.get(0);
                    return createModel(result);

                }
            }
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        } finally {
            pm.close();
        }

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

    @Override

    public Map<String, Entity> getSystemWideEntityMap(final EntityType type, final Class<?> cls) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final Query q1 = pm.newQuery(cls);

            final List<Entity> result = (List<Entity>) q1.execute(type.getCode());
            LogHelper.log(this.getClass(), "system wide search found " + result.size());
            final List<Entity> models = createModels(result);
            final Map<String, Entity> retObj = new HashMap<String, Entity>(models.size());

            for (final Entity e : models) {
                retObj.put(e.getKey(), e);
            }
            return retObj;

        } finally {
            pm.close();
        }


    }



    @Override
    public void removeEntityFromCache(final Entity entity) throws NimbitsException {
        throw new NimbitsException(UserMessages.ERROR_NOT_IMPLEMENTED);
    }

    @Override
    public Entity getEntityByName(final EntityName name,final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Entity> c;

        try {
            final Query q1 = pm.newQuery(cls);
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
                return null;
            } else {

                final Entity result = c.get(0);
                return createModel(result);

            }

        } finally {
            pm.close();
        }
    }



    private List<Entity> createModels(final Collection<Entity> entity) throws NimbitsException {
        final List<Entity> retObj = new ArrayList<Entity>(entity.size());
        for (final Entity e : entity) {
            retObj.add(createModel(e));
        }
        return retObj;

    }
    private Entity createModel(final Entity entity ) throws NimbitsException {
        Entity retObj;
        switch (entity.getEntityType()) {

            case user:
                retObj = UserModelFactory.createUserModel((User) entity);
                break;
            case point:
                retObj = PointModelFactory.createPointModel(entity);
                break;
            case category:
                retObj = EntityModelFactory.createSimpleEntity(entity);
                break;
            case file:
                retObj = EntityModelFactory.createSimpleEntity(entity);
                break;
            case subscription:
                retObj = SubscriptionFactory.createSubscription((Subscription) entity);
                break;
            case userConnection:
                retObj = UserModelFactory.createUserModel((User) entity);//?
                break;
            case calculation:
                retObj = CalculationModelFactory.createCalculation((Calculation) entity);
                break;
            case intelligence:
                retObj = IntelligenceModelFactory.createIntelligenceModel((Intelligence) entity);
                break;
            case feed:
                retObj = PointModelFactory.createPointModel(entity);
                break;
            case resource:
                retObj = XmppResourceFactory.createXmppResource((XmppResource) entity);
                break;
            case summary:
                retObj = SummaryModelFactory.createSummary((Summary) entity);
                break;
            case instance:
                retObj = null;
                break;
            default:retObj = null;

        }
        if (retObj != null) {
            final boolean isOwner = (user != null) && retObj.getOwner().equals(user.getKey());
            final boolean isReadable = entityIsReadable(entity, isOwner);
            if (!isReadable) {
                retObj = null;
            }
            else if (! isOwner) {
                retObj.setReadOnly(true);
            }
        }

        return retObj;


    }

    private boolean entityIsReadable(  final Entity e, final boolean isOwner) {
        boolean retVal =  ((e.getEntityType().equals(EntityType.user) ||
                isOwner ||
                e.getProtectionLevel().equals(ProtectionLevel.everyone) ||
                e.getProtectionLevel().equals(ProtectionLevel.onlyConnection))

        );

        if (e.getEntityType().equals(EntityType.userConnection) && ! e.getOwner().equals(user.getKey())) {
            retVal = false;
        }
        if (e.getEntityType().equals(EntityType.summary) && user == null) {
            retVal = true; //this is a system request from the summary cron job.
        }
        return retVal;


    }
}
