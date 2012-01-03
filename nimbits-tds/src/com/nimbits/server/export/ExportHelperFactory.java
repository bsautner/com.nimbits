package com.nimbits.server.export;

public class ExportHelperFactory {

    private static ExportHelper instance;

    public static ExportHelper getInstance() {
        if (instance == null) {
            instance = new ExportHelperImpl();
        }
        return instance;
    }


}
