package com.google.gwt.xml.client.impl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DOMParseException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getContents(com.google.gwt.xml.client.impl.DOMParseException instance) /*-{
    return instance.@com.google.gwt.xml.client.impl.DOMParseException::contents;
  }-*/;
  
  private static native void setContents(com.google.gwt.xml.client.impl.DOMParseException instance, java.lang.String value) 
  /*-{
    instance.@com.google.gwt.xml.client.impl.DOMParseException::contents = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.xml.client.impl.DOMParseException instance) throws SerializationException {
    setContents(instance, streamReader.readString());
    
    com.google.gwt.xml.client.DOMException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.google.gwt.xml.client.impl.DOMParseException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.xml.client.impl.DOMParseException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.xml.client.impl.DOMParseException instance) throws SerializationException {
    streamWriter.writeString(getContents(instance));
    
    com.google.gwt.xml.client.DOMException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.xml.client.impl.DOMParseException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.xml.client.impl.DOMParseException_FieldSerializer.deserialize(reader, (com.google.gwt.xml.client.impl.DOMParseException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.xml.client.impl.DOMParseException_FieldSerializer.serialize(writer, (com.google.gwt.xml.client.impl.DOMParseException)object);
  }
  
}
