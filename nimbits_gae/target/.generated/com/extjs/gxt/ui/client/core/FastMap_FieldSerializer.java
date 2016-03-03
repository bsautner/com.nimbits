package com.extjs.gxt.ui.client.core;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FastMap_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static com.extjs.gxt.ui.client.core.FastMap instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.extjs.gxt.ui.client.core.FastMap();
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.extjs.gxt.ui.client.core.FastMap_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.extjs.gxt.ui.client.core.FastMap_CustomFieldSerializer.deserialize(reader, (com.extjs.gxt.ui.client.core.FastMap)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.extjs.gxt.ui.client.core.FastMap_CustomFieldSerializer.serialize(writer, (com.extjs.gxt.ui.client.core.FastMap)object);
  }
  
}
