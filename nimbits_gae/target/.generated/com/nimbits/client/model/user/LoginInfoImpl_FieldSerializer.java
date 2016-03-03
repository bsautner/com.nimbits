package com.nimbits.client.model.user;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LoginInfoImpl_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getIsGAE(com.nimbits.client.model.user.LoginInfoImpl instance) /*-{
    return instance.@com.nimbits.client.model.user.LoginInfoImpl::isGAE;
  }-*/;
  
  private static native void setIsGAE(com.nimbits.client.model.user.LoginInfoImpl instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.user.LoginInfoImpl::isGAE = value;
  }-*/;
  
  private static native java.lang.String getLoginUrl(com.nimbits.client.model.user.LoginInfoImpl instance) /*-{
    return instance.@com.nimbits.client.model.user.LoginInfoImpl::loginUrl;
  }-*/;
  
  private static native void setLoginUrl(com.nimbits.client.model.user.LoginInfoImpl instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.LoginInfoImpl::loginUrl = value;
  }-*/;
  
  private static native java.lang.String getLogoutUrl(com.nimbits.client.model.user.LoginInfoImpl instance) /*-{
    return instance.@com.nimbits.client.model.user.LoginInfoImpl::logoutUrl;
  }-*/;
  
  private static native void setLogoutUrl(com.nimbits.client.model.user.LoginInfoImpl instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.LoginInfoImpl::logoutUrl = value;
  }-*/;
  
  private static native com.nimbits.client.model.user.UserStatus getUserStatus(com.nimbits.client.model.user.LoginInfoImpl instance) /*-{
    return instance.@com.nimbits.client.model.user.LoginInfoImpl::userStatus;
  }-*/;
  
  private static native void setUserStatus(com.nimbits.client.model.user.LoginInfoImpl instance, com.nimbits.client.model.user.UserStatus value) 
  /*-{
    instance.@com.nimbits.client.model.user.LoginInfoImpl::userStatus = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.user.LoginInfoImpl instance) throws SerializationException {
    setIsGAE(instance, streamReader.readBoolean());
    setLoginUrl(instance, streamReader.readString());
    setLogoutUrl(instance, streamReader.readString());
    setUserStatus(instance, (com.nimbits.client.model.user.UserStatus) streamReader.readObject());
    
  }
  
  public static com.nimbits.client.model.user.LoginInfoImpl instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.user.LoginInfoImpl();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.user.LoginInfoImpl instance) throws SerializationException {
    streamWriter.writeBoolean(getIsGAE(instance));
    streamWriter.writeString(getLoginUrl(instance));
    streamWriter.writeString(getLogoutUrl(instance));
    streamWriter.writeObject(getUserStatus(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.user.LoginInfoImpl_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.user.LoginInfoImpl_FieldSerializer.deserialize(reader, (com.nimbits.client.model.user.LoginInfoImpl)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.user.LoginInfoImpl_FieldSerializer.serialize(writer, (com.nimbits.client.model.user.LoginInfoImpl)object);
  }
  
}
