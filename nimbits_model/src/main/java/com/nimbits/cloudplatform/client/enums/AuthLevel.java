package com.nimbits.cloudplatform.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/10/12
 * Time: 2:26 PM
 */
public enum AuthLevel {

    restricted(1, "Read Only Public Data", false),
    readPoint(2, "Read Single Point", true),
    readWritePoint(3, "Read/Write To Single Point",true),
    readAll(4, "Read all points", true),
    readWriteAll(5, "Read/Write to all points", true),
    admin(6, "Admin - Read/Write/Delete all",false);



    private static final Map<Integer, AuthLevel> lookup = new HashMap<Integer, AuthLevel>(3);

    static {
        for (AuthLevel s : EnumSet.allOf(AuthLevel.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;
    private final boolean userVisible;

    private AuthLevel(int aCode, String aText, boolean userVisible) {
        this.code = aCode;
        this.text = aText;
        this.userVisible = userVisible;
    }

    public int getCode() {
        return code;
    }

    public static AuthLevel get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }

    public boolean isUserVisible() {
        return userVisible;
    }
}
