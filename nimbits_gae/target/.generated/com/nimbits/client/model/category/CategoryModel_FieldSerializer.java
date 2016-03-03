package com.nimbits.client.model.category;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CategoryModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.category.CategoryModel instance) throws SerializationException {
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.nimbits.client.model.category.CategoryModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.nimbits.client.model.category.CategoryModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.category.CategoryModel instance) throws SerializationException {
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.category.CategoryModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.category.CategoryModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.category.CategoryModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.category.CategoryModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.category.CategoryModel)object);
  }
  
}
