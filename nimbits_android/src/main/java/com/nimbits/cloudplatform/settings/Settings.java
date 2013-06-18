package com.nimbits.cloudplatform.settings;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Benjamin Sautner
 * Date: 12/29/12
 * Time: 3:49 PM
 */
public enum Settings {

    gps(Constants.GPS);

    private static final int CAPACITY = 10;
    private static final Map<String, Settings> lookup = new HashMap<String, Settings>(CAPACITY);

    static {
        for (Settings s : EnumSet.allOf(Settings.class))
            lookup.put(s.code, s);
    }

    private final String code;

    private Settings(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Settings get(String code) {
        return lookup.get(code);
    }

    private class Constants {
        protected final static String GPS = "USE_GPS";


    }
}
