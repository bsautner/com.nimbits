package com.nimbits.client.model.server.apikey;

public class ApiKeyFactory {

    public static AccessCode createApiKey(final String value) {
        return value == null ? createEmptyKey() :  new AccessCode(value);
    }

    public static AccessCode createEmptyKey() {
        return new AccessCode();
    }
}
