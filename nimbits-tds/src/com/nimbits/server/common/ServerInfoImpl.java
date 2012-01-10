package com.nimbits.server.common;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/17/11
 * Time: 9:35 AM
 */
public class ServerInfoImpl {

    public static String getFullServerURL(final HttpServletRequest req) {

        return req == null ? null : req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
    }

}
