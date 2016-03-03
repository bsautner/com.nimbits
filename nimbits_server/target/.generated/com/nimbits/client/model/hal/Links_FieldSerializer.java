package com.nimbits.client.model.hal;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Links_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.nimbits.client.model.hal.Children getChildren(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::children;
  }-*/;
  
  private static native void setChildren(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Children value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::children = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.DataTable getDatatable(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::datatable;
  }-*/;
  
  private static native void setDatatable(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.DataTable value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::datatable = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Nearby getNearby(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::nearby;
  }-*/;
  
  private static native void setNearby(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Nearby value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::nearby = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Next getNext(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::next;
  }-*/;
  
  private static native void setNext(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Next value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::next = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Parent getParent(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::parent;
  }-*/;
  
  private static native void setParent(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Parent value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::parent = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Sample getSample(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::sample;
  }-*/;
  
  private static native void setSample(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Sample value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::sample = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Self getSelf(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::self;
  }-*/;
  
  private static native void setSelf(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Self value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::self = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Series getSeries(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::series;
  }-*/;
  
  private static native void setSeries(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Series value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::series = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Snapshot getSnapshot(com.nimbits.client.model.hal.Links instance) /*-{
    return instance.@com.nimbits.client.model.hal.Links::snapshot;
  }-*/;
  
  private static native void setSnapshot(com.nimbits.client.model.hal.Links instance, com.nimbits.client.model.hal.Snapshot value) 
  /*-{
    instance.@com.nimbits.client.model.hal.Links::snapshot = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.hal.Links instance) throws SerializationException {
    setChildren(instance, (com.nimbits.client.model.hal.Children) streamReader.readObject());
    setDatatable(instance, (com.nimbits.client.model.hal.DataTable) streamReader.readObject());
    setNearby(instance, (com.nimbits.client.model.hal.Nearby) streamReader.readObject());
    setNext(instance, (com.nimbits.client.model.hal.Next) streamReader.readObject());
    setParent(instance, (com.nimbits.client.model.hal.Parent) streamReader.readObject());
    setSample(instance, (com.nimbits.client.model.hal.Sample) streamReader.readObject());
    setSelf(instance, (com.nimbits.client.model.hal.Self) streamReader.readObject());
    setSeries(instance, (com.nimbits.client.model.hal.Series) streamReader.readObject());
    setSnapshot(instance, (com.nimbits.client.model.hal.Snapshot) streamReader.readObject());
    
  }
  
  public static com.nimbits.client.model.hal.Links instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.hal.Links();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.hal.Links instance) throws SerializationException {
    streamWriter.writeObject(getChildren(instance));
    streamWriter.writeObject(getDatatable(instance));
    streamWriter.writeObject(getNearby(instance));
    streamWriter.writeObject(getNext(instance));
    streamWriter.writeObject(getParent(instance));
    streamWriter.writeObject(getSample(instance));
    streamWriter.writeObject(getSelf(instance));
    streamWriter.writeObject(getSeries(instance));
    streamWriter.writeObject(getSnapshot(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.hal.Links_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Links_FieldSerializer.deserialize(reader, (com.nimbits.client.model.hal.Links)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.hal.Links_FieldSerializer.serialize(writer, (com.nimbits.client.model.hal.Links)object);
  }
  
}
