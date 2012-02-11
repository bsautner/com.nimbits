package com.nimbits.server.dao.entity;


import com.nimbits.PMF;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.entity.EntityTransactions;
import com.nimbits.server.orm.entity.EntityStore;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
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
public class EntityDaoImpl implements EntityTransactions {

    private final User user;

    @Override
    public Map<String, Entity> getEntityMap(EntityType type) {
        Map<String, Entity> retObj = new HashMap<String, Entity>();

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "ownerUUID==b && entityType=t");
        q1.declareParameters("String b, Integer t");
        try {

            final List<Entity> result = (List<Entity>) q1.execute(user.getUuid(), type.getCode());
            List<Entity> models = EntityModelFactory.createEntities(user, result);
            for (Entity e : models) {

                retObj.put(e.getUUID(), e);
            }
            return retObj;

        } finally {
            pm.close();
        }


    }



    public EntityDaoImpl(User user) {
        this.user = user;
    }

    @Override
    public Entity addUpdateEntity(Entity entity) {
        Entity retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "entityUUID==b");
        q1.declareParameters("String b");
        q1.setRange(0, 1);

        try {

            final List<Entity> c = (List<Entity>) q1.execute(entity.getUUID());
            if (c.size() > 0) {
                Transaction tx = pm.currentTransaction();
                Entity result = c.get(0);
                tx.begin();
                result.setDescription(entity.getDescription());
                result.setName(entity.getName());
                result.setProtectionLevel(entity.getProtectionLevel());
                result.setParentUUID(entity.getParentUUID());
                tx.commit();
                return EntityModelFactory.createEntity(result);
            }
            else {
                Entity commit = new EntityStore(entity);
                pm.makePersistent(commit);
                switch (entity.getEntityType()) {
                    case point:
                        PointServiceFactory.getInstance().addPoint(user, entity);
                        break;
                }
                return EntityModelFactory.createEntity(commit);

            }
        } finally {
            pm.close();
        }



    }

    @Override
    public List<Entity> getEntities() {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<String> uuids = user.getUserConnections();
        uuids.add(user.getUuid());

        final Query q1 = pm.newQuery(EntityStore.class, ":p.contains(ownerUUID)");

        try {
            final List<Entity> result = (List<Entity>) q1.execute(uuids);
            return EntityModelFactory.createEntities(user, result);
        } finally {
            pm.close();
        }


    }

    private List<Entity> getEntityChildren(PersistenceManager pm, Entity entity) {

        final Query q1 = pm.newQuery(EntityStore.class, "parentUUID==b");
        q1.declareParameters("String b");
        final List<Entity> retObj = new ArrayList<Entity>();



        final List<Entity> c = (List<Entity>) q1.execute(entity.getUUID());
        if (c.size() > 0) {
            retObj.addAll(c);
            for (Entity e : c) {
                List<Entity> children = getEntityChildren(pm, e);
                retObj.addAll(children);
            }
        }

        return retObj;

    }

    @Override
    public void deleteEntity(Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "entityUUID==b");
        q1.declareParameters("String b");
        q1.setRange(0, 1);

        try {

            final List<Entity> c = (List<Entity>) q1.execute(entity.getUUID());

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
            final Query q1 = pm.newQuery(EntityStore.class, "entityUUID==b");
            q1.declareParameters("String b");
            q1.setRange(0, 1);
            final List<Entity> c = (List<Entity>) q1.execute(uuid);
            if (c.size() > 0) {

                Entity result = c.get(0);
                return EntityModelFactory.createEntity(result);

            }
            else {
                return null;
            }

        } finally {
            pm.close();
        }
    }
}
