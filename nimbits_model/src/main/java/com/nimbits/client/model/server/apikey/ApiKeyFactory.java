package com.nimbits.client.model.server.apikey;

public class ApiKeyFactory {

    public static ApiKey createApiKey(final String value) {
        return new ApiKey(value);
    }

    public static ApiKey createEmptyKey() {
        return new ApiKey();
    }
}
