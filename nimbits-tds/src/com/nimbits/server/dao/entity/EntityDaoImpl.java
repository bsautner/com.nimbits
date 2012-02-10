package com.nimbits.server.dao.entity;


import com.nimbits.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.orm.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.shared.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:46 AM
 */
public class EntityDaoImpl implements EntityTransactions {

    private final User user;

    @Override
    public Map<String, Entity> getEntityMap(EntityType type, boolean includeValues) {
        Map<String, Entity> retObj = new HashMap<String, Entity>();

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(EntityStore.class, "ownerUUID==b && entityType=t");
        q1.declareParameters("String b, Integer t");
        try {

            final List<Entity> result = (List<Entity>) q1.execute(user.getUuid(), type.getCode());
            List<Entity> models = EntityModelFactory.createEntities(result);
            for (Entity e : models) {
                if (includeValues) {
                    Point p = PointServiceFactory.getInstance().getPointByUUID(e.getUUID());
                    if (p!= null) {
                        Value v = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                        e.setValue(v);
                    }
                }
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
        Entity retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<String> uuids = user.getUserConnections();
        uuids.add(user.getUuid());

        final Query q1 = pm.newQuery(EntityStore.class, ":p.contains(ownerUUID)");// "ownerUUID==a");

        try {
            final List<Entity> result = (List<Entity>) q1.execute(uuids);
            return EntityModelFactory.createEntities(result);
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

                Transaction tx = pm.currentTransaction();
                tx.begin();
                List<Entity> entities = getEntityChildren(pm, c.get(0));
                pm.deletePersistentAll(entities);
                tx.commit();

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
