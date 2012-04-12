package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum EntityType {
    user(0, true, true, 0, "com.nimbits.server.orm.UserEntity"),
    point(1, true,true,2, "com.nimbits.server.orm.PointEntity"),
    category(2, false,true, 1, "com.nimbits.server.orm.CategoryEntity"),
    file(4, false,true,3, "com.nimbits.server.orm.FileEntity"),
    subscription(5, false,true, 4, "com.nimbits.server.orm.SubscriptionEntity"),
    userConnection(6, true,true, 5, "com.nimbits.server.orm.ConnectionEntity"),
    calculation(7, false,true, 6, "com.nimbits.server.orm.CalcEntity"),
    intelligence(8, false,true, 7, "com.nimbits.server.orm.IntelligenceEntity"),
    feed(9, false,false,  8, "com.nimbits.server.orm.PointEntity"),
    resource(10, true,true, 9, "com.nimbits.server.orm.XmppResourceEntity"),
    summary(11, false, true, 10,"com.nimbits.server.orm.SummaryEntity"),
    instance(12, false, false, 11, "com.nimbits.server.orm.CategoryEntity");
    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>(EntityType.values().length);

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final boolean uniqueNameFlag;
    private final String className;
    private final boolean isTreeGridItem;
    private final int order;
    private EntityType(int code, boolean uniqueNameFlag, boolean isTreeGridItem, int order, String className) {
        this.code = code;
        this.uniqueNameFlag = uniqueNameFlag;
        this.className = className;
        this.isTreeGridItem = isTreeGridItem;
        this.order = order;
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

    public boolean isTreeGridItem() {
        return isTreeGridItem;
    }

    public Integer getOrder() {
        return order;
    }
}
