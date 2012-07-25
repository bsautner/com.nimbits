/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.transactions.dao.search;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.orm.JpaSearchLog;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:14 PM
 */
@Repository("searchDao")
public class SearchLogDaoImpl implements SearchLogTransactions {

    @PersistenceContext
    EntityManager em;

    final String findSQL = "Select e from JpaSearchLog e where e.searchText = ?1";

    @Override
    public void addUpdateSearchLog(final String searchText) throws NimbitsException {

        if (readSearchLog(searchText).isEmpty()) {
             addSearchLog(searchText);
        } else {
            updateSearchLog(searchText);
        }


    }

    @Override
    public JpaSearchLog addSearchLog(final String searchText) {


        try {
            JpaSearchLog j = new JpaSearchLog(searchText);
            em.persist(j);

            return j;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteSearchLog(String searchText) {
        try {

            List<JpaSearchLog> response =  em.createQuery(findSQL)
                    .setParameter(1, searchText).getResultList();


            if (! response.isEmpty()) {

                for (JpaSearchLog r : response) {
                    em.remove(r);

                }

            }

        } finally {
            em.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JpaSearchLog updateSearchLog(final String searchText) throws NimbitsException {


        try {

            List<JpaSearchLog> response =  em.createQuery(findSQL)
                    .setParameter(1, searchText).getResultList();


            if (! response.isEmpty()) {
                JpaSearchLog l = response.get(0);
                l.setSearchCount(l.getSearchCount() + 1);
                l.setTs(new Timestamp(new Date().getTime()));


              return (l);

            } else {
                throw new NimbitsException("Could not update search log");
            }

        } finally {
            em.close();
        }

    }




    @Override
    public List<JpaSearchLog> readSearchLog(final String searchText) {


        try {

            return  em.createQuery(findSQL).setParameter(1, searchText).getResultList();

        }  finally {
            em.close();
        }



    }

}
