package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum EntityType {
    user(0, true,  "com.nimbits.server.orm.UserEntity"),
    point(1, true, "com.nimbits.server.orm.PointEntity"),
    category(2, false,  "com.nimbits.server.orm.SimpleEntity"),
    file(4, false, "com.nimbits.server.orm.SimpleEntity"),
    subscription(5, false, "com.nimbits.server.orm.SubscriptionEntity"),
    userConnection(6, false, "com.nimbits.server.orm.EntityStore"),
    calculation(7, false, "com.nimbits.server.orm.CalcEntity"),
    intelligence(8, false, "com.nimbits.server.orm.IntelligenceEntity"),
    feed(9, false,  "com.nimbits.server.orm.PointEntity"),
    resource(10, true, "com.nimbits.server.orm.XmppResourceEntity"),
    summary(11, false, "com.nimbits.server.orm.SummaryEntity"),
    instance(12, false, "com.nimbits.server.orm.EntityStore");
    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>(EntityType.values().length);

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final boolean uniqueNameFlag;
    private final String className;
    private EntityType(int code, boolean uniqueNameFlag, String className) {
        this.code = code;
        this.uniqueNameFlag = uniqueNameFlag;
        this.className = className;
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

    public String getClassName() {
        return className;
    }
}
