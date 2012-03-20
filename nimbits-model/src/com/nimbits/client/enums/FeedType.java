package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 4:06 PM
 */
public enum FeedType {
    error(0),
    system(1),
    info(2),
    data(3);


    private static final Map<Integer, FeedType> lookup = new HashMap<Integer, FeedType>();

    static {
        for (FeedType s : EnumSet.allOf(FeedType.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;

    private FeedType(int code) {
        this.code = code;

    }

    public int getCode() {
        return code;
    }

    public static FeedType get(int code) {
        return lookup.get(code);
    }

}
