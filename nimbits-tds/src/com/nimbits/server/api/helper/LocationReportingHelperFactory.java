package com.nimbits.server.api.helper;


public class LocationReportingHelperFactory {

    private static LocationReportingHelper instance;

    public static LocationReportingHelper getInstance() {
        return new LocationReportingHelperImpl();
    }


}
