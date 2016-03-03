package com.nimbits.client.model.entity;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class EntityModel_FieldSerializer {
  private static native java.lang.String getAction(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::action;
  }-*/;
  
  private static native void setAction(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::action = value;
  }-*/;
  
  private static native int getAlertType(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::alertType;
  }-*/;
  
  private static native void setAlertType(com.nimbits.client.model.entity.EntityModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::alertType = value;
  }-*/;
  
  private static native java.util.ArrayList getChildren(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::children;
  }-*/;
  
  private static native void setChildren(com.nimbits.client.model.entity.EntityModel instance, java.util.ArrayList value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::children = value;
  }-*/;
  
  private static native java.lang.String getDescription(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::description;
  }-*/;
  
  private static native void setDescription(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::description = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Embedded getEmbedded(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::embedded;
  }-*/;
  
  private static native void setEmbedded(com.nimbits.client.model.entity.EntityModel instance, com.nimbits.client.model.hal.Embedded value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::embedded = value;
  }-*/;
  
  private static native java.lang.String getId(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::id;
  }-*/;
  
  private static native void setId(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::id = value;
  }-*/;
  
  private static native java.lang.String getInstanceUrl(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::instanceUrl;
  }-*/;
  
  private static native void setInstanceUrl(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::instanceUrl = value;
  }-*/;
  
  private static native boolean getIsCached(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::isCached;
  }-*/;
  
  private static native void setIsCached(com.nimbits.client.model.entity.EntityModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::isCached = value;
  }-*/;
  
  private static native java.lang.String getKey(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::key;
  }-*/;
  
  private static native void setKey(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::key = value;
  }-*/;
  
  private static native com.nimbits.client.model.hal.Links getLinks(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::links;
  }-*/;
  
  private static native void setLinks(com.nimbits.client.model.entity.EntityModel instance, com.nimbits.client.model.hal.Links value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::links = value;
  }-*/;
  
  private static native java.lang.String getName(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::name;
  }-*/;
  
  private static native void setName(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::name = value;
  }-*/;
  
  private static native java.lang.String getOwner(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::owner;
  }-*/;
  
  private static native void setOwner(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::owner = value;
  }-*/;
  
  private static native java.lang.String getParent(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::parent;
  }-*/;
  
  private static native void setParent(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::parent = value;
  }-*/;
  
  private static native int getProtectionLevel(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::protectionLevel;
  }-*/;
  
  private static native void setProtectionLevel(com.nimbits.client.model.entity.EntityModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::protectionLevel = value;
  }-*/;
  
  private static native boolean getReadOnly(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::readOnly;
  }-*/;
  
  private static native void setReadOnly(com.nimbits.client.model.entity.EntityModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::readOnly = value;
  }-*/;
  
  private static native java.lang.String getUuid(com.nimbits.client.model.entity.EntityModel instance) /*-{
    return instance.@com.nimbits.client.model.entity.EntityModel::uuid;
  }-*/;
  
  private static native void setUuid(com.nimbits.client.model.entity.EntityModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.entity.EntityModel::uuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.entity.EntityModel instance) throws SerializationException {
    setAction(instance, streamReader.readString());
    setAlertType(instance, streamReader.readInt());
    setChildren(instance, (java.util.ArrayList) streamReader.readObject());
    setDescription(instance, streamReader.readString());
    setEmbedded(instance, (com.nimbits.client.model.hal.Embedded) streamReader.readObject());
    instance.entityType = streamReader.readInt();
    setId(instance, streamReader.readString());
    setInstanceUrl(instance, streamReader.readString());
    setIsCached(instance, streamReader.readBoolean());
    setKey(instance, streamReader.readString());
    setLinks(instance, (com.nimbits.client.model.hal.Links) streamReader.readObject());
    setName(instance, streamReader.readString());
    setOwner(instance, streamReader.readString());
    setParent(instance, streamReader.readString());
    setProtectionLevel(instance, streamReader.readInt());
    setReadOnly(instance, streamReader.readBoolean());
    setUuid(instance, streamReader.readString());
    
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.entity.EntityModel instance) throws SerializationException {
    streamWriter.writeString(getAction(instance));
    streamWriter.writeInt(getAlertType(instance));
    streamWriter.writeObject(getChildren(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeObject(getEmbedded(instance));
    streamWriter.writeInt(instance.entityType);
    streamWriter.writeString(getId(instance));
    streamWriter.writeString(getInstanceUrl(instance));
    streamWriter.writeBoolean(getIsCached(instance));
    streamWriter.writeString(getKey(instance));
    streamWriter.writeObject(getLinks(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getOwner(instance));
    streamWriter.writeString(getParent(instance));
    streamWriter.writeInt(getProtectionLevel(instance));
    streamWriter.writeBoolean(getReadOnly(instance));
    streamWriter.writeString(getUuid(instance));
    
  }
  
}
