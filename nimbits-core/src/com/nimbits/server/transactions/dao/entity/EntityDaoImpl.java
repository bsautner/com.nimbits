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

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.server.orm.JpaEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:59 PM
 */
@Repository("entityDao")
public class EntityDaoImpl implements EntityJPATransactions {
    private static final Logger log = Logger.getLogger(EntityDaoImpl.class.getName());
    @PersistenceContext
    EntityManager em;



    final String uuidSQL = "select e from JpaEntity e where e.uuid= ?1";

    @Override
    @SuppressWarnings("unchecked")
    public List<Entity> searchEntity(final String searchText) {
        String sql = "select * from ENTITY " +
                "    where " +
                "        MATCH (entity_name, entity_desc) " +
                "  AGAINST (?1 WITH QUERY EXPANSION)";



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
    public Entity addEntity(final Entity p, final String instanceUrl) throws NimbitsException {

       log.info("Adding Entity");
        try {
            JpaEntity j = new JpaEntity(p, instanceUrl);

            em.persist(j);

            return EntityModelFactory.createEntity(j);
        } catch (Exception ex) {

           throw new NimbitsException(ex);
        } finally {
            em.close();
        }


    }

    @Override
    public Entity addUpdateEntity(final Entity entityDescription, final String instanceUrl) throws NimbitsException {
        log.info("Add Update Entity");

        final Entity retObj;

        final List result = em.createQuery(uuidSQL)
                .setParameter(1, entityDescription.getKey())
                .getResultList();

        if (result != null && result.size() > 0) {
            final JpaEntity r = (JpaEntity) result.get(0);

            r.setDescription(entityDescription.getDescription());

            r.setName(entityDescription.getName());
            r.setEntityType(entityDescription.getEntityType() );


            retObj = EntityModelFactory.createEntity( r);

        } else {
            retObj = addEntity(entityDescription,  instanceUrl);
        }

        return retObj;
    }

    @Override
    public Entity getEntityByUUID(final String uuid) throws NimbitsException {


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
    public void deleteEntityByUUID(final String uuid) {

        try {
            List result = em.createQuery(uuidSQL)
                    .setParameter(1, uuid)
                    .getResultList();

            if (result.size() > 0) {

                em.remove(result.get(0));


            }
        } finally {
            em.close();
        }

    }


}
