package com.nimbits.client.enums;

import com.nimbits.client.constants.*;


import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 1:23 PM
 */
public enum ApiParam {
    record(Params.ACTION_RECORD),
    point(Params.PARAM_POINT),
    value(Params.PARAM_VALUE),
    json(Params.PARAM_JSON),
    name(Params.PARAM_NAME),
    note(Params.PARAM_NOTE),
    lat(Params.PARAM_LAT),
    lng(Params.PARAM_LNG),
    timestamp(Params.PARAM_TIMESTAMP),
    data(Params.PARAM_DATA),
    uuid(Params.PARAM_UUID),
    format(Params.PARAM_FORMAT),
    points(Params.PARAM_POINTS),
    count(Params.PARAM_COUNT),
    autoscale(Params.PARAM_AUTO_SCALE), category(Params.PARAM_CATEGORY);

    private static final Map<String, ApiParam> lookup = new HashMap<String, ApiParam>();

    static {
        for (ApiParam s : EnumSet.allOf(ApiParam.class))
            lookup.put(s.getCode(), s);
    }

    private final String code;

    private ApiParam(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ApiParam get(String code) {
        return lookup.get(code);
    }


}
