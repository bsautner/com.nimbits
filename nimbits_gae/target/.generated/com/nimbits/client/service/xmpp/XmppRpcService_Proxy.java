package com.nimbits.client.service.xmpp;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.RpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.user.client.rpc.impl.RpcStatsContext;

public class XmppRpcService_Proxy extends RemoteServiceProxy implements com.nimbits.client.service.xmpp.XmppRpcServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.nimbits.client.service.xmpp.XmppRpcService";
  private static final String SERIALIZATION_POLICY ="F5C236C70B6D9FDAB54380A5828C8C34";
  private static final com.nimbits.client.service.xmpp.XmppRpcService_TypeSerializer SERIALIZER = new com.nimbits.client.service.xmpp.XmppRpcService_TypeSerializer();
  
  public XmppRpcService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "rpc/xmppService", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void sendInviteRpc(com.google.gwt.user.client.rpc.AsyncCallback async) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("XmppRpcService_Proxy", "sendInviteRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(async, ResponseReader.VOID);
    } catch (SerializationException ex) {
      async.onFailure(ex);
    }
  }
  @Override
  public SerializationStreamWriter createStreamWriter() {
    ClientSerializationStreamWriter toReturn =
      (ClientSerializationStreamWriter) super.createStreamWriter();
    if (getRpcToken() != null) {
      toReturn.addFlags(ClientSerializationStreamWriter.FLAG_RPC_TOKEN_INCLUDED);
    }
    return toReturn;
  }
  @Override
  protected void checkRpcTokenType(RpcToken token) {
    if (!(token instanceof com.google.gwt.user.client.rpc.XsrfToken)) {
      throw new RpcTokenException("Invalid RpcToken type: expected 'com.google.gwt.user.client.rpc.XsrfToken' but got '" + token.getClass() + "'");
    }
  }
}
