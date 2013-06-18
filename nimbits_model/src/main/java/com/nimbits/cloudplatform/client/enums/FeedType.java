package com.nimbits.cloudplatform.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 4:06 PM
 */
public enum FeedType {
    error(0, "Errors"),
    status(1, "User Status"),
    system(2, "System Messages"),
    info(3, "Info"),
    data(4, "Subscription Data"),
    all(5, "Everything"),
    ;


    private static final Map<Integer, FeedType> lookup = new HashMap<Integer, FeedType>(5);

    static {
        for (FeedType s : EnumSet.allOf(FeedType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private FeedType(int code, String text) {
        this.code = code;
        this.text = text;


    }

    public int getCode() {
        return code;
    }

    public static FeedType get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }
}
