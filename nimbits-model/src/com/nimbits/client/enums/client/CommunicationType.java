package com.nimbits.client.enums.client;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/29/12
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public enum CommunicationType {

   devRequest(1);


    private static final Map<Integer, CommunicationType> lookup = new HashMap<Integer, CommunicationType>(1);

    static {
        for (CommunicationType s : EnumSet.allOf(CommunicationType.class))
            lookup.put(s.code, s);
    }

    private final int code;

    public static CommunicationType get(int code) {
        return lookup.get(code);
    }
    private CommunicationType(int aCode) {
        this.code = aCode;

    }

    public int getCode() {
        return code;
    }


}
