package com.nimbits.client.model.server.apikey;

import com.nimbits.client.model.common.SimpleValue;

import java.io.Serializable;

public class ApiKey extends SimpleValue<String> implements Serializable {

    protected ApiKey(String aValue) {
        super(aValue);

    }

    protected ApiKey() {
        super("");

    }


    public static ApiKey getInstance(final String aValue) {
        return new ApiKey(aValue);
    }




}
