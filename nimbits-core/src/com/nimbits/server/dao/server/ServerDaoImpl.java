package com.nimbits.server.dao.server;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.EntityDescription;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModelFactory;
import com.nimbits.server.EMF;
import com.nimbits.server.orm.jpa.JpaServer;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:59 PM
 */
public class ServerDaoImpl implements ServerTransactions {

    final String findSQL = "Select e from JpaServer e where e.baseUrl = ?1";

    @Override
    public Server addUpdateServer(final Server server) throws NimbitsException {

        if (readServer(server.getBaseUrl()) == null) {

            return addServer(server);
        } else {
            return updateServer(server);
        }


    }

    public Server addServer(Server server) {
        EntityManager em = EMF.getInstance();


        try {
            JpaServer jpaServer = new JpaServer(server);

            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(jpaServer);
            em.flush();
            tx.commit();

            Server retObj = ServerModelFactory.createServer(jpaServer);

            return retObj;
        } finally {
            em.close();
        }
    }

    public Server updateServer(Server server) throws NimbitsException {
        EntityManager em = EMF.getInstance();
        Server retObj;
        try {
            retObj = null;
            JpaServer response = (JpaServer) em.createQuery(findSQL)
                    .setParameter(1, server.getBaseUrl())
                    .getSingleResult();


            if (response != null) {
                EntityTransaction tx = em.getTransaction();
                tx.begin();

                response.setBaseUrl(server.getBaseUrl());
                response.setOwnerEmail(server.getOwnerEmail());
                response.setServerVersion(server.getServerVersion());
                response.setTs(new Date());

                em.flush();
                tx.commit();
                retObj = ServerModelFactory.createServer(response);

            } else {
                throw new NimbitsException("Could not update server");
            }

        } catch (NoResultException ex) {
            retObj = null;
        } finally {
            em.close();
        }
        return retObj;
    }


    @Override
    public void deleteServer(Server server) {
        EntityManager em = EMF.getInstance();

        try {

            int id = server.getIdServer();
            EntityTransaction tx = em.getTransaction();
            Server s = em.find(JpaServer.class, id);

            List<EntityDescription> entityDescriptions
                    = em.createQuery("select e from JpaEntityDescription  e " +
                    "where e.fkServer = ?1").setParameter(1, id).getResultList();


            tx.begin();
            em.remove(s);
            for (EntityDescription entityDescription : entityDescriptions) {
                em.remove(entityDescription);
            }

            em.flush();
            tx.commit();

        } finally {
            em.close();
        }
    }

    @Override
    public Server readServer(final String hostUrl) {
        EntityManager em = EMF.getInstance();
        Server retObj;
        try {
            retObj = null;
            Server response = (Server) em.createQuery(findSQL)
                    .setParameter(1, hostUrl)
                    .getSingleResult();

            if (response != null) {
                retObj = ServerModelFactory.createServer(response);

            }

        } catch (NoResultException ex) {
            retObj = null;
        } finally {
            em.close();
        }
        return retObj;


    }

}
