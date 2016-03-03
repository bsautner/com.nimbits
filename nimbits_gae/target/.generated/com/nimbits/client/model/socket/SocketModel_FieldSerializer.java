package com.nimbits.client.model.socket;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SocketModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getExtraParams(com.nimbits.client.model.socket.SocketModel instance) /*-{
    return instance.@com.nimbits.client.model.socket.SocketModel::extraParams;
  }-*/;
  
  private static native void setExtraParams(com.nimbits.client.model.socket.SocketModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.socket.SocketModel::extraParams = value;
  }-*/;
  
  private static native java.lang.String getTargetApiKey(com.nimbits.client.model.socket.SocketModel instance) /*-{
    return instance.@com.nimbits.client.model.socket.SocketModel::targetApiKey;
  }-*/;
  
  private static native void setTargetApiKey(com.nimbits.client.model.socket.SocketModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.socket.SocketModel::targetApiKey = value;
  }-*/;
  
  private static native java.lang.String getTargetPath(com.nimbits.client.model.socket.SocketModel instance) /*-{
    return instance.@com.nimbits.client.model.socket.SocketModel::targetPath;
  }-*/;
  
  private static native void setTargetPath(com.nimbits.client.model.socket.SocketModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.socket.SocketModel::targetPath = value;
  }-*/;
  
  private static native java.lang.String getTargetUrl(com.nimbits.client.model.socket.SocketModel instance) /*-{
    return instance.@com.nimbits.client.model.socket.SocketModel::targetUrl;
  }-*/;
  
  private static native void setTargetUrl(com.nimbits.client.model.socket.SocketModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.socket.SocketModel::targetUrl = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.socket.SocketModel instance) throws SerializationException {
    setExtraParams(instance, streamReader.readString());
    setTargetApiKey(instance, streamReader.readString());
    setTargetPath(instance, streamReader.readString());
    setTargetUrl(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.socket.SocketModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.socket.SocketModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.socket.SocketModel instance) throws SerializationException {
    streamWriter.writeString(getExtraParams(instance));
    streamWriter.writeString(getTargetApiKey(instance));
    streamWriter.writeString(getTargetPath(instance));
    streamWriter.writeString(getTargetUrl(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.socket.SocketModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.socket.SocketModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.socket.SocketModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.socket.SocketModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.socket.SocketModel)object);
  }
  
}
