package com.nimbits.server;

import javax.persistence.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:00 PM
 */
public class EMF {

    private static EntityManager entityManager;

    public static EntityManager getInstance() {
     //if (entityManager == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistenceUnit");
            entityManager= emf.createEntityManager();
      //  }
        return  entityManager;


    }


}
