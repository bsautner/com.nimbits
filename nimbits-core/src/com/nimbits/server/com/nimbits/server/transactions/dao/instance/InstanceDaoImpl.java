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

package com.nimbits.server.com.nimbits.server.transactions.dao.instance;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.instance.InstanceModelFactory;


import com.nimbits.server.com.nimbits.server.transactions.dao.instance.InstanceTransactions;
import com.nimbits.server.orm.JpaInstance;


import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:59 PM
 */
public class InstanceDaoImpl implements InstanceTransactions {

  final String findSQL = "Select e from JpaInstance e where e.baseUrl = ?1";

   @PersistenceContext
   private EntityManager em;


    @Override
    public Instance addUpdateInstance(final Instance instance) throws NimbitsException {

        if (readInstance(instance.getBaseUrl()) == null) {

            return addInstance(instance);
        } else {
            return updateInstance(instance);
        }


    }

    public Instance addInstance(Instance instance) throws NimbitsException {

      // EntityManager em = EMF.getInstance();

        try {
            Instance jpaInstance = new JpaInstance(instance);

           // EntityTransaction tx = em.getTransaction();
           // tx.begin();
            em.persist(jpaInstance);
            em.flush();
           // tx.commit();

            return InstanceModelFactory.createInstance(jpaInstance);
        } finally {
            em.close();
        }
    }

    public Instance updateInstance(Instance instance) throws NimbitsException {
       // EntityManager em = EMF.getInstance();
        Instance retObj;
        try {
            retObj = null;
            JpaInstance response = (JpaInstance) em.createQuery(findSQL)
                    .setParameter(1, instance.getBaseUrl())
                    .getSingleResult();


            if (response != null) {
                //EntityTransaction tx = em.getTransaction();
                //tx.begin();

                response.setBaseUrl(instance.getBaseUrl());
                response.setOwnerEmail(instance.getOwnerEmail());
                response.setServerVersion(instance.getVersion());
                response.setTs( new Timestamp(new Date().getTime()));

               // em.flush();
                //tx.commit();
                retObj = InstanceModelFactory.createInstance(response);

            } else {
                throw new NimbitsException("Could not update instance");
            }

        } catch (NoResultException ex) {
            retObj = null;
        } finally {
            em.close();
        }
        return retObj;
    }


    @Override
    public void deleteInstance(Instance instance) {
        //EntityManager em = EMF.getInstance();

        try {

            int id = instance.getId();
           // EntityTransaction tx = em.getTransaction();
            Instance s = em.find(JpaInstance.class, id);

            List<Entity> entityDescriptions
                    = em.createQuery("select e from JpaEntity  e " +
                    "where e.fkInstance = ?1").setParameter(1, id).getResultList();


         //   tx.begin();
            em.remove(s);
            for (Entity entityDescription : entityDescriptions) {
                em.remove(entityDescription);
            }

           // em.flush();
          //  tx.commit();

        } finally {
            em.close();
        }
    }

    @Override
    public Instance readInstance(final String hostUrl) throws NimbitsException {
       //EntityManager em = EMF.getInstance();
        Instance retObj;
        try {
            retObj = null;
            Instance response = (Instance) em.createQuery(findSQL)
                    .setParameter(1, hostUrl)
                    .getSingleResult();

            if (response != null) {
                retObj = InstanceModelFactory.createInstance(response);

            }

        } catch (NoResultException ex) {
            retObj = null;
        } finally {
           em.close();
        }
        return retObj;


    }

}
