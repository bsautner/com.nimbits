package com.google.gwt.user.client.rpc.core.java.lang.annotation;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AnnotationFormatError_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, java.lang.annotation.AnnotationFormatError instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Error_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static java.lang.annotation.AnnotationFormatError instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new java.lang.annotation.AnnotationFormatError();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, java.lang.annotation.AnnotationFormatError instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Error_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.core.java.lang.annotation.AnnotationFormatError_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.annotation.AnnotationFormatError_FieldSerializer.deserialize(reader, (java.lang.annotation.AnnotationFormatError)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.annotation.AnnotationFormatError_FieldSerializer.serialize(writer, (java.lang.annotation.AnnotationFormatError)object);
  }
  
}
