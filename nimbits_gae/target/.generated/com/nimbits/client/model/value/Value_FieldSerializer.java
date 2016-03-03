package com.nimbits.client.model.value;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Value_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.value.Value instance) throws SerializationException {
    instance.d = (java.lang.Double) streamReader.readObject();
    instance.dx = streamReader.readString();
    instance.lg = (java.lang.Double) streamReader.readObject();
    instance.lt = (java.lang.Double) streamReader.readObject();
    instance.m = streamReader.readString();
    instance.st = (java.lang.Integer) streamReader.readObject();
    instance.t = (java.lang.Long) streamReader.readObject();
    
  }
  
  public static com.nimbits.client.model.value.Value instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.value.Value();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.value.Value instance) throws SerializationException {
    streamWriter.writeObject(instance.d);
    streamWriter.writeString(instance.dx);
    streamWriter.writeObject(instance.lg);
    streamWriter.writeObject(instance.lt);
    streamWriter.writeString(instance.m);
    streamWriter.writeObject(instance.st);
    streamWriter.writeObject(instance.t);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.value.Value_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.value.Value_FieldSerializer.deserialize(reader, (com.nimbits.client.model.value.Value)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.value.Value_FieldSerializer.serialize(writer, (com.nimbits.client.model.value.Value)object);
  }
  
}
