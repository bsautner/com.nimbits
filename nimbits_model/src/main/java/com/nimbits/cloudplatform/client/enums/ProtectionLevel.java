package com.nimbits.cloudplatform.client.enums;

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
    onlyMe(0, "Only Me"),
    onlyConnection(1, "Connections"),
    everyone(2, "Everyone");


    private static final Map<Integer, ProtectionLevel> lookup = new HashMap<Integer, ProtectionLevel>(3);

    static {
        for (ProtectionLevel s : EnumSet.allOf(ProtectionLevel.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private ProtectionLevel(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public static ProtectionLevel get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }


}
