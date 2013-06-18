package com.nimbits.client.model.value.impl;

import com.nimbits.client.model.value.ValueData;

import java.io.Serializable;


public class ValueDataImpl implements ValueData, Serializable {

    private String data;

    protected ValueDataImpl() {
    }

    public ValueDataImpl(String data) {

        this.data = data == null ? "" : data;
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

        ValueDataImpl valueData = (ValueDataImpl) o;

        if (!data.equals(valueData.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
