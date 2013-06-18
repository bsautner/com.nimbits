package com.nimbits.client.enums;


import com.nimbits.client.constants.Const;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ExportType {

    text_csv("text/csv", "csv"),
    csvSeparateColumns("text/csv", "csv"),
    descriptiveStatistics(Const.CONTENT_TYPE_HTML,"html"),
    possibleContinuation(Const.CONTENT_TYPE_HTML,"html"),
    png("image/png", "png"),
    table(Const.CONTENT_TYPE_PLAIN, "html"),
    plain("text/plain", "txt"),
    html(Const.CONTENT_TYPE_HTML, "html"),
    json("text/plain", "json"),
    unknown("", "dat"),
    currentStatusReport(Const.CONTENT_TYPE_HTML, "html");

    private static final Map<String, ExportType> lookup = new HashMap<String, ExportType>(11);

    static {
        for (ExportType s : EnumSet.allOf(ExportType.class))
            lookup.put(s.mimeType, s);
    }

    private final String mimeType;
    private final String fileExtension;

    private ExportType(String mimeType, String ext) {
        this.mimeType = mimeType;
        this.fileExtension = ext;
    }

    public String getCode() {
        return mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static ExportType get(String mimeType) {
        return lookup.get(mimeType);
    }


}
