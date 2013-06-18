package com.nimbits.cloudplatform.client.model.value.impl;

import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.ValueData;

import java.io.Serializable;


public class ValueDataModel implements ValueData, Serializable {

    private String data;

    protected ValueDataModel() {
        this.data = "";
    }

    public ValueDataModel(SimpleValue<String> data) {
        this.data = data.getValue();
    }

    public static ValueData getInstance(SimpleValue<String> data) {
       return  new ValueDataModel(data);

    }
    public static ValueData getEmptyInstance( ) {
        return  new ValueDataModel();


    }
    @Override
    public String getContent() {
        return this.data;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueDataModel that = (ValueDataModel) o;
        if (data==null) {
            data = "";
        }
        if (!data.equals(that.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
