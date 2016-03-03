package com.nimbits.client.model.calculation;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CalculationModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFormula(com.nimbits.client.model.calculation.CalculationModel instance) /*-{
    return instance.@com.nimbits.client.model.calculation.CalculationModel::formula;
  }-*/;
  
  private static native void setFormula(com.nimbits.client.model.calculation.CalculationModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.calculation.CalculationModel::formula = value;
  }-*/;
  
  private static native java.lang.String getX(com.nimbits.client.model.calculation.CalculationModel instance) /*-{
    return instance.@com.nimbits.client.model.calculation.CalculationModel::x;
  }-*/;
  
  private static native void setX(com.nimbits.client.model.calculation.CalculationModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.calculation.CalculationModel::x = value;
  }-*/;
  
  private static native java.lang.String getY(com.nimbits.client.model.calculation.CalculationModel instance) /*-{
    return instance.@com.nimbits.client.model.calculation.CalculationModel::y;
  }-*/;
  
  private static native void setY(com.nimbits.client.model.calculation.CalculationModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.calculation.CalculationModel::y = value;
  }-*/;
  
  private static native java.lang.String getZ(com.nimbits.client.model.calculation.CalculationModel instance) /*-{
    return instance.@com.nimbits.client.model.calculation.CalculationModel::z;
  }-*/;
  
  private static native void setZ(com.nimbits.client.model.calculation.CalculationModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.calculation.CalculationModel::z = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.calculation.CalculationModel instance) throws SerializationException {
    setFormula(instance, streamReader.readString());
    setX(instance, streamReader.readString());
    setY(instance, streamReader.readString());
    setZ(instance, streamReader.readString());
    
    com.nimbits.client.model.trigger.TriggerModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.calculation.CalculationModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.calculation.CalculationModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.calculation.CalculationModel instance) throws SerializationException {
    streamWriter.writeString(getFormula(instance));
    streamWriter.writeString(getX(instance));
    streamWriter.writeString(getY(instance));
    streamWriter.writeString(getZ(instance));
    
    com.nimbits.client.model.trigger.TriggerModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.calculation.CalculationModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.calculation.CalculationModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.calculation.CalculationModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.calculation.CalculationModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.calculation.CalculationModel)object);
  }
  
}
