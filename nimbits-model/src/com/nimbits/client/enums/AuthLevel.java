package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/10/12
 * Time: 2:26 PM
 */
public enum AuthLevel {

    restricted(1),
    readWrite(2),
    admin(3);


    private static final Map<Integer, AuthLevel> lookup = new HashMap<Integer, AuthLevel>();

    static {
        for (AuthLevel s : EnumSet.allOf(AuthLevel.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;

    private AuthLevel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static AuthLevel get(int code) {
        return lookup.get(code);
    }

}
