package com.nimbits.cloudplatform.client.model.simple;


public class SimpleValue<T> {

    private final T value;

    protected SimpleValue(final T aValue) {

        this.value = aValue;

    }

    public static <T> SimpleValue<T> getInstance(final T aValue) {
        return new SimpleValue<T>(aValue);
    }


    public T getValue() {
        return this.value;
    }


    public String toString() {
        return this.value.toString();
    }


    public static SimpleValue<String> getEmptyInstance() {
        return getInstance("");

    }

    public boolean isEmpty() {
        return value == null || value.toString().trim().length() == 0;
    }

}
