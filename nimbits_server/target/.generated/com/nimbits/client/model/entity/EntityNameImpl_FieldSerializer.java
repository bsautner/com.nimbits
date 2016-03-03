package com.nimbits.client.model.entity;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class EntityNameImpl_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.entity.EntityNameImpl instance) throws SerializationException {
    
    com.nimbits.client.model.common.impl.CommonIdentifierImpl_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.entity.EntityNameImpl instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.entity.EntityNameImpl::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.entity.EntityNameImpl instance) throws SerializationException {
    
    com.nimbits.client.model.common.impl.CommonIdentifierImpl_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.entity.EntityNameImpl_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.entity.EntityNameImpl_FieldSerializer.deserialize(reader, (com.nimbits.client.model.entity.EntityNameImpl)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.entity.EntityNameImpl_FieldSerializer.serialize(writer, (com.nimbits.client.model.entity.EntityNameImpl)object);
  }
  
}
