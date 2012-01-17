package com.nimbits.server.dao.search;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public class SearchLogTransactionFactory {

    private static SearchLogTransactions daoInstance;

    public static SearchLogTransactions getInstance() {
        if (daoInstance == null) {
            daoInstance = new SearchLogDaoImpl();
        }
        return daoInstance;
    }



}
