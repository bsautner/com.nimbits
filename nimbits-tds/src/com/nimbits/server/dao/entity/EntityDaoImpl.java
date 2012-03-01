package com.nimbits.server.dao.entity;


import com.nimbits.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.orm.entity.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:46 AM
 */
public class EntityDaoImpl implements  EntityTransactions {

    private final User user;



    public EntityDaoImpl(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Entity> getEntityMap(EntityType type) {
        Map<String, Entity> retObj = new HashMap<String, Entity>();

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "owner==b && entityType==t");
        q1.declareParameters("String b, Integer t");
        try {

            final List<Entity> result = (List<Entity>) q1.execute(user.getUuid(), type.getCode());
            List<Entity> models = EntityModelFactory.createEntities(user, result);
            for (Entity e : models) {

                retObj.put(e.getEntity(), e);
            }
            return retObj;

        } finally {
            pm.close();
        }


    }


    @Override
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) {

        Map<EntityName, Entity> retObj = new HashMap<EntityName, Entity>();

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "owner==b && entityType==t");
        q1.declareParameters("String b, Integer t");
        try {

            final List<Entity> result = (List<Entity>) q1.execute(user.getUuid(), type.getCode());
            List<Entity> models = EntityModelFactory.createEntities(user, result);
            for (Entity e : models) {

                retObj.put(e.getName(), e);
            }
            return retObj;

        } finally {
            pm.close();
        }


    }



    @Override
    public List<Entity> getEntityChildren(Entity parentEntity, EntityType type) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            return getEntityChildren(pm, parentEntity, type);
        }
        finally {
            pm.close();
        }



    }


    @Override
    public Entity addUpdateEntity(Entity entity) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "owner==o && entity==b");
        q1.declareParameters("String o, String b");
        q1.setRange(0, 1);

        try {

            final List<Entity> c = (List<Entity>) q1.execute(user.getUuid(), entity.getEntity());
            if (c.size() > 0) {
                Transaction tx = pm.currentTransaction();
                Entity result = c.get(0);
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
                Entity commit = new EntityStore(entity);
                pm.makePersistent(commit);

                return EntityModelFactory.createEntity(user, commit);
            }
        } finally {
            pm.close();
        }



    }



    @Override
    public List<Entity> getEntities() {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<String> uuids = new ArrayList<String>();
        Map<String, Entity> connections = getEntityMap(EntityType.userConnection);

        uuids.add(user.getUuid());


        for (Entity e : connections.values()) {

            uuids.add(e.getEntity());
        }

        final Query q1 = pm.newQuery(EntityStore.class, ":p.contains(owner)");

        try {
            final List<Entity> result = (List<Entity>) q1.execute(uuids);
            return EntityModelFactory.createEntities(user, result);
        } finally {
            pm.close();
        }


    }

    private List<Entity> getEntityChildren(PersistenceManager pm, Entity entity) {

        final Query q1 = pm.newQuery(EntityStore.class, "parent==b");
        q1.declareParameters("String b");
        final List<Entity> retObj = new ArrayList<Entity>();



        final List<Entity> result = (List<Entity>) q1.execute(entity.getEntity());
        if (result.size() > 0) {
            retObj.addAll(result);
            for (Entity e : result) {
                List<Entity> children = getEntityChildren(pm, e);
                retObj.addAll(children);
            }
        }

        return retObj;

    }

    private List<Entity> getEntityChildren(PersistenceManager pm, Entity entity, EntityType type) {

        final Query q1 = pm.newQuery(EntityStore.class, "parent==b && entityType==t");
        q1.declareParameters("String b, Integer t");
        final List<Entity> retObj = new ArrayList<Entity>();



        final List<Entity> result = (List<Entity>) q1.execute(entity.getEntity(), type.getCode());
        if (result.size() > 0) {
            retObj.addAll(result);
            for (Entity e : result) {
                List<Entity> children = getEntityChildren(pm, e);
                retObj.addAll(children);
            }
        }

        return retObj;

    }



    @Override
    public void deleteEntity(Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "entity==b");
        q1.declareParameters("String b");
        q1.setRange(0, 1);

        try {

            final List<Entity> c = (List<Entity>) q1.execute(entity.getEntity());

            if (c.size() > 0) {
                List<Entity> entities = getEntityChildren(pm, c.get(0));
                entities.add(c.get(0));
                pm.deletePersistentAll(entities);
            }
        } finally {
            pm.close();
        }
    }

    @Override
    public Entity getEntityByUUID(String uuid) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            final Query q1 = pm.newQuery(EntityStore.class, "entity==b");
            q1.declareParameters("String b");
            q1.setRange(0, 1);
            final List<Entity> c = (List<Entity>) q1.execute(uuid);
            if (c.size() > 0) {

                Entity result = c.get(0);
                return EntityModelFactory.createEntity(user,result);

            }
            else {
                return null;
            }

        } finally {
            pm.close();
        }
    }

    @Override
    public Entity getEntityByName(EntityName name) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            final Query q1 = pm.newQuery(EntityStore.class, "name==b && owner==o");
            q1.declareParameters("String b, String o");
            q1.setRange(0, 1);
            final List<Entity> c = (List<Entity>) q1.execute(name.getValue(), user.getUuid());
            if (c.size() > 0) {

                Entity result = c.get(0);
                return EntityModelFactory.createEntity(user,  result);

            }
            else {
                return null;
            }

        } finally {
            pm.close();
        }
    }


}
