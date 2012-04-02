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

package com.nimbits.server.dao.pointDescription;

import com.nimbits.client.model.entity.EntityDescription;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.server.EMF;
import com.nimbits.server.transactions.dao.EntityDescription.EntityJPATransactions;
import com.nimbits.server.jpa.JpaEntityDescription;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:59 PM
 */
public class EntityDescriptionDaoImpl implements EntityJPATransactions {
    final String uuidSQL = "select e from JpaEntityDescription e where e.uuid= ?1";

    @Override
    public List<EntityDescription> searchEntityDescription(final String searchText) {
        String sql = "select * from ENTITY_DESCRIPTIONS " +
                "    where " +
                "        MATCH (entity_name, entity_desc) " +
                "  AGAINST (?1 WITH QUERY EXPANSION)";

        EntityManager em = EMF.getInstance();

        try {

            List<EntityDescription> result = em.createNativeQuery(sql, JpaEntityDescription.class)
                    .setParameter(1, searchText)
                    .getResultList();
            List<EntityDescription> models = EntityModelFactory.createPointDescriptions(result);
            return models;
        } catch (Exception ex) {
            return null;
        } finally {
            em.close();
        }


    }

    @Override
    public EntityDescription addEntityDescription(final EntityDescription p) {
        EntityManager em = EMF.getInstance();

        try {
            JpaEntityDescription j = new JpaEntityDescription(p);

            EntityTransaction tx = em.getTransaction();

            tx.begin();
            em.persist(j);
            em.flush();
            tx.commit();

            EntityDescription retObj = EntityModelFactory.createPointDescription(j);

            return retObj;
        } finally {
            em.close();
        }


    }

    @Override
    public EntityDescription addUpdateEntityDescription(EntityDescription entityDescription) {

        final EntityManager em = EMF.getInstance();
        final EntityDescription retObj;

        final List result = em.createQuery(uuidSQL)
                .setParameter(1, entityDescription.getKey())
                .getResultList();

        if (result != null && result.size() > 0) {
            final JpaEntityDescription r = (JpaEntityDescription) result.get(0);
            final EntityTransaction tx = em.getTransaction();
            tx.begin();
            r.setPointDesc(entityDescription.getDesc());
            r.setEntityName(entityDescription.getName());
            r.setEntityType(entityDescription.getEntityType());
            em.flush();
            tx.commit();

            retObj = EntityModelFactory.createPointDescription(r);

        } else {
            retObj = addEntityDescription(entityDescription);
        }

        return retObj;
    }

    public EntityDescription getEntityDescriptionByUUID(final String uuid) {

        final EntityManager em = EMF.getInstance();
        final EntityDescription retObj;
        try {
            final List result = em.createQuery(uuidSQL)
                    .setParameter(1, uuid)
                    .getResultList();
            if (result.size() > 0) {
                retObj = EntityModelFactory.createPointDescription((EntityDescription) result.get(0));
            } else {
                retObj = null;
            }


            return retObj;
        } finally {
            em.close();
        }

    }

    @Override
    public void deleteEntityDescriptionByUUID(String uuid) {
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
