package com.google.web.bindery.event.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class UmbrellaException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Set getCauses(com.google.web.bindery.event.shared.UmbrellaException instance) /*-{
    return instance.@com.google.web.bindery.event.shared.UmbrellaException::causes;
  }-*/;
  
  private static native void setCauses(com.google.web.bindery.event.shared.UmbrellaException instance, java.util.Set value) 
  /*-{
    instance.@com.google.web.bindery.event.shared.UmbrellaException::causes = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.google.web.bindery.event.shared.UmbrellaException instance) throws SerializationException {
    setCauses(instance, (java.util.Set) streamReader.readObject());
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.google.web.bindery.event.shared.UmbrellaException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.web.bindery.event.shared.UmbrellaException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.web.bindery.event.shared.UmbrellaException instance) throws SerializationException {
    streamWriter.writeObject(getCauses(instance));
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.web.bindery.event.shared.UmbrellaException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.web.bindery.event.shared.UmbrellaException_FieldSerializer.deserialize(reader, (com.google.web.bindery.event.shared.UmbrellaException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.web.bindery.event.shared.UmbrellaException_FieldSerializer.serialize(writer, (com.google.web.bindery.event.shared.UmbrellaException)object);
  }
  
}
