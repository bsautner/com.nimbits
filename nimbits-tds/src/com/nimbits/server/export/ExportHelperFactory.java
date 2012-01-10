package com.nimbits.server.export;

public class ExportHelperFactory {

    public static ExportHelper getInstance() {
        return new ExportHelperImpl();
    }


}
