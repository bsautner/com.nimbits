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
import com.nimbits.client.model.user.User;
import com.nimbits.server.entity.EntityTransactions;
import com.nimbits.server.orm.EntityStore;
import com.nimbits.shared.Utils;

import javax.jdo.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:46 AM
 */

@SuppressWarnings("unchecked")
public class EntityDaoImpl implements  EntityTransactions {

    private final User user;
    private Entity entity;


    public EntityDaoImpl(final User user) {
        this.user = user;
    }

    @Override
    public Map<String, Entity> getEntityMap(final EntityType type) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class);
        q1.setFilter("owner==b && entityType==t");
        q1.declareParameters("String b, Integer t");
        try {

            final List<Entity> result = (List<Entity>) q1.execute(user.getKey(), type.getCode());
            final List<Entity> models = EntityModelFactory.createEntities(user, result);
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
    public Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException {



        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class);
        q1.setFilter("owner==b && entityType==t");
        q1.declareParameters("String b, Integer t");
        try {

            final List<Entity> result = (List<Entity>) q1.execute(user.getKey(), type.getCode());
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
            if (! Utils.isEmptyString(entity.getKey())) {

                final Transaction tx = pm.currentTransaction();
                final Entity result = pm.getObjectById(EntityStore.class, entity.getKey());
                if (result != null) {
                    tx.begin();
                    result.setDescription(entity.getDescription());
                    result.setName(entity.getName());
                    result.setProtectionLevel(entity.getProtectionLevel());
                    result.setParent(entity.getParent());
                    result.setBlobKey(entity.getBlobKey());
                    tx.commit();
                    return EntityModelFactory.createEntity(user,result);
                }
                else {
                    return addEntity(entity, pm);
                }
            }
            else {
                return addEntity(entity, pm);
            }
        } finally {
            pm.close();
        }



    }

    private Entity addEntity(final Entity entity, final PersistenceManager pm) throws NimbitsException {
        this.entity = entity;
        if (entity.getEntityType().isUniqueNameFlag()) {
            checkDuplicateEntity(entity);

        }


        final Entity commit = new EntityStore(entity);
        pm.makePersistent(commit);
        if (entity.getEntityType().equals(EntityType.user) ){
            final Transaction tx = pm.currentTransaction();
            tx.begin();
            entity.setParent(entity.getKey());
            entity.setOwner(entity.getKey());
            tx.commit();

        }
        return EntityModelFactory.createEntity(user, commit);
    }


    @Override
    public List<Entity> getEntities() throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        final Map<String, Entity> connections = getEntityMap(EntityType.userConnection);
        final List<String> uuids = new ArrayList<String>(connections.size() + 1);
        uuids.add(user.getKey());


        for (final Entity e : connections.values()) {

            uuids.add(e.getKey());
        }

        final Query q1 = pm.newQuery(EntityStore.class, ":p.contains(owner)");

        try {
            final List<Entity> result = (List<Entity>) q1.execute(uuids);
            return EntityModelFactory.createEntities(user, result);
        } finally {
            pm.close();
        }


    }

    private static List<Entity> getEntityChildren(final PersistenceManager pm, final Entity entity) {

        final Query q1 = pm.newQuery(EntityStore.class);
        q1.setFilter("parent==b");
        q1.declareParameters("String b");
        final List<Entity> retObj = new ArrayList<Entity>(1024);



        final List<Entity> result = (List<Entity>) q1.execute(entity.getKey());
        if (result.size() > 0) {
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
        final List<Entity> retObj = new ArrayList<Entity>(1024);



        final List<Entity> result = (List<Entity>) q1.execute(entity.getKey(), type.getCode());
        if (result.size() > 0) {
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

    public List<Entity> deleteEntity(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {




            Entity c = pm.getObjectById(EntityStore.class, entity.getKey());
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

    public Entity getEntityByKey(final String uuid) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {

            final Entity result =  pm.getObjectById(EntityStore.class, uuid);
            return EntityModelFactory.createEntity(user,result);

        } catch (JDOObjectNotFoundException ex) {
            return null;
        } catch (JDOFatalUserException ex) {
            return null;
        } finally {
            pm.close();
        }
    }

    @Override

    public Entity getEntityByName(final EntityName name) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Entity> c;

        try {
            final Query q1 = pm.newQuery(EntityStore.class);
            if (user != null) {
                q1.setFilter("name==b && owner==o");
                q1.declareParameters("String b, String o");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue(), user.getKey());
            }
            else {
                q1.setFilter("name==b");
                q1.declareParameters("String b");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue());
            }
            if (c.size() > 0) {

                final Entity result = c.get(0);
                return EntityModelFactory.createEntity(user,  result);

            }
            else {
                return null;
            }

        } finally {
            pm.close();
        }
    }

    @Override
    public Entity getEntityByName(EntityName name, EntityType type) throws NimbitsException {
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
            if (c.size() > 0) {

                final Entity result = c.get(0);
                return EntityModelFactory.createEntity(user,  result);

            }
            else {
                return null;
            }

        } finally {
            pm.close();
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
                final List<Entity> c = (List<Entity>) q1.execute(
                        entity.getName().getValue(),
                        entity.getEntityType().getCode());
                if (c.size() > 0) {

                    throw new NimbitsException("An User with the email address " + entity.getName().getValue() +
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
                final List<Entity> c = (List<Entity>) q1.execute(
                        entity.getName().getValue(),
                        user.getKey(),
                        entity.getEntityType().getCode());
                if (c.size() > 0) {

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

    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class);
        q1.setFilter("entityType==t");
        q1.declareParameters("Integer t");
        try {

            final List<Entity> result = (List<Entity>) q1.execute(type.getCode());
            final List<Entity> models = EntityModelFactory.createEntities(null, result);
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


}
