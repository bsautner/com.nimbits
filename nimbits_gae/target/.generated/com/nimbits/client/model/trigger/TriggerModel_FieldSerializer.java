package com.nimbits.client.model.trigger;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TriggerModel_FieldSerializer {
  private static native boolean getEnabled(com.nimbits.client.model.trigger.TriggerModel instance) /*-{
    return instance.@com.nimbits.client.model.trigger.TriggerModel::enabled;
  }-*/;
  
  private static native void setEnabled(com.nimbits.client.model.trigger.TriggerModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.trigger.TriggerModel::enabled = value;
  }-*/;
  
  private static native java.lang.String getTarget(com.nimbits.client.model.trigger.TriggerModel instance) /*-{
    return instance.@com.nimbits.client.model.trigger.TriggerModel::target;
  }-*/;
  
  private static native void setTarget(com.nimbits.client.model.trigger.TriggerModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.trigger.TriggerModel::target = value;
  }-*/;
  
  private static native java.lang.String getTrigger(com.nimbits.client.model.trigger.TriggerModel instance) /*-{
    return instance.@com.nimbits.client.model.trigger.TriggerModel::trigger;
  }-*/;
  
  private static native void setTrigger(com.nimbits.client.model.trigger.TriggerModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.trigger.TriggerModel::trigger = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.trigger.TriggerModel instance) throws SerializationException {
    setEnabled(instance, streamReader.readBoolean());
    setTarget(instance, streamReader.readString());
    setTrigger(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.trigger.TriggerModel instance) throws SerializationException {
    streamWriter.writeBoolean(getEnabled(instance));
    streamWriter.writeString(getTarget(instance));
    streamWriter.writeString(getTrigger(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
}
