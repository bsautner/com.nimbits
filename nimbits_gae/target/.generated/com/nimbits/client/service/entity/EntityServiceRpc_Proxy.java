package com.nimbits.client.service.entity;

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

public class EntityServiceRpc_Proxy extends RemoteServiceProxy implements com.nimbits.client.service.entity.EntityServiceRpcAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.nimbits.client.service.entity.EntityServiceRpc";
  private static final String SERIALIZATION_POLICY ="95202478AC291B5BB7CC7A7A7FBBDA57";
  private static final com.nimbits.client.service.entity.EntityServiceRpc_TypeSerializer SERIALIZER = new com.nimbits.client.service.entity.EntityServiceRpc_TypeSerializer();
  
  public EntityServiceRpc_Proxy() {
    super(GWT.getModuleBaseURL(),
      "rpc/entityService", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void addUpdateEntityRpc(com.nimbits.client.model.entity.Entity entity, com.google.gwt.user.client.rpc.AsyncCallback arg2) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("EntityServiceRpc_Proxy", "addUpdateEntityRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.nimbits.client.model.entity.Entity");
      streamWriter.writeObject(entity);
      helper.finish(arg2, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      arg2.onFailure(ex);
    }
  }
  
  public void copyEntity(com.nimbits.client.model.entity.Entity originalEntity, com.nimbits.client.model.entity.EntityName newName, com.google.gwt.user.client.rpc.AsyncCallback arg3) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("EntityServiceRpc_Proxy", "copyEntity");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("com.nimbits.client.model.entity.Entity");
      streamWriter.writeString("com.nimbits.client.model.entity.EntityName");
      streamWriter.writeObject(originalEntity);
      streamWriter.writeObject(newName);
      helper.finish(arg3, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      arg3.onFailure(ex);
    }
  }
  
  public void deleteEntityRpc(com.nimbits.client.model.entity.Entity entity, com.google.gwt.user.client.rpc.AsyncCallback arg2) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("EntityServiceRpc_Proxy", "deleteEntityRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.nimbits.client.model.entity.Entity");
      streamWriter.writeObject(entity);
      helper.finish(arg2, ResponseReader.VOID);
    } catch (SerializationException ex) {
      arg2.onFailure(ex);
    }
  }
  
  public void getEntitiesRpc(com.nimbits.client.model.user.User user, com.google.gwt.user.client.rpc.AsyncCallback arg2) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("EntityServiceRpc_Proxy", "getEntitiesRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.nimbits.client.model.user.User");
      streamWriter.writeObject(user);
      helper.finish(arg2, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      arg2.onFailure(ex);
    }
  }
  
  public void getEntityByKeyRpc(com.nimbits.client.model.user.User u, java.lang.String key, com.nimbits.client.enums.EntityType type, com.google.gwt.user.client.rpc.AsyncCallback arg4) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("EntityServiceRpc_Proxy", "getEntityByKeyRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 3);
      streamWriter.writeString("com.nimbits.client.model.user.User");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("com.nimbits.client.enums.EntityType/258941242");
      streamWriter.writeObject(u);
      streamWriter.writeString(key);
      streamWriter.writeObject(type);
      helper.finish(arg4, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      arg4.onFailure(ex);
    }
  }
  
  public void getEntityMapRpc(int type, int limit, com.google.gwt.user.client.rpc.AsyncCallback arg3) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("EntityServiceRpc_Proxy", "getEntityMapRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("I");
      streamWriter.writeString("I");
      streamWriter.writeInt(type);
      streamWriter.writeInt(limit);
      helper.finish(arg3, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      arg3.onFailure(ex);
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
