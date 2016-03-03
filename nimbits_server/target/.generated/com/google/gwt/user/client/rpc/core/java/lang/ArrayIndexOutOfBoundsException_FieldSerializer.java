package com.google.gwt.user.client.rpc.core.java.lang;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ArrayIndexOutOfBoundsException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, java.lang.ArrayIndexOutOfBoundsException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.IndexOutOfBoundsException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static java.lang.ArrayIndexOutOfBoundsException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new java.lang.ArrayIndexOutOfBoundsException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, java.lang.ArrayIndexOutOfBoundsException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.IndexOutOfBoundsException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.core.java.lang.ArrayIndexOutOfBoundsException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.ArrayIndexOutOfBoundsException_FieldSerializer.deserialize(reader, (java.lang.ArrayIndexOutOfBoundsException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.ArrayIndexOutOfBoundsException_FieldSerializer.serialize(writer, (java.lang.ArrayIndexOutOfBoundsException)object);
  }
  
}
