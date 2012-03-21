package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 4:06 PM
 */
public enum FeedType {
    error(0, "Errors"),
    system(1, "System Messages"),
    info(2, "Info"),
    data(3, "Subscription Data"),
    all(4, "Everything");


    private static final Map<Integer, FeedType> lookup = new HashMap<Integer, FeedType>();

    static {
        for (FeedType s : EnumSet.allOf(FeedType.class))
            lookup.put(s.getCode(), s);
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
