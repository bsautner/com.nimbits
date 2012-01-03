package com.nimbits.xmpp;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 11/22/11
 * Time: 3:56 PM
 */
public interface XMPPClient {
    boolean connect() throws NimbitsException;

    void sendMessage(final String outgoingMessage);

    void sendValue(PointName pointName, Value value);

    void addMessageReceivedListeners(final XMPPClientImpl.MessageReceivedListener listener);

    void disconnect();
}
