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
import com.nimbits.server.orm.JpaInstance;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
    final String instanceSQL = "select e from JpaInstance e where e.instanceUrl=?1";
    final String locationSQL = "select\n" +
            "\t`ENTITY`.`UUID`,\n" +
            "\t`ENTITY`.`ENTITY_NAME`,\n" +
            "\t`ENTITY`.`ENTITY_DESC`,\n" +
            "\t`ENTITY`.`ENTITY_TYPE`,\n" +
            "\tAsText(LOCATION),\n" +
            "\t`INSTANCE`.`INSTANCE_URL`,\n" +
            "\t`ENTITY`.`LOCATION` \n" +
            "from\n" +
            "\t`ENTITY` `ENTITY` \n" +
            "\t\tinner join `INSTANCE` `INSTANCE` \n" +
            "\t\ton `ENTITY`.`FK_INSTANCE` = `INSTANCE`.`ID_INSTANCE` \n" +
            "where\n" +
            "\t(`ENTITY`.`LOCATION` is not null)";
    @Override
    @SuppressWarnings("unchecked")
    public List<Entity> searchEntity(final String searchText) {
        String sql = "select * from ENTITY " +
                "    where " +
                "        MATCH (entity_name, entity_desc) " +
                "  AGAINST (?1 WITH QUERY EXPANSION) LIMIT 15";



        try {

            List<Entity> result = em.createNativeQuery(sql, JpaEntity.class)
                    .setParameter(1, searchText)
                    .getResultList();
            List<Entity> models = new ArrayList<Entity>(result.size());
            for (Entity r : result) {
                try {
                models.add(EntityModelFactory.createEntity(r));
                }
                catch (NullPointerException npr) {
                    log.severe(npr.getMessage());
                }
            }
            return models;
        } catch (Exception ex) {
            log.severe(ex.getMessage());
            return new ArrayList<Entity>(0);
        } finally {
            em.close();
        }


    }

    private List<JpaInstance> getInstance(String url) {
        try {

            return em.createQuery(
                    instanceSQL, JpaInstance.class)
                    .setParameter(1, url)
                    .getResultList();

        }
        finally {
            em.close();
        }

    }

    @Override
    public List<JpaEntity> getAllEntities() {

        return em.createQuery("select e from JpaEntity e", JpaEntity.class).getResultList();

    }

    @Override
    public void updateLocation(final Entity entity, final String location) {

        final String sql = "update ENTITY set LOCATION = " +
                "(GeomFromText('POINT(" +  location.replace(",", " ") + ")')) " +
                "where UUID='" + entity.getUUID() + "'";

        try {
            log.info("updating location for " + entity.getName().getValue());
            log.info(sql);
            em.createNativeQuery(sql).executeUpdate();
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public String getLocation(Entity entity) {
        String sql = "SELECT AsText(LOCATION) FROM ENTITY where UUID=?1";
        return  (String) em.createNativeQuery(sql)
                .setParameter(1, entity.getUUID()).getSingleResult();

    }

    @Override
    public List<String[]> getLocations() {

        List<Object[]> result = em.createNativeQuery(locationSQL).getResultList();
        List<String[]> retObj = new ArrayList<String[]>(3);
        for (Object[] r : result) {
            String[] s = new String[r.length];
            for (int i = 0; i < r.length; i++) {
                s[i] = String.valueOf(r[i]);
            }


            retObj.add(s);
        }
        return retObj;


    }

    @Override
    public Entity addEntity(final Entity p, final String instanceUrl) throws NimbitsException {

        log.info("Adding Entity");
        try {
            List<JpaInstance> instances = getInstance(instanceUrl);
            JpaInstance instance;
            if (instances.isEmpty()) {
                instance = new JpaInstance();
                instance.setInstanceUrl(instanceUrl);
            }
            else {
                instance = instances.get(0);
            }

            JpaEntity j = new JpaEntity(p, instance);
            log.info("created entity");
            em.persist(j);
            log.info("persisted");
            return EntityModelFactory.createEntity(j);
        }
        catch (Exception e) {
            log.severe("Exception occurred while persisting entity");

            if (e != null) {
                log.severe(e.getMessage());
                log.severe(ExceptionUtils.getStackTrace(e));
                throw  new NimbitsException(e);
            }
            else {
                throw new NimbitsException("Exception occurred, but exception was null. Super.");
            }

        }
        finally {
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
