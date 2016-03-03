package com.nimbits.client.model.hal;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Embedded_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getChildren(com.nimbits.client.model.hal.Embedded instance) /*-{
    return instance.@com.nimbits.client.model.hal.Embedded::children;
  }-*/;
  
  private static native void setChildren(com.nimbits.client.model.hal.Embedded instance, java.util.List value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Embedded::children = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.hal.Embedded instance) throws SerializationException {
    setChildren(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static com.nimbits.client.model.hal.Embedded instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.hal.Embedded();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.hal.Embedded instance) throws SerializationException {
    streamWriter.writeObject(getChildren(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.hal.Embedded_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Embedded_FieldSerializer.deserialize(reader, (com.nimbits.client.model.hal.Embedded)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Embedded_FieldSerializer.serialize(writer, (com.nimbits.client.model.hal.Embedded)object);
  }
  
}
