package com.nimbits.client.model.user;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class UserModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getEmailAddress(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::emailAddress;
  }-*/;
  
  private static native void setEmailAddress(com.nimbits.client.model.user.UserModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::emailAddress = value;
  }-*/;
  
  private static native java.lang.Boolean getIsAdmin(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::isAdmin;
  }-*/;
  
  private static native void setIsAdmin(com.nimbits.client.model.user.UserModel instance, java.lang.Boolean value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::isAdmin = value;
  }-*/;
  
  private static native com.nimbits.client.model.user.LoginInfo getLoginInfo(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::loginInfo;
  }-*/;
  
  private static native void setLoginInfo(com.nimbits.client.model.user.UserModel instance, com.nimbits.client.model.user.LoginInfo value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::loginInfo = value;
  }-*/;
  
  private static native java.lang.String getPassword(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::password;
  }-*/;
  
  private static native void setPassword(com.nimbits.client.model.user.UserModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::password = value;
  }-*/;
  
  private static native java.lang.String getPasswordResetToken(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::passwordResetToken;
  }-*/;
  
  private static native void setPasswordResetToken(com.nimbits.client.model.user.UserModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::passwordResetToken = value;
  }-*/;
  
  private static native java.util.Date getPasswordResetTokenTimestamp(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::passwordResetTokenTimestamp;
  }-*/;
  
  private static native void setPasswordResetTokenTimestamp(com.nimbits.client.model.user.UserModel instance, java.util.Date value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::passwordResetTokenTimestamp = value;
  }-*/;
  
  private static native java.lang.String getPasswordSalt(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::passwordSalt;
  }-*/;
  
  private static native void setPasswordSalt(com.nimbits.client.model.user.UserModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::passwordSalt = value;
  }-*/;
  
  private static native java.lang.String getSource(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::source;
  }-*/;
  
  private static native void setSource(com.nimbits.client.model.user.UserModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::source = value;
  }-*/;
  
  private static native java.lang.String getToken(com.nimbits.client.model.user.UserModel instance) /*-{
    return instance.@com.nimbits.client.model.user.UserModel::token;
  }-*/;
  
  private static native void setToken(com.nimbits.client.model.user.UserModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.user.UserModel::token = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.user.UserModel instance) throws SerializationException {
    setEmailAddress(instance, streamReader.readString());
    setIsAdmin(instance, (java.lang.Boolean) streamReader.readObject());
    setLoginInfo(instance, (com.nimbits.client.model.user.LoginInfo) streamReader.readObject());
    setPassword(instance, streamReader.readString());
    setPasswordResetToken(instance, streamReader.readString());
    setPasswordResetTokenTimestamp(instance, (java.util.Date) streamReader.readObject());
    setPasswordSalt(instance, streamReader.readString());
    setSource(instance, streamReader.readString());
    setToken(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.user.UserModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.user.UserModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.user.UserModel instance) throws SerializationException {
    streamWriter.writeString(getEmailAddress(instance));
    streamWriter.writeObject(getIsAdmin(instance));
    streamWriter.writeObject(getLoginInfo(instance));
    streamWriter.writeString(getPassword(instance));
    streamWriter.writeString(getPasswordResetToken(instance));
    streamWriter.writeObject(getPasswordResetTokenTimestamp(instance));
    streamWriter.writeString(getPasswordSalt(instance));
    streamWriter.writeString(getSource(instance));
    streamWriter.writeString(getToken(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.user.UserModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.user.UserModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.user.UserModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.user.UserModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.user.UserModel)object);
  }
  
}
