package com.nimbits.client.exception;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ValueException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.exception.ValueException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.nimbits.client.exception.ValueException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.exception.ValueException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.exception.ValueException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.exception.ValueException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.exception.ValueException_FieldSerializer.deserialize(reader, (com.nimbits.client.exception.ValueException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.exception.ValueException_FieldSerializer.serialize(writer, (com.nimbits.client.exception.ValueException)object);
  }
  
}
