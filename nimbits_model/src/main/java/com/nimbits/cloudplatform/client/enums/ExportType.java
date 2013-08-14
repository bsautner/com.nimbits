/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.enums;


import com.nimbits.cloudplatform.client.constants.Const;

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
