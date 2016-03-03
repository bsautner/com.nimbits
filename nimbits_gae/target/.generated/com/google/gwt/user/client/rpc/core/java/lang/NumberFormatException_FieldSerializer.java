package com.google.gwt.user.client.rpc.core.java.lang;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class NumberFormatException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, java.lang.NumberFormatException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.IllegalArgumentException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static java.lang.NumberFormatException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new java.lang.NumberFormatException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, java.lang.NumberFormatException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.IllegalArgumentException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.core.java.lang.NumberFormatException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.NumberFormatException_FieldSerializer.deserialize(reader, (java.lang.NumberFormatException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.NumberFormatException_FieldSerializer.serialize(writer, (java.lang.NumberFormatException)object);
  }
  
}
