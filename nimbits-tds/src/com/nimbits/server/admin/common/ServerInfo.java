package com.nimbits.server.admin.common;

import javax.servlet.ServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 10/9/12
 * Time: 5:07 PM
 */
public interface ServerInfo {
    String getFullServerURL(ServletRequest req);
}
