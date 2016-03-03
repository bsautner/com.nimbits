package com.google.gwt.http.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RequestException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.http.client.RequestException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.google.gwt.http.client.RequestException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.http.client.RequestException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.http.client.RequestException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.http.client.RequestException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.http.client.RequestException_FieldSerializer.deserialize(reader, (com.google.gwt.http.client.RequestException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.http.client.RequestException_FieldSerializer.serialize(writer, (com.google.gwt.http.client.RequestException)object);
  }
  
}
