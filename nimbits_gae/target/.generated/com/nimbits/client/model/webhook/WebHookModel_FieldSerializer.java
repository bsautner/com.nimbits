package com.nimbits.client.model.webhook;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class WebHookModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getBodyChannel(com.nimbits.client.model.webhook.WebHookModel instance) /*-{
    return instance.@com.nimbits.client.model.webhook.WebHookModel::bodyChannel;
  }-*/;
  
  private static native void setBodyChannel(com.nimbits.client.model.webhook.WebHookModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.webhook.WebHookModel::bodyChannel = value;
  }-*/;
  
  private static native java.lang.String getDownloadTarget(com.nimbits.client.model.webhook.WebHookModel instance) /*-{
    return instance.@com.nimbits.client.model.webhook.WebHookModel::downloadTarget;
  }-*/;
  
  private static native void setDownloadTarget(com.nimbits.client.model.webhook.WebHookModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.webhook.WebHookModel::downloadTarget = value;
  }-*/;
  
  private static native boolean getEnabled(com.nimbits.client.model.webhook.WebHookModel instance) /*-{
    return instance.@com.nimbits.client.model.webhook.WebHookModel::enabled;
  }-*/;
  
  private static native void setEnabled(com.nimbits.client.model.webhook.WebHookModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.webhook.WebHookModel::enabled = value;
  }-*/;
  
  private static native int getMethod(com.nimbits.client.model.webhook.WebHookModel instance) /*-{
    return instance.@com.nimbits.client.model.webhook.WebHookModel::method;
  }-*/;
  
  private static native void setMethod(com.nimbits.client.model.webhook.WebHookModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.webhook.WebHookModel::method = value;
  }-*/;
  
  private static native int getPathChannel(com.nimbits.client.model.webhook.WebHookModel instance) /*-{
    return instance.@com.nimbits.client.model.webhook.WebHookModel::pathChannel;
  }-*/;
  
  private static native void setPathChannel(com.nimbits.client.model.webhook.WebHookModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.webhook.WebHookModel::pathChannel = value;
  }-*/;
  
  private static native java.lang.String getUrl(com.nimbits.client.model.webhook.WebHookModel instance) /*-{
    return instance.@com.nimbits.client.model.webhook.WebHookModel::url;
  }-*/;
  
  private static native void setUrl(com.nimbits.client.model.webhook.WebHookModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.webhook.WebHookModel::url = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.webhook.WebHookModel instance) throws SerializationException {
    setBodyChannel(instance, streamReader.readInt());
    setDownloadTarget(instance, streamReader.readString());
    setEnabled(instance, streamReader.readBoolean());
    setMethod(instance, streamReader.readInt());
    setPathChannel(instance, streamReader.readInt());
    setUrl(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.webhook.WebHookModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.webhook.WebHookModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.webhook.WebHookModel instance) throws SerializationException {
    streamWriter.writeInt(getBodyChannel(instance));
    streamWriter.writeString(getDownloadTarget(instance));
    streamWriter.writeBoolean(getEnabled(instance));
    streamWriter.writeInt(getMethod(instance));
    streamWriter.writeInt(getPathChannel(instance));
    streamWriter.writeString(getUrl(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.webhook.WebHookModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.webhook.WebHookModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.webhook.WebHookModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.webhook.WebHookModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.webhook.WebHookModel)object);
  }
  
}
