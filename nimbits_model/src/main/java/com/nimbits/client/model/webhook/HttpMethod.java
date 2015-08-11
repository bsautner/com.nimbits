package com.nimbits.client.model.webhook;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum HttpMethod implements Serializable {
    POST(1), GET(2), DELETE(3), PUT(4);

    private static final Map<Integer, HttpMethod> lookup = new HashMap<Integer, HttpMethod>(4);

    static {
        for (HttpMethod s : EnumSet.allOf(HttpMethod.class))
            lookup.put(s.code, s);
    }

    private final int code;

    HttpMethod(int i) {
        this.code = i;

    }

    public int getCode() {
        return code;
    }

    public static HttpMethod lookup(int i) {
        return lookup.get(i);
    }
}
