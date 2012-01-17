package com.nimbits.server.dao.search;

import com.nimbits.client.exception.*;
import com.nimbits.server.*;
import com.nimbits.server.orm.jpa.*;

import javax.persistence.*;
import java.util.*;

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
                response.setTs(new Date());

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
