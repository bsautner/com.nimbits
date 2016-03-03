package com.nimbits.client.model.hal;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class EntityChild_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.nimbits.client.model.hal.Links getLinks(com.nimbits.client.model.hal.EntityChild instance) /*-{
    return instance.@com.nimbits.client.model.hal.EntityChild::links;
  }-*/;
  
  private static native void setLinks(com.nimbits.client.model.hal.EntityChild instance, com.nimbits.client.model.hal.Links value) 
  /*-{
    instance.@com.nimbits.client.model.hal.EntityChild::links = value;
  }-*/;
  
  private static native java.lang.String getName(com.nimbits.client.model.hal.EntityChild instance) /*-{
    return instance.@com.nimbits.client.model.hal.EntityChild::name;
  }-*/;
  
  private static native void setName(com.nimbits.client.model.hal.EntityChild instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.hal.EntityChild::name = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.hal.EntityChild instance) throws SerializationException {
    setLinks(instance, (com.nimbits.client.model.hal.Links) streamReader.readObject());
    setName(instance, streamReader.readString());
    
  }
  
  public static com.nimbits.client.model.hal.EntityChild instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.hal.EntityChild();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.hal.EntityChild instance) throws SerializationException {
    streamWriter.writeObject(getLinks(instance));
    streamWriter.writeString(getName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.hal.EntityChild_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.hal.EntityChild_FieldSerializer.deserialize(reader, (com.nimbits.client.model.hal.EntityChild)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.hal.EntityChild_FieldSerializer.serialize(writer, (com.nimbits.client.model.hal.EntityChild)object);
  }
  
}
