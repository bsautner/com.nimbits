package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:54 AM
 */
public enum SummaryType {
    average(0), standardDeviation(1);

    private static final Map<Integer, SummaryType> lookup = new HashMap<Integer, SummaryType>();

    static {
        for (SummaryType s : EnumSet.allOf(SummaryType.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;

    private SummaryType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SummaryType get(int code) {
        return lookup.get(code);
    }

}
