package com.google.gwt.user.client.rpc.core.java.security;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DigestException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, java.security.DigestException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.security.GeneralSecurityException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static java.security.DigestException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new java.security.DigestException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, java.security.DigestException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.security.GeneralSecurityException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.core.java.security.DigestException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.security.DigestException_FieldSerializer.deserialize(reader, (java.security.DigestException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.security.DigestException_FieldSerializer.serialize(writer, (java.security.DigestException)object);
  }
  
}
