package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum EntityType {
    user(0),
    point(1),
    category(2),
    file(4),
    subscription(5),
    userConnection(6),
    calculation(7);

    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>();

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;

    private EntityType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EntityType get(int code) {
        return lookup.get(code);
    }

}
