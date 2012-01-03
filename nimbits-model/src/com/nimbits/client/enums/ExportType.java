package com.nimbits.client.enums;


import com.nimbits.client.model.Const;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ExportType {

    text_csv("text/csv"),
    csvSeparateColumns("text/csv"),
    descriptiveStatistics(Const.CONTENT_TYPE_HTML),
    possibleContinuation(Const.CONTENT_TYPE_HTML),
    png("image/png"),
    table(Const.CONTENT_TYPE_PLAIN),
    plain("text/plain"),
    html(Const.CONTENT_TYPE_HTML),
    json("text/plain"),
    currentStatusReport(Const.CONTENT_TYPE_HTML);

    private static final Map<String, ExportType> lookup = new HashMap<String, ExportType>();

    static {
        for (ExportType s : EnumSet.allOf(ExportType.class))
            lookup.put(s.getCode(), s);
    }

    private final String mimeType;

    private ExportType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCode() {
        return mimeType;
    }

    public static ExportType get(String mimeType) {
        return lookup.get(mimeType);
    }


}
