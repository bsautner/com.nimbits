package com.nimbits.server.transactions.dao.client.impl;

import com.nimbits.client.enums.client.CommunicationType;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.orm.JpaClient;
import com.nimbits.server.orm.JpaClientCommunication;
import com.nimbits.server.transactions.dao.client.ClientDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

@Repository("clientDao")
public class ClientDaoImpl implements ClientDao {
    private static final Logger log = Logger.getLogger(ClientDaoImpl.class.getName());

    final String instanceSQL = "select e from JpaClient e where e.contactEmail=?1";

    @PersistenceContext
    EntityManager em;


   @Override
   public void addClientCommunication(final EmailAddress emailAddress,
                                      final String contactPhone,
                                      final String companyName,
                                      final String clientName,
                                      final String message,
                                      final CommunicationType type) {

       log.info("Adding client communication");
       try {
           List<JpaClient> clients = getClient(emailAddress);
           JpaClient client;
           if (clients.isEmpty()) {
              client = new JpaClient(emailAddress, contactPhone, companyName, clientName);
           }
           else {
               client = clients.get(0);
               client.setContactPhone(contactPhone);
               client.setCompanyName(companyName);
               client.setClientName(clientName);
           }

           JpaClientCommunication j = new JpaClientCommunication(client, message, type);
           log.info("created client comm");
           em.persist(j);
           log.info("persisted");

       }

       finally {
           em.close();
       }

   }

    private List<JpaClient> getClient(final EmailAddress emailAddress) {
        try {

            return em.createQuery(
                    instanceSQL, JpaClient.class)
                    .setParameter(1, emailAddress.getValue())
                    .getResultList();

        }
        finally {
            em.close();
        }

    }

}
