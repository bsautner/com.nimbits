package com.nimbits.client.model.server.apikey;

public class ApiKeyFactory {

    public static ApiKey createApiKey(final String value) {
        return value == null ? createEmptyKey() :  new ApiKey(value);
    }

    public static ApiKey createEmptyKey() {
        return new ApiKey();
    }
}
