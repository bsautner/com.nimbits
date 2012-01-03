package com.nimbits.client.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/20/11
 * Time: 10:05 AM
 */
public enum ValueType {

    rawData(0), statisticShard(1), counter(2), calculated(3), mobile(4), manualEntry(5), emailBatch(6), xmpp(7), nonexistent(8);

    private static final Map<Integer, ValueType> lookup = new HashMap<Integer, ValueType>();

    static {
        for (ValueType s : EnumSet.allOf(ValueType.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;

    private ValueType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ValueType get(int code) {
        return lookup.get(code);
    }

}
