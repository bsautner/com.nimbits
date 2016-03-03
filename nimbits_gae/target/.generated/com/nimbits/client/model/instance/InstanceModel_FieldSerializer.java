package com.nimbits.client.model.instance;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class InstanceModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAdminEmail(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::adminEmail;
  }-*/;
  
  private static native void setAdminEmail(com.nimbits.client.model.instance.InstanceModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::adminEmail = value;
  }-*/;
  
  private static native java.lang.String getApiKey(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::apiKey;
  }-*/;
  
  private static native void setApiKey(com.nimbits.client.model.instance.InstanceModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::apiKey = value;
  }-*/;
  
  private static native java.lang.String getBaseUrl(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::baseUrl;
  }-*/;
  
  private static native void setBaseUrl(com.nimbits.client.model.instance.InstanceModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::baseUrl = value;
  }-*/;
  
  private static native boolean getIsDefault(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::isDefault;
  }-*/;
  
  private static native void setIsDefault(com.nimbits.client.model.instance.InstanceModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::isDefault = value;
  }-*/;
  
  private static native java.lang.String getProtocol(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::protocol;
  }-*/;
  
  private static native void setProtocol(com.nimbits.client.model.instance.InstanceModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::protocol = value;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getServerId(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::serverId;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setServerId(com.nimbits.client.model.instance.InstanceModel instance, long value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::serverId = value;
  }-*/;
  
  private static native boolean getSocketsEnabled(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::socketsEnabled;
  }-*/;
  
  private static native void setSocketsEnabled(com.nimbits.client.model.instance.InstanceModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::socketsEnabled = value;
  }-*/;
  
  private static native java.lang.String getVersion(com.nimbits.client.model.instance.InstanceModel instance) /*-{
    return instance.@com.nimbits.client.model.instance.InstanceModel::version;
  }-*/;
  
  private static native void setVersion(com.nimbits.client.model.instance.InstanceModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.instance.InstanceModel::version = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.instance.InstanceModel instance) throws SerializationException {
    setAdminEmail(instance, streamReader.readString());
    setApiKey(instance, streamReader.readString());
    setBaseUrl(instance, streamReader.readString());
    setIsDefault(instance, streamReader.readBoolean());
    setProtocol(instance, streamReader.readString());
    setServerId(instance, streamReader.readLong());
    setSocketsEnabled(instance, streamReader.readBoolean());
    setVersion(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.instance.InstanceModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.instance.InstanceModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.instance.InstanceModel instance) throws SerializationException {
    streamWriter.writeString(getAdminEmail(instance));
    streamWriter.writeString(getApiKey(instance));
    streamWriter.writeString(getBaseUrl(instance));
    streamWriter.writeBoolean(getIsDefault(instance));
    streamWriter.writeString(getProtocol(instance));
    streamWriter.writeLong(getServerId(instance));
    streamWriter.writeBoolean(getSocketsEnabled(instance));
    streamWriter.writeString(getVersion(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.instance.InstanceModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.instance.InstanceModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.instance.InstanceModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.instance.InstanceModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.instance.InstanceModel)object);
  }
  
}
