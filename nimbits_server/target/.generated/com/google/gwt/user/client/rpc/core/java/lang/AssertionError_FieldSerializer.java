package com.google.gwt.user.client.rpc.core.java.lang;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AssertionError_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, java.lang.AssertionError instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Error_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static java.lang.AssertionError instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new java.lang.AssertionError();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, java.lang.AssertionError instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Error_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.core.java.lang.AssertionError_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.AssertionError_FieldSerializer.deserialize(reader, (java.lang.AssertionError)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.AssertionError_FieldSerializer.serialize(writer, (java.lang.AssertionError)object);
  }
  
}
