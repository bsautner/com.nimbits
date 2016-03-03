package com.nimbits.client.model.connection;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConnectionModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getApprovalKey(com.nimbits.client.model.connection.ConnectionModel instance) /*-{
    return instance.@com.nimbits.client.model.connection.ConnectionModel::approvalKey;
  }-*/;
  
  private static native void setApprovalKey(com.nimbits.client.model.connection.ConnectionModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.connection.ConnectionModel::approvalKey = value;
  }-*/;
  
  private static native boolean getApproved(com.nimbits.client.model.connection.ConnectionModel instance) /*-{
    return instance.@com.nimbits.client.model.connection.ConnectionModel::approved;
  }-*/;
  
  private static native void setApproved(com.nimbits.client.model.connection.ConnectionModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.connection.ConnectionModel::approved = value;
  }-*/;
  
  private static native java.lang.String getTargetEmail(com.nimbits.client.model.connection.ConnectionModel instance) /*-{
    return instance.@com.nimbits.client.model.connection.ConnectionModel::targetEmail;
  }-*/;
  
  private static native void setTargetEmail(com.nimbits.client.model.connection.ConnectionModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.connection.ConnectionModel::targetEmail = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.connection.ConnectionModel instance) throws SerializationException {
    setApprovalKey(instance, streamReader.readString());
    setApproved(instance, streamReader.readBoolean());
    setTargetEmail(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.connection.ConnectionModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.connection.ConnectionModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.connection.ConnectionModel instance) throws SerializationException {
    streamWriter.writeString(getApprovalKey(instance));
    streamWriter.writeBoolean(getApproved(instance));
    streamWriter.writeString(getTargetEmail(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.connection.ConnectionModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.connection.ConnectionModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.connection.ConnectionModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.connection.ConnectionModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.connection.ConnectionModel)object);
  }
  
}
