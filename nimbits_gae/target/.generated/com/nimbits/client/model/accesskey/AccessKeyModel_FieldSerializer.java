package com.nimbits.client.model.accesskey;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AccessKeyModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getCode(com.nimbits.client.model.accesskey.AccessKeyModel instance) /*-{
    return instance.@com.nimbits.client.model.accesskey.AccessKeyModel::code;
  }-*/;
  
  private static native void setCode(com.nimbits.client.model.accesskey.AccessKeyModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.accesskey.AccessKeyModel::code = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.accesskey.AccessKeyModel instance) throws SerializationException {
    setCode(instance, streamReader.readString());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.accesskey.AccessKeyModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.accesskey.AccessKeyModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.accesskey.AccessKeyModel instance) throws SerializationException {
    streamWriter.writeString(getCode(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.accesskey.AccessKeyModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.accesskey.AccessKeyModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.accesskey.AccessKeyModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.accesskey.AccessKeyModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.accesskey.AccessKeyModel)object);
  }
  
}
