package com.nimbits.client.service.user;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class UserServiceRpcException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.service.user.UserServiceRpcException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.nimbits.client.service.user.UserServiceRpcException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.service.user.UserServiceRpcException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.service.user.UserServiceRpcException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.service.user.UserServiceRpcException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.service.user.UserServiceRpcException_FieldSerializer.deserialize(reader, (com.nimbits.client.service.user.UserServiceRpcException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.service.user.UserServiceRpcException_FieldSerializer.serialize(writer, (com.nimbits.client.service.user.UserServiceRpcException)object);
  }
  
}
