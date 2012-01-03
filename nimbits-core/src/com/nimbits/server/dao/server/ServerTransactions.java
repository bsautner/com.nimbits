package com.nimbits.server.dao.server;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.server.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public interface ServerTransactions {
    Server addUpdateServer(Server server) throws NimbitsException;
    void deleteServer(Server server);
    Server readServer(String hostUrl);

}
