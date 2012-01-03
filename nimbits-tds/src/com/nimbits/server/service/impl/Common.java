package com.nimbits.server.service.impl;

import com.nimbits.client.enums.ExportType;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/9/11
 * Time: 3:09 PM
 */
public class Common {

     public static void addResponseHeaders(HttpServletResponse resp, ExportType type) {
        resp.setContentType(type.getCode());
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }

}
