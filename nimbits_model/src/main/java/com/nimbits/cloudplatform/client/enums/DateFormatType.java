package com.nimbits.cloudplatform.client.enums;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/20/11
 * Time: 10:05 AM
 */
public enum DateFormatType {

    unixEpoch("unix"), json("json");
    private static final Map<String, DateFormatType> lookup = new HashMap<String, DateFormatType>(2);

    static {
        for (DateFormatType s : EnumSet.allOf(DateFormatType.class))
            lookup.put(s.code, s);
    }

    private final String code;

    private DateFormatType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DateFormatType get(String code) {
        return lookup.get(code);
    }

}
