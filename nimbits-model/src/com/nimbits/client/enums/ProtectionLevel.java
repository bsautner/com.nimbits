package com.nimbits.client.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/28/11
 * Time: 7:57 PM
 */
public enum ProtectionLevel {
    onlyMe(0),
    onlyConnection(1),
    everyone(2);


    private static final Map<Integer, ProtectionLevel> lookup = new HashMap<Integer, ProtectionLevel>();

    static {
        for (ProtectionLevel s : EnumSet.allOf(ProtectionLevel.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;

    private ProtectionLevel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ProtectionLevel get(int code) {
        return lookup.get(code);
    }
}
