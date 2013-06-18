/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.admin.common;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletRequest;
import java.util.zip.Deflater;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/17/11
 * Time: 9:35 AM
 */
@Service("serverInfo")
@Transactional
public class ServerInfoImpl implements ServerInfo {
    private static final String TEST_URL = "http://localhost:8081";

    private ServerInfoImpl() {
    }

    @Override
    public String getFullServerURL(final ServletRequest req) {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
     try {
         return req == null ? getUrl() : req.getScheme() + "://" + req.getServerName() + ':' + req.getServerPort();

     }
     catch (NullPointerException ex) {
        return TEST_URL;
     }


    }
    private  String getUrl() {

        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if (environment.equals("Production")) {
            String applicationId = System.getProperty("com.google.appengine.application.id");
            //String version = System.getProperty("com.google.appengine.application.version");
           return  "http://" +applicationId+".appspot.com/";
        } else {
           return TEST_URL;
        }

    }
}
