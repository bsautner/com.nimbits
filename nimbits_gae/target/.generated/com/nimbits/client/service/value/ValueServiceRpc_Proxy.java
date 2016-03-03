package com.nimbits.client.service.value;

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

public class ValueServiceRpc_Proxy extends RemoteServiceProxy implements com.nimbits.client.service.value.ValueServiceRpcAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.nimbits.client.service.value.ValueServiceRpc";
  private static final String SERIALIZATION_POLICY ="77778FF7F7D9CA516FD70EF781FCE428";
  private static final com.nimbits.client.service.value.ValueServiceRpc_TypeSerializer SERIALIZER = new com.nimbits.client.service.value.ValueServiceRpc_TypeSerializer();
  
  public ValueServiceRpc_Proxy() {
    super(GWT.getModuleBaseURL(),
      "rpc/valueService", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void getChartTable(com.nimbits.client.model.user.User user, com.nimbits.client.model.entity.Entity entity, java.lang.Integer count, com.google.gwt.user.client.rpc.AsyncCallback asyncCallback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ValueServiceRpc_Proxy", "getChartTable");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 3);
      streamWriter.writeString("com.nimbits.client.model.user.User");
      streamWriter.writeString("com.nimbits.client.model.entity.Entity");
      streamWriter.writeString("java.lang.Integer/3438268394");
      streamWriter.writeObject(user);
      streamWriter.writeObject(entity);
      streamWriter.writeObject(count);
      helper.finish(asyncCallback, ResponseReader.STRING);
    } catch (SerializationException ex) {
      asyncCallback.onFailure(ex);
    }
  }
  
  public void getCurrentValuesRpc(java.util.Map entities, com.google.gwt.user.client.rpc.AsyncCallback async) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ValueServiceRpc_Proxy", "getCurrentValuesRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("java.util.Map");
      streamWriter.writeObject(entities);
      helper.finish(async, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      async.onFailure(ex);
    }
  }
  
  public void recordValueRpc(com.nimbits.client.model.entity.Entity point, com.nimbits.client.model.value.Value value, com.google.gwt.user.client.rpc.AsyncCallback asyncCallback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ValueServiceRpc_Proxy", "recordValueRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("com.nimbits.client.model.entity.Entity");
      streamWriter.writeString("com.nimbits.client.model.value.Value/1264306294");
      streamWriter.writeObject(point);
      streamWriter.writeObject(value);
      helper.finish(asyncCallback, ResponseReader.VOID);
    } catch (SerializationException ex) {
      asyncCallback.onFailure(ex);
    }
  }
  
  public void solveEquationRpc(com.nimbits.client.model.calculation.Calculation calculation, com.google.gwt.user.client.rpc.AsyncCallback async) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ValueServiceRpc_Proxy", "solveEquationRpc");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.nimbits.client.model.calculation.Calculation");
      streamWriter.writeObject(calculation);
      helper.finish(async, ResponseReader.OBJECT);
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
