package com.google.gwt.user.client.rpc.core.javax.validation;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConstraintDeclarationException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, javax.validation.ConstraintDeclarationException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.javax.validation.ValidationException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static javax.validation.ConstraintDeclarationException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new javax.validation.ConstraintDeclarationException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, javax.validation.ConstraintDeclarationException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.javax.validation.ValidationException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.core.javax.validation.ConstraintDeclarationException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.javax.validation.ConstraintDeclarationException_FieldSerializer.deserialize(reader, (javax.validation.ConstraintDeclarationException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.javax.validation.ConstraintDeclarationException_FieldSerializer.serialize(writer, (javax.validation.ConstraintDeclarationException)object);
  }
  
}
