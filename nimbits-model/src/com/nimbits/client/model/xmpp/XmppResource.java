package com.nimbits.client.model.xmpp;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:39 PM
 */
public interface XmppResource extends Serializable {

    String getKey();

    String getEntity();
}
