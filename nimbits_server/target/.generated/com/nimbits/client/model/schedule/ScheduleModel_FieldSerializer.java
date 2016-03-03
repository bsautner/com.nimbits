package com.nimbits.client.model.schedule;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ScheduleModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.Boolean getEnabled(com.nimbits.client.model.schedule.ScheduleModel instance) /*-{
    return instance.@com.nimbits.client.model.schedule.ScheduleModel::enabled;
  }-*/;
  
  private static native void setEnabled(com.nimbits.client.model.schedule.ScheduleModel instance, java.lang.Boolean value) 
  /*-{
    instance.@com.nimbits.client.model.schedule.ScheduleModel::enabled = value;
  }-*/;
  
  private static native java.lang.Long getInterval(com.nimbits.client.model.schedule.ScheduleModel instance) /*-{
    return instance.@com.nimbits.client.model.schedule.ScheduleModel::interval;
  }-*/;
  
  private static native void setInterval(com.nimbits.client.model.schedule.ScheduleModel instance, java.lang.Long value) 
  /*-{
    instance.@com.nimbits.client.model.schedule.ScheduleModel::interval = value;
  }-*/;
  
  private static native java.lang.Long getLastProcessed(com.nimbits.client.model.schedule.ScheduleModel instance) /*-{
    return instance.@com.nimbits.client.model.schedule.ScheduleModel::lastProcessed;
  }-*/;
  
  private static native void setLastProcessed(com.nimbits.client.model.schedule.ScheduleModel instance, java.lang.Long value) 
  /*-{
    instance.@com.nimbits.client.model.schedule.ScheduleModel::lastProcessed = value;
  }-*/;
  
  private static native java.lang.String getSource(com.nimbits.client.model.schedule.ScheduleModel instance) /*-{
    return instance.@com.nimbits.client.model.schedule.ScheduleModel::source;
  }-*/;
  
  private static native void setSource(com.nimbits.client.model.schedule.ScheduleModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.schedule.ScheduleModel::source = value;
  }-*/;
  
  private static native java.lang.String getTarget(com.nimbits.client.model.schedule.ScheduleModel instance) /*-{
    return instance.@com.nimbits.client.model.schedule.ScheduleModel::target;
  }-*/;
  
  private static native void setTarget(com.nimbits.client.model.schedule.ScheduleModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.schedule.ScheduleModel::target = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.schedule.ScheduleModel instance) throws SerializationException {
    setEnabled(instance, (java.lang.Boolean) streamReader.readObject());
    setInterval(instance, (java.lang.Long) streamReader.readObject());
    setLastProcessed(instance, (java.lang.Long) streamReader.readObject());
    setSource(instance, streamReader.readString());
    setTarget(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.schedule.ScheduleModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.schedule.ScheduleModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.schedule.ScheduleModel instance) throws SerializationException {
    streamWriter.writeObject(getEnabled(instance));
    streamWriter.writeObject(getInterval(instance));
    streamWriter.writeObject(getLastProcessed(instance));
    streamWriter.writeString(getSource(instance));
    streamWriter.writeString(getTarget(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.schedule.ScheduleModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.schedule.ScheduleModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.schedule.ScheduleModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.schedule.ScheduleModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.schedule.ScheduleModel)object);
  }
  
}
