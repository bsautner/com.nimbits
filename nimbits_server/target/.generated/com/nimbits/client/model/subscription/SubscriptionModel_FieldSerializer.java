package com.nimbits.client.model.subscription;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SubscriptionModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getEnabled(com.nimbits.client.model.subscription.SubscriptionModel instance) /*-{
    return instance.@com.nimbits.client.model.subscription.SubscriptionModel::enabled;
  }-*/;
  
  private static native void setEnabled(com.nimbits.client.model.subscription.SubscriptionModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.subscription.SubscriptionModel::enabled = value;
  }-*/;
  
  private static native int getMaxRepeat(com.nimbits.client.model.subscription.SubscriptionModel instance) /*-{
    return instance.@com.nimbits.client.model.subscription.SubscriptionModel::maxRepeat;
  }-*/;
  
  private static native void setMaxRepeat(com.nimbits.client.model.subscription.SubscriptionModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.subscription.SubscriptionModel::maxRepeat = value;
  }-*/;
  
  private static native boolean getNotifyFormatJson(com.nimbits.client.model.subscription.SubscriptionModel instance) /*-{
    return instance.@com.nimbits.client.model.subscription.SubscriptionModel::notifyFormatJson;
  }-*/;
  
  private static native void setNotifyFormatJson(com.nimbits.client.model.subscription.SubscriptionModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.subscription.SubscriptionModel::notifyFormatJson = value;
  }-*/;
  
  private static native int getNotifyMethod(com.nimbits.client.model.subscription.SubscriptionModel instance) /*-{
    return instance.@com.nimbits.client.model.subscription.SubscriptionModel::notifyMethod;
  }-*/;
  
  private static native void setNotifyMethod(com.nimbits.client.model.subscription.SubscriptionModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.subscription.SubscriptionModel::notifyMethod = value;
  }-*/;
  
  private static native java.lang.String getSubscribedEntity(com.nimbits.client.model.subscription.SubscriptionModel instance) /*-{
    return instance.@com.nimbits.client.model.subscription.SubscriptionModel::subscribedEntity;
  }-*/;
  
  private static native void setSubscribedEntity(com.nimbits.client.model.subscription.SubscriptionModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.subscription.SubscriptionModel::subscribedEntity = value;
  }-*/;
  
  private static native int getSubscriptionType(com.nimbits.client.model.subscription.SubscriptionModel instance) /*-{
    return instance.@com.nimbits.client.model.subscription.SubscriptionModel::subscriptionType;
  }-*/;
  
  private static native void setSubscriptionType(com.nimbits.client.model.subscription.SubscriptionModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.subscription.SubscriptionModel::subscriptionType = value;
  }-*/;
  
  private static native java.lang.String getTarget(com.nimbits.client.model.subscription.SubscriptionModel instance) /*-{
    return instance.@com.nimbits.client.model.subscription.SubscriptionModel::target;
  }-*/;
  
  private static native void setTarget(com.nimbits.client.model.subscription.SubscriptionModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.subscription.SubscriptionModel::target = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.subscription.SubscriptionModel instance) throws SerializationException {
    setEnabled(instance, streamReader.readBoolean());
    setMaxRepeat(instance, streamReader.readInt());
    setNotifyFormatJson(instance, streamReader.readBoolean());
    setNotifyMethod(instance, streamReader.readInt());
    setSubscribedEntity(instance, streamReader.readString());
    setSubscriptionType(instance, streamReader.readInt());
    setTarget(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.subscription.SubscriptionModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.subscription.SubscriptionModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.subscription.SubscriptionModel instance) throws SerializationException {
    streamWriter.writeBoolean(getEnabled(instance));
    streamWriter.writeInt(getMaxRepeat(instance));
    streamWriter.writeBoolean(getNotifyFormatJson(instance));
    streamWriter.writeInt(getNotifyMethod(instance));
    streamWriter.writeString(getSubscribedEntity(instance));
    streamWriter.writeInt(getSubscriptionType(instance));
    streamWriter.writeString(getTarget(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.subscription.SubscriptionModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.subscription.SubscriptionModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.subscription.SubscriptionModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.subscription.SubscriptionModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.subscription.SubscriptionModel)object);
  }
  
}
