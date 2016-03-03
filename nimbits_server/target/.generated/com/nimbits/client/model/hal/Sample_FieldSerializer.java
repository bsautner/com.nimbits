package com.nimbits.client.model.hal;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Sample_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDescription(com.nimbits.client.model.hal.Sample instance) /*-{
    return instance.@com.nimbits.client.model.hal.Sample::description;
  }-*/;
  
  private static native void setDescription(com.nimbits.client.model.hal.Sample instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Sample::description = value;
  }-*/;
  
  private static native java.lang.String getHref(com.nimbits.client.model.hal.Sample instance) /*-{
    return instance.@com.nimbits.client.model.hal.Sample::href;
  }-*/;
  
  private static native void setHref(com.nimbits.client.model.hal.Sample instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Sample::href = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.hal.Sample instance) throws SerializationException {
    setDescription(instance, streamReader.readString());
    setHref(instance, streamReader.readString());
    
  }
  
  public static com.nimbits.client.model.hal.Sample instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.hal.Sample();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.hal.Sample instance) throws SerializationException {
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeString(getHref(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.hal.Sample_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Sample_FieldSerializer.deserialize(reader, (com.nimbits.client.model.hal.Sample)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Sample_FieldSerializer.serialize(writer, (com.nimbits.client.model.hal.Sample)object);
  }
  
}
