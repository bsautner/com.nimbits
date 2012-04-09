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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.relationship.Relationship;
import com.nimbits.client.model.user.User;
import com.nimbits.server.entity.EntityTransactions;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.logging.LogHelper;
import com.nimbits.server.orm.EntityStore;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.orm.SimpleEntity;
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
        final Query q1 = pm.newQuery(EntityStore.class);
        q1.setFilter("owner==b && entityType==t");
        q1.declareParameters("String b, Integer t");
        q1.setRange(0, limit);
        try {

            final Collection<Entity> result = (Collection<Entity>) q1.execute(user.getKey(), type.getCode());

            final Map<String, Entity> retObj = new HashMap<String, Entity>(result.size());
            for (final Entity e : result) {
                final Entity model = createModel(e, type);


                retObj.put(model.getKey(), model);
            }
            return retObj;

        } finally {
            pm.close();
        }


    }


    @Override
    public Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException {



        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class);
        q1.setFilter("owner==b && entityType==t");
        q1.declareParameters("String b, Integer t");
        try {

            final Collection<Entity> result = (Collection<Entity>) q1.execute(user.getKey(), type.getCode());
            final List<Entity> models = EntityModelFactory.createEntities(user, result);
            final Map<EntityName, Entity> retObj = new HashMap<EntityName, Entity>(models.size());
            for (final Entity e : models) {

                retObj.put(e.getName(), e);
            }
            return retObj;

        } finally {
            pm.close();
        }


    }



    @Override
    public List<Entity> getChildren(final Entity parentEntity, final EntityType type) {
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
                final Entity result = pm.getObjectById(EntityStore.class, entity.getKey());
                if (result != null) {
                    tx.begin();
                    result.setDescription(entity.getDescription());
                    result.setName(entity.getName());
                    result.setProtectionLevel(entity.getProtectionLevel());
                    result.setParent(entity.getParent());
                    result.setBlobKey(entity.getBlobKey());
                    result.setUUID(entity.getUUID());
                    tx.commit();
                    return EntityModelFactory.createEntity(user, result);
                } else {
                    return addEntity(entity, pm);
                }
            }
        } finally {
            pm.close();
        }



    }

    private Entity addEntity(final Entity entity, final PersistenceManager pm) throws NimbitsException {

        if (entity.getEntityType().isUniqueNameFlag()) {
            checkDuplicateEntity(entity);

        }


        final Entity commit = new EntityStore(entity);
        if (Utils.isEmptyString(entity.getUUID())) {
            entity.setUUID(UUID.randomUUID().toString());
        }
        SimpleEntity simple = new SimpleEntity(commit);

        pm.makePersistent(simple);
        if (simple.getEntityType().equals(EntityType.user) ){
            final Transaction tx = pm.currentTransaction();
            tx.begin();
            simple.setParent(entity.getKey());
            simple.setOwner(entity.getKey());
            tx.commit();

        }
        return EntityModelFactory.createEntity(user, commit);
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

        final Query q1 = pm.newQuery(EntityStore.class, ":p.contains(owner)");

//        final Query q2 = pm.newQuery(PointEntity.class, ":p.contains(owner)");


        try {
            final Collection<Entity> result = (Collection<Entity>) q1.execute(uuids);
           // final List<Point> result2 = (List<Point>) q2.execute(uuids);


            final List<Entity> entities =  EntityModelFactory.createEntities(user, result);
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

        final Query q1 = pm.newQuery(EntityStore.class);
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

    private static List<Entity> getEntityChildren(final PersistenceManager pm, final Entity entity, final EntityType type) {

        final Query q1 = pm.newQuery(EntityStore.class);
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

    }



    @Override

    public List<Entity> deleteEntity(final Entity entity, final Class<?> cls) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {




            final Entity c = (Entity) pm.getObjectById(cls, entity.getKey());
            if (c != null) {
                final List<Entity> entities = getEntityChildren(pm,c);
                entities.add(c);
                final List<Entity> deleted = EntityModelFactory.createEntities(user, entities);

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

                return createModel(result, cls);
                //  return EntityModelFactory.createEntity(user, result);

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
            final Query q1 = pm.newQuery(EntityStore.class);
            q1.setFilter("uuid==u");
            q1.declareParameters("String u");
            q1.setRange(0, 1);
            // final List<Entity> retObj = new ArrayList<Entity>(1);
            final Collection<Entity> result = (Collection<Entity>) q1.execute(uuid);
            if (result.isEmpty()) {
                return null;
            }
            else {
                return createModel(result.iterator().next(), cls);
            }
        } finally {
            pm.close();
        }

    }


//    @Override
//
//    public Entity getEntityByName(final EntityName name) throws NimbitsException {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final List<Entity> c;
//
//        try {
//            final Query q1 = pm.newQuery(EntityStore.class);
//            if (user != null) {
//                q1.setFilter("name==b && owner==o");
//                q1.declareParameters("String b, String o");
//                q1.setRange(0, 1);
//                c = (List<Entity>) q1.execute(name.getValue(), user.getKey());
//            }
//            else {
//                q1.setFilter("name==b");
//                q1.declareParameters("String b");
//                q1.setRange(0, 1);
//                c = (List<Entity>) q1.execute(name.getValue());
//            }
//            if (c.size() > 0) {
//
//                final Entity result = c.get(0);
//                return EntityModelFactory.createEntity(user,  result);
//
//            }
//            else {
//                return null;
//            }
//
//        } finally {
//            pm.close();
//        }
//    }

    @Override
    public Entity getEntityByName(EntityName name, EntityType type) throws NimbitsException {
        if (type.equals(EntityType.point)) {

            return getEntityByName(name, PointEntity.class);
        }
        else {

            final PersistenceManager pm = PMF.get().getPersistenceManager();
            final List<Entity> c;

            try {
                final Query q1 = pm.newQuery(EntityStore.class);
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
                    return EntityModelFactory.createEntity(user, result);

                }

            } finally {
                pm.close();
            }
        }
    }

    private void checkDuplicateEntity(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {

            if (entity.getEntityType().equals(EntityType.user)) {
                final Query q1 = pm.newQuery(EntityStore.class);
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


                final Query q1 = pm.newQuery(EntityStore.class);
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
            final List<Entity> models = createModels(result, cls);
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
    public Map<String, Point> getSystemWidePointMap( ) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final Query q1 = pm.newQuery(PointEntity.class);

            final List<PointEntity> result = (List<PointEntity>) q1.execute(PointEntity.class);
            LogHelper.log(this.getClass(), "system wide point search found " + result.size());

            for (Point px : result) {
                LogHelper.log(this.getClass(), GsonFactory.getInstance().toJson(px));
                LogHelper.log(this.getClass(), "" + (px.getName() == null));
            }
            // final List<Point> models = PointModelFactory.createPointModels(result);
            final Map<String, Point> retObj = new HashMap<String, Point>(result.size());

//            for (final Point e : models) {
//                retObj.put(e.getKey(), e);
//            }
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
                return createModel(result, cls);

            }

        } finally {
            pm.close();
        }
    }


    private Entity createModel(final Entity entity, final Class<?> cls) throws NimbitsException {

        if (cls.equals(PointEntity.class)) {
            return PointModelFactory.createPointModel((Point) entity);
        }
        else {
            return EntityModelFactory.createEntity(user, entity);
        }

    }
    private List<Entity> createModels(final List<Entity> entity, final Class<?> cls) throws NimbitsException {
        final List<Entity> retObj = new ArrayList<Entity>(entity.size());
        for (final Entity e : entity) {
            retObj.add(createModel(e, cls));
        }
        return retObj;

    }
    private Entity createModel(final Entity entity, final EntityType type) throws NimbitsException {
        try {
            if (Utils.isEmptyString(type.getClassName())) {
                return  EntityModelFactory.createEntity(user, entity);
            }
            else {
                return createModel(entity, Class.forName(type.getClassName()));
            }


        } catch (ClassNotFoundException e) {
            return EntityModelFactory.createEntity(user, entity);
        }

    }


}
