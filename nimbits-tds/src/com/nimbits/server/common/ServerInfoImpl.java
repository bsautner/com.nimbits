/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.common;

import org.apache.commons.lang3.*;

import javax.servlet.http.*;
import java.util.logging.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/17/11
 * Time: 9:35 AM
 */
public class ServerInfoImpl {
    private static final Logger log = Logger.getLogger(ServerInfoImpl.class.getName());

    public static String getFullServerURL(final HttpServletRequest req) {

        String retVal =  req == null ? getUrl() : req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
         log.severe(retVal);
        return retVal;
    }
    private  static String getUrl() {
        String hostUrl;
        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if (StringUtils.equals("Production", environment)) {
            String applicationId = System.getProperty("com.google.appengine.application.id");
            String version = System.getProperty("com.google.appengine.application.version");
            hostUrl = "http://"+version+"."+applicationId+".appspot.com/";
        } else {
            hostUrl = "http://localhost:8081";
        }
        return hostUrl;
    }
}
