package com.nimbits.client.model.summary;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SummaryModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Date getLastProcessed(com.nimbits.client.model.summary.SummaryModel instance) /*-{
    return instance.@com.nimbits.client.model.summary.SummaryModel::lastProcessed;
  }-*/;
  
  private static native void setLastProcessed(com.nimbits.client.model.summary.SummaryModel instance, java.util.Date value) 
  /*-{
    instance.@com.nimbits.client.model.summary.SummaryModel::lastProcessed = value;
  }-*/;
  
  private static native java.lang.Long getSummaryIntervalMs(com.nimbits.client.model.summary.SummaryModel instance) /*-{
    return instance.@com.nimbits.client.model.summary.SummaryModel::summaryIntervalMs;
  }-*/;
  
  private static native void setSummaryIntervalMs(com.nimbits.client.model.summary.SummaryModel instance, java.lang.Long value) 
  /*-{
    instance.@com.nimbits.client.model.summary.SummaryModel::summaryIntervalMs = value;
  }-*/;
  
  private static native java.lang.Integer getSummaryType(com.nimbits.client.model.summary.SummaryModel instance) /*-{
    return instance.@com.nimbits.client.model.summary.SummaryModel::summaryType;
  }-*/;
  
  private static native void setSummaryType(com.nimbits.client.model.summary.SummaryModel instance, java.lang.Integer value) 
  /*-{
    instance.@com.nimbits.client.model.summary.SummaryModel::summaryType = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.summary.SummaryModel instance) throws SerializationException {
    setLastProcessed(instance, (java.util.Date) streamReader.readObject());
    setSummaryIntervalMs(instance, (java.lang.Long) streamReader.readObject());
    setSummaryType(instance, (java.lang.Integer) streamReader.readObject());
    
    com.nimbits.client.model.trigger.TriggerModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.summary.SummaryModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.summary.SummaryModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.summary.SummaryModel instance) throws SerializationException {
    streamWriter.writeObject(getLastProcessed(instance));
    streamWriter.writeObject(getSummaryIntervalMs(instance));
    streamWriter.writeObject(getSummaryType(instance));
    
    com.nimbits.client.model.trigger.TriggerModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.summary.SummaryModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.summary.SummaryModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.summary.SummaryModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.summary.SummaryModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.summary.SummaryModel)object);
  }
  
}
