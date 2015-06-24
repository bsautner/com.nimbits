package com.nimbits.client.model.server.apikey;

import com.nimbits.client.model.common.SimpleValue;

import java.io.Serializable;

/**
 * Container for a Password KEY String
 */
public class AccessToken extends SimpleValue<String> implements Serializable {


    protected AccessToken(String aValue) {
        super(aValue);

    }

    protected AccessToken() {
        super("");

    }


    public static AccessToken getInstance(final String aValue) {
        return aValue == null ? createEmptyKey() : new AccessToken(aValue);
    }

    public static AccessToken createEmptyKey() {
        return new AccessToken();
    }


}
