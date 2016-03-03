package com.nimbits.client.model.user;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class UserStatus_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.user.UserStatus instance) throws SerializationException {
    // Enum deserialization is handled via the instantiate method
  }
  
  public static com.nimbits.client.model.user.UserStatus instantiate(SerializationStreamReader streamReader) throws SerializationException {
    int ordinal = streamReader.readInt();
    com.nimbits.client.model.user.UserStatus[] values = com.nimbits.client.model.user.UserStatus.values();
    assert (ordinal >= 0 && ordinal < values.length);
    return values[ordinal];
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.user.UserStatus instance) throws SerializationException {
    assert (instance != null);
    streamWriter.writeInt(instance.ordinal());
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.user.UserStatus_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.user.UserStatus_FieldSerializer.deserialize(reader, (com.nimbits.client.model.user.UserStatus)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.user.UserStatus_FieldSerializer.serialize(writer, (com.nimbits.client.model.user.UserStatus)object);
  }
  
}
