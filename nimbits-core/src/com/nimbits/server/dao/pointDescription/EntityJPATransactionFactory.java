package com.nimbits.server.dao.pointDescription;

import com.nimbits.server.dao.EntityDescription.EntityJPATransactions;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public class EntityJPATransactionFactory {

    private static EntityJPATransactions daoInstance;

    public static EntityJPATransactions getInstance() {
        if (daoInstance == null) {
            daoInstance = new EntityDescriptionDaoImpl();
        }
        return daoInstance;
    }


}
