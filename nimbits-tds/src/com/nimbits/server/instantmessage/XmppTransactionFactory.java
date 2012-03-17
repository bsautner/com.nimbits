package com.nimbits.server.instantmessage;

import com.nimbits.client.model.user.*;
import com.nimbits.server.dao.xmpp.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 1:18 PM
 */
public class XmppTransactionFactory {


    public static XmppTransaction getInstance(User u) {
        return new XmppDaoImpl(u);
    }

}
