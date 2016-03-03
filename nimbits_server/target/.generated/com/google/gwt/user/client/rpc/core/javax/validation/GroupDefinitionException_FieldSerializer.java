package com.google.gwt.user.client.rpc.core.javax.validation;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class GroupDefinitionException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, javax.validation.GroupDefinitionException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.javax.validation.ValidationException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static javax.validation.GroupDefinitionException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new javax.validation.GroupDefinitionException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, javax.validation.GroupDefinitionException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.javax.validation.ValidationException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.core.javax.validation.GroupDefinitionException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.javax.validation.GroupDefinitionException_FieldSerializer.deserialize(reader, (javax.validation.GroupDefinitionException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.javax.validation.GroupDefinitionException_FieldSerializer.serialize(writer, (javax.validation.GroupDefinitionException)object);
  }
  
}
