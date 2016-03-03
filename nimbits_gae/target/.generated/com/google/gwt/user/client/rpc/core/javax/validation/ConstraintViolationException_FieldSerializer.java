package com.google.gwt.user.client.rpc.core.javax.validation;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConstraintViolationException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return javax.validation.ConstraintViolationException_CustomFieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    javax.validation.ConstraintViolationException_CustomFieldSerializer.deserialize(reader, (javax.validation.ConstraintViolationException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    javax.validation.ConstraintViolationException_CustomFieldSerializer.serialize(writer, (javax.validation.ConstraintViolationException)object);
  }
  
}
