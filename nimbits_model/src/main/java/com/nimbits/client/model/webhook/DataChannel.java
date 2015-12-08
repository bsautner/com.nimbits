package com.nimbits.client.model.webhook;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DataChannel  implements Serializable {
    none(0), number(1), data(2), meta(3), timestamp(4), gps(5), object(6);

    private static final Map<Integer, DataChannel> lookup = new HashMap<Integer, DataChannel>(5);

    static {
        for (DataChannel s : EnumSet.allOf(DataChannel.class))
            lookup.put(s.code, s);
    }

    private final int code;

    DataChannel(int i) {
        this.code = i;

    }

    public int getCode() {
        return code;
    }

    public static DataChannel lookup(int i) {
        return lookup.get(i);
    }
}
