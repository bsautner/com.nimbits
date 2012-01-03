package com.nimbits.server.dao.server;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public class ServerTransactionFactory {

    private static ServerTransactions daoInstance;

    public static ServerTransactions getInstance() {
        if (daoInstance == null) {
            daoInstance = new ServerDaoImpl();
        }
        return daoInstance;
    }



}
