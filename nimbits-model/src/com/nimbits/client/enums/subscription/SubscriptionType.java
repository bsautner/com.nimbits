package com.nimbits.client.enums.subscription;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 1:36 PM
 */
public enum SubscriptionType {

   none(-1, "None"),
   anyAlert(1, "Any Alert"),
   high(2, "High Alert"),
   low(3, "Low Alert"),
   idle(4, "Idle Alert"),
   newValue(5, "New Values"),
   changed(6, "Any Updates"),
   deltaAlert(7, "Delta Alert");


    private static final Map<Integer, SubscriptionType> lookup = new HashMap<Integer, SubscriptionType>(7);

    static {
        for (SubscriptionType s : EnumSet.allOf(SubscriptionType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private SubscriptionType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static SubscriptionType get(int code) {
        return lookup.get(code);
    }
}
