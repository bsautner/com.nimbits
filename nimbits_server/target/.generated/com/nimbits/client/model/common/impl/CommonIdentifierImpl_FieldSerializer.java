package com.nimbits.client.model.common.impl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CommonIdentifierImpl_FieldSerializer {
  private static native java.lang.String getValue(com.nimbits.client.model.common.impl.CommonIdentifierImpl instance) /*-{
    return instance.@com.nimbits.client.model.common.impl.CommonIdentifierImpl::value;
  }-*/;
  
  private static native void setValue(com.nimbits.client.model.common.impl.CommonIdentifierImpl instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.common.impl.CommonIdentifierImpl::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.common.impl.CommonIdentifierImpl instance) throws SerializationException {
    setValue(instance, streamReader.readString());
    
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.common.impl.CommonIdentifierImpl instance) throws SerializationException {
    streamWriter.writeString(getValue(instance));
    
  }
  
}
