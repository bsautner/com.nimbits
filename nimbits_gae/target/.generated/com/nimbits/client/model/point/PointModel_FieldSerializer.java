package com.nimbits.client.model.point;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PointModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native double getDeltaAlarm(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::deltaAlarm;
  }-*/;
  
  private static native void setDeltaAlarm(com.nimbits.client.model.point.PointModel instance, double value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::deltaAlarm = value;
  }-*/;
  
  private static native boolean getDeltaAlarmOn(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::deltaAlarmOn;
  }-*/;
  
  private static native void setDeltaAlarmOn(com.nimbits.client.model.point.PointModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::deltaAlarmOn = value;
  }-*/;
  
  private static native int getDeltaSeconds(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::deltaSeconds;
  }-*/;
  
  private static native void setDeltaSeconds(com.nimbits.client.model.point.PointModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::deltaSeconds = value;
  }-*/;
  
  private static native int getExpire(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::expire;
  }-*/;
  
  private static native void setExpire(com.nimbits.client.model.point.PointModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::expire = value;
  }-*/;
  
  private static native int getFilterType(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::filterType;
  }-*/;
  
  private static native void setFilterType(com.nimbits.client.model.point.PointModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::filterType = value;
  }-*/;
  
  private static native double getFilterValue(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::filterValue;
  }-*/;
  
  private static native void setFilterValue(com.nimbits.client.model.point.PointModel instance, double value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::filterValue = value;
  }-*/;
  
  private static native double getHighAlarm(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::highAlarm;
  }-*/;
  
  private static native void setHighAlarm(com.nimbits.client.model.point.PointModel instance, double value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::highAlarm = value;
  }-*/;
  
  private static native boolean getHighAlarmOn(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::highAlarmOn;
  }-*/;
  
  private static native void setHighAlarmOn(com.nimbits.client.model.point.PointModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::highAlarmOn = value;
  }-*/;
  
  private static native boolean getIdleAlarmOn(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::idleAlarmOn;
  }-*/;
  
  private static native void setIdleAlarmOn(com.nimbits.client.model.point.PointModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::idleAlarmOn = value;
  }-*/;
  
  private static native boolean getIdleAlarmSent(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::idleAlarmSent;
  }-*/;
  
  private static native void setIdleAlarmSent(com.nimbits.client.model.point.PointModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::idleAlarmSent = value;
  }-*/;
  
  private static native int getIdleSeconds(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::idleSeconds;
  }-*/;
  
  private static native void setIdleSeconds(com.nimbits.client.model.point.PointModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::idleSeconds = value;
  }-*/;
  
  private static native boolean getInferLocation(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::inferLocation;
  }-*/;
  
  private static native void setInferLocation(com.nimbits.client.model.point.PointModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::inferLocation = value;
  }-*/;
  
  private static native double getLowAlarm(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::lowAlarm;
  }-*/;
  
  private static native void setLowAlarm(com.nimbits.client.model.point.PointModel instance, double value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::lowAlarm = value;
  }-*/;
  
  private static native boolean getLowAlarmOn(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::lowAlarmOn;
  }-*/;
  
  private static native void setLowAlarmOn(com.nimbits.client.model.point.PointModel instance, boolean value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::lowAlarmOn = value;
  }-*/;
  
  private static native int getPointType(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::pointType;
  }-*/;
  
  private static native void setPointType(com.nimbits.client.model.point.PointModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::pointType = value;
  }-*/;
  
  private static native int getPrecision(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::precision;
  }-*/;
  
  private static native void setPrecision(com.nimbits.client.model.point.PointModel instance, int value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::precision = value;
  }-*/;
  
  private static native java.lang.String getUnit(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::unit;
  }-*/;
  
  private static native void setUnit(com.nimbits.client.model.point.PointModel instance, java.lang.String value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::unit = value;
  }-*/;
  
  private static native com.nimbits.client.model.value.Value getValue(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::value;
  }-*/;
  
  private static native void setValue(com.nimbits.client.model.point.PointModel instance, com.nimbits.client.model.value.Value value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::value = value;
  }-*/;
  
  private static native java.util.List getValues(com.nimbits.client.model.point.PointModel instance) /*-{
    return instance.@com.nimbits.client.model.point.PointModel::values;
  }-*/;
  
  private static native void setValues(com.nimbits.client.model.point.PointModel instance, java.util.List value) 
  /*-{
    instance.@com.nimbits.client.model.point.PointModel::values = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.nimbits.client.model.point.PointModel instance) throws SerializationException {
    setDeltaAlarm(instance, streamReader.readDouble());
    setDeltaAlarmOn(instance, streamReader.readBoolean());
    setDeltaSeconds(instance, streamReader.readInt());
    setExpire(instance, streamReader.readInt());
    setFilterType(instance, streamReader.readInt());
    setFilterValue(instance, streamReader.readDouble());
    setHighAlarm(instance, streamReader.readDouble());
    setHighAlarmOn(instance, streamReader.readBoolean());
    setIdleAlarmOn(instance, streamReader.readBoolean());
    setIdleAlarmSent(instance, streamReader.readBoolean());
    setIdleSeconds(instance, streamReader.readInt());
    setInferLocation(instance, streamReader.readBoolean());
    setLowAlarm(instance, streamReader.readDouble());
    setLowAlarmOn(instance, streamReader.readBoolean());
    setPointType(instance, streamReader.readInt());
    setPrecision(instance, streamReader.readInt());
    setUnit(instance, streamReader.readString());
    setValue(instance, (com.nimbits.client.model.value.Value) streamReader.readObject());
    setValues(instance, (java.util.List) streamReader.readObject());
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.nimbits.client.model.point.PointModel instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.nimbits.client.model.point.PointModel::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, com.nimbits.client.model.point.PointModel instance) throws SerializationException {
    streamWriter.writeDouble(getDeltaAlarm(instance));
    streamWriter.writeBoolean(getDeltaAlarmOn(instance));
    streamWriter.writeInt(getDeltaSeconds(instance));
    streamWriter.writeInt(getExpire(instance));
    streamWriter.writeInt(getFilterType(instance));
    streamWriter.writeDouble(getFilterValue(instance));
    streamWriter.writeDouble(getHighAlarm(instance));
    streamWriter.writeBoolean(getHighAlarmOn(instance));
    streamWriter.writeBoolean(getIdleAlarmOn(instance));
    streamWriter.writeBoolean(getIdleAlarmSent(instance));
    streamWriter.writeInt(getIdleSeconds(instance));
    streamWriter.writeBoolean(getInferLocation(instance));
    streamWriter.writeDouble(getLowAlarm(instance));
    streamWriter.writeBoolean(getLowAlarmOn(instance));
    streamWriter.writeInt(getPointType(instance));
    streamWriter.writeInt(getPrecision(instance));
    streamWriter.writeString(getUnit(instance));
    streamWriter.writeObject(getValue(instance));
    streamWriter.writeObject(getValues(instance));
    
    com.nimbits.client.model.entity.EntityModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.nimbits.client.model.point.PointModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.nimbits.client.model.point.PointModel_FieldSerializer.deserialize(reader, (com.nimbits.client.model.point.PointModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.nimbits.client.model.point.PointModel_FieldSerializer.serialize(writer, (com.nimbits.client.model.point.PointModel)object);
  }
  
}
