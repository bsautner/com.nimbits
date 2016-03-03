package com.nimbits.client.model.sync;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SyncModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAccessKey(com.nimbits.client.model.sync.SyncModel instance) /*-{
    return instance.@com.nimbits.client.model.sync.SyncModel::accessKey;
  }-*/;
  
  private static native void setAccessKey(com.nimbits.client.model.sync.SyncModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.sync.SyncModel::accessKey = value;
  }-*/;
  
  private static native java.lang.String getTargetInstance(com.nimbits.client.model.sync.SyncModel instance) /*-{
    return instance.@com.nimbits.client.model.sync.SyncModel::targetInstance;
  }-*/;
  
  private static native void setTargetInstance(com.nimbits.client.model.sync.SyncModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.sync.SyncModel::targetInstance = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.sync.SyncModel instance) throws SerializationException {
    setAccessKey(instance, streamReader.readString());
    setTargetInstance(instance, streamReader.readString());
    
    com.nimbits.client.model.trigger.TriggerModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.nimbits.client.model.sync.SyncModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.sync.SyncModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.sync.SyncModel instance) throws SerializationException {
    streamWriter.writeString(getAccessKey(instance));
    streamWriter.writeString(getTargetInstance(instance));
    
    com.nimbits.client.model.trigger.TriggerModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.sync.SyncModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.sync.SyncModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.sync.SyncModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.sync.SyncModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.sync.SyncModel)object);
  }
  
}
