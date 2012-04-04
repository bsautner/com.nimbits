package com.nimbits.server.relationship;

import com.nimbits.server.transactions.dao.relationship.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 10:03 AM
 */
public class RelationshipTransactionFactory {

    public static RelationshipTransaction getInstance() {

        return new RelationshipDAOImpl();

    }

}
