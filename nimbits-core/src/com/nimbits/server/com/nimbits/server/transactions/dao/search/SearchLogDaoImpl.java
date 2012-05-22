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

package com.nimbits.server.com.nimbits.server.transactions.dao.search;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.EMF;
import com.nimbits.server.orm.JpaSearchLog;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:14 PM
 */
public class SearchLogDaoImpl implements SearchLogTransactions {

    final String findSQL = "Select e from JpaSearchLog e where e.searchText = ?1";

    @Override
    public void addUpdateSearchLog(final String searchText) throws NimbitsException {

        if (readSearchLog(searchText) == null) {
             addSearchLog(searchText);
        } else {
            updateSearchLog(searchText);
        }


    }

    @Override
    public JpaSearchLog addSearchLog(final String searchText) {
        EntityManager em = EMF.getInstance();


        try {
            JpaSearchLog j = new JpaSearchLog(searchText);

            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(j);
            em.flush();
            tx.commit();



            return j;
        } finally {
            em.close();
        }
    }

    @Override
    public JpaSearchLog updateSearchLog(final String searchText) throws NimbitsException {
        EntityManager em = EMF.getInstance();
        JpaSearchLog retObj;
        try {
            retObj = null;
            JpaSearchLog response = (JpaSearchLog) em.createQuery(findSQL)
                    .setParameter(1, searchText)
                    .getSingleResult();


            if (response != null) {
                EntityTransaction tx = em.getTransaction();
                tx.begin();
                response.setSearchCount(response.getSearchCount() + 1);
                response.setTs((Timestamp) new Date());

                em.flush();
                tx.commit();
              return (response);

            } else {
                throw new NimbitsException("Could not update search log");
            }

        } catch (NoResultException ex) {
            retObj = null;
        } finally {
            em.close();
        }
        return retObj;
    }




    @Override
    public JpaSearchLog readSearchLog(final String searchText) {
        EntityManager em = EMF.getInstance();
        JpaSearchLog retObj;
        try {
            retObj = null;
            JpaSearchLog response = (JpaSearchLog) em.createQuery(findSQL)
                    .setParameter(1, searchText)
                    .getSingleResult();

            if (response != null) {
               return response;

            }

        } catch (NoResultException ex) {
            retObj = null;
        } finally {
            em.close();
        }
        return retObj;


    }

}
