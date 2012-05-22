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

package com.nimbits.server.com.nimbits.server.transactions.dao.entity;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.Entity;

import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.server.EMF;
import com.nimbits.server.orm.JpaEntity;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:59 PM
 */
public class EntityDaoImpl implements EntityJPATransactions {
    final String uuidSQL = "select e from JpaEntity e where e.uuid= ?1";

    @Override
    public List<Entity> searchEntity(final String searchText) {
        String sql = "select * from ENTITY_DESCRIPTIONS " +
                "    where " +
                "        MATCH (entity_name, entity_desc) " +
                "  AGAINST (?1 WITH QUERY EXPANSION)";

        EntityManager em = EMF.getInstance();

        try {

            List<Entity> result = em.createNativeQuery(sql, JpaEntity.class)
                    .setParameter(1, searchText)
                    .getResultList();
            List<Entity> models = new ArrayList<Entity>(result.size());
            for (Entity r : result) {
                models.add(EntityModelFactory.createEntity(r));
            }
            return models;
        } catch (Exception ex) {
            return null;
        } finally {
            em.close();
        }


    }

    @Override
    public Entity addEntity(final Entity p) throws NimbitsException {
        EntityManager em = EMF.getInstance();

        try {
            JpaEntity j = new JpaEntity(p);

            EntityTransaction tx = em.getTransaction();

            tx.begin();
            em.persist(j);
            em.flush();
            tx.commit();

            return EntityModelFactory.createEntity((Entity) j);
        } finally {
            em.close();
        }


    }

    @Override
    public Entity addUpdateEntity(Entity entityDescription) throws NimbitsException {

        final EntityManager em = EMF.getInstance();
        final Entity retObj;

        final List result = em.createQuery(uuidSQL)
                .setParameter(1, entityDescription.getKey())
                .getResultList();

        if (result != null && result.size() > 0) {
            final JpaEntity r = (JpaEntity) result.get(0);
            final EntityTransaction tx = em.getTransaction();
            tx.begin();
            r.setEntityDesc(entityDescription.getDescription());
            r.setEntityName(entityDescription.getName().getValue());
            r.setEntityType(entityDescription.getEntityType().getCode());
            em.flush();
            tx.commit();

            retObj = EntityModelFactory.createEntity((Entity) r);

        } else {
            retObj = addEntity(entityDescription);
        }

        return retObj;
    }

    @Override
    public Entity getEntityByUUID(final String uuid) throws NimbitsException {

        final EntityManager em = EMF.getInstance();
        final Entity retObj;
        try {
            final List result = em.createQuery(uuidSQL)
                    .setParameter(1, uuid)
                    .getResultList();
            retObj = result.isEmpty() ? null : EntityModelFactory.createEntity((Entity) result.get(0));


            return retObj;
        } finally {
            em.close();
        }

    }

    @Override
    public void deleteEntityByUUID(String uuid) {
        EntityManager em = EMF.getInstance();
        try {
            List result = em.createQuery(uuidSQL)
                    .setParameter(1, uuid)
                    .getResultList();

            if (result.size() > 0) {
                final EntityTransaction tx = em.getTransaction();
                tx.begin();
                em.remove(result.get(0));
                em.flush();
                tx.commit();

            }
        } finally {
            em.close();
        }

    }


}
