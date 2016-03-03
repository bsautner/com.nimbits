package com.nimbits.client.model.hal;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Series_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getHref(com.nimbits.client.model.hal.Series instance) /*-{
    return instance.@com.nimbits.client.model.hal.Series::href;
  }-*/;
  
  private static native void setHref(com.nimbits.client.model.hal.Series instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Series::href = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.hal.Series instance) throws SerializationException {
    setHref(instance, streamReader.readString());
    
  }
  
  public static com.nimbits.client.model.hal.Series instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.hal.Series();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.hal.Series instance) throws SerializationException {
    streamWriter.writeString(getHref(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.hal.Series_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Series_FieldSerializer.deserialize(reader, (com.nimbits.client.model.hal.Series)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Series_FieldSerializer.serialize(writer, (com.nimbits.client.model.hal.Series)object);
  }
  
}
