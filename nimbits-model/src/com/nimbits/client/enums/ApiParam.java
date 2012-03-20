package com.nimbits.client.enums;

import com.nimbits.client.model.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 1:23 PM
 */
public enum ApiParam {
    record(Const.ACTION_RECORD),
    point(Const.Params.PARAM_POINT),
    value(Const.PARAM_VALUE),
    json(Const.Params.PARAM_JSON),
    name(Const.Params.PARAM_NAME),
    note(Const.Params.PARAM_NOTE),
    lat(Const.Params.PARAM_LAT),
    lng(Const.Params.PARAM_LNG),
    timestamp(Const.Params.PARAM_TIMESTAMP),
    data(Const.PARAM_DATA),
    uuid(Const.PARAM_UUID),
    format(Const.Params.PARAM_FORMAT),
    points(Const.Params.PARAM_POINTS),
    count(Const.Params.PARAM_COUNT),
    autoscale(Const.Params.PARAM_AUTO_SCALE), category(Const.Params.PARAM_CATEGORY);

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
