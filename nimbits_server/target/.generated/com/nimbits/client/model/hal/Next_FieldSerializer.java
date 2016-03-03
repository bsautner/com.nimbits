package com.nimbits.client.model.hal;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Next_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getHref(com.nimbits.client.model.hal.Next instance) /*-{
    return instance.@com.nimbits.client.model.hal.Next::href;
  }-*/;
  
  private static native void setHref(com.nimbits.client.model.hal.Next instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Next::href = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.hal.Next instance) throws SerializationException {
    setHref(instance, streamReader.readString());
    
  }
  
  public static com.nimbits.client.model.hal.Next instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.hal.Next();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.hal.Next instance) throws SerializationException {
    streamWriter.writeString(getHref(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.hal.Next_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Next_FieldSerializer.deserialize(reader, (com.nimbits.client.model.hal.Next)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Next_FieldSerializer.serialize(writer, (com.nimbits.client.model.hal.Next)object);
  }
  
}
