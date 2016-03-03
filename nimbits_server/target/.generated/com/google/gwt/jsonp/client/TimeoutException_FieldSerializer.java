package com.google.gwt.jsonp.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TimeoutException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.jsonp.client.TimeoutException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.google.gwt.jsonp.client.TimeoutException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.jsonp.client.TimeoutException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.jsonp.client.TimeoutException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.jsonp.client.TimeoutException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.jsonp.client.TimeoutException_FieldSerializer.deserialize(reader, (com.google.gwt.jsonp.client.TimeoutException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.jsonp.client.TimeoutException_FieldSerializer.serialize(writer, (com.google.gwt.jsonp.client.TimeoutException)object);
  }
  
}
