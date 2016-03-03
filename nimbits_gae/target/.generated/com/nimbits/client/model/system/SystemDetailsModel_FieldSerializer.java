package com.nimbits.client.model.system;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SystemDetailsModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getIsGAE(com.nimbits.client.model.system.SystemDetailsModel instance) /*-{
    return instance.@com.nimbits.client.model.system.SystemDetailsModel::isGAE;
  }-*/;
  
  private static native void setIsGAE(com.nimbits.client.model.system.SystemDetailsModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.system.SystemDetailsModel::isGAE = value;
  }-*/;
  
  private static native java.lang.String getVersion(com.nimbits.client.model.system.SystemDetailsModel instance) /*-{
    return instance.@com.nimbits.client.model.system.SystemDetailsModel::version;
  }-*/;
  
  private static native void setVersion(com.nimbits.client.model.system.SystemDetailsModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.system.SystemDetailsModel::version = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.system.SystemDetailsModel instance) throws SerializationException {
    setIsGAE(instance, streamReader.readBoolean());
    setVersion(instance, streamReader.readString());
    
  }
  
  public static com.nimbits.client.model.system.SystemDetailsModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.system.SystemDetailsModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.system.SystemDetailsModel instance) throws SerializationException {
    streamWriter.writeBoolean(getIsGAE(instance));
    streamWriter.writeString(getVersion(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.system.SystemDetailsModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.system.SystemDetailsModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.system.SystemDetailsModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.system.SystemDetailsModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.system.SystemDetailsModel)object);
  }
  
}
