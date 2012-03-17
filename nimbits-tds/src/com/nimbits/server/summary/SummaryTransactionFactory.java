package com.nimbits.server.summary;

import com.nimbits.client.model.user.*;
import com.nimbits.server.dao.summary.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 11:20 AM
 */
public class SummaryTransactionFactory {

    public static SummaryTransactions getInstance(final User u) {
        return new SummaryDaoImpl(u);
    }

}
