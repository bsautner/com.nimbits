package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum EntityType {
    user(0, true),
    point(1, true),
    category(2, false),
    file(4, false),
    subscription(5, false),
    userConnection(6, false),
    calculation(7, false),
    intelligence(8, false),
    feed(9, false),
    resource(10, true),
    summary(11, false),
    instance(12, false);
    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>();

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;
    private final boolean uniqueNameFlag;
    private EntityType(int code, boolean uniqueNameFlag) {
        this.code = code;
        this.uniqueNameFlag = uniqueNameFlag;
    }

    public int getCode() {
        return code;
    }

    public static EntityType get(int code) {
        return lookup.get(code);
    }

    public boolean isUniqueNameFlag() {
        return uniqueNameFlag;
    }
}
