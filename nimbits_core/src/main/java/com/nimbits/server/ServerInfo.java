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

package com.nimbits.server;


import javax.servlet.ServletRequest;
import java.util.zip.Deflater;


public class ServerInfo {
    private static final String TEST_URL = "http://localhost:8081";

    private ServerInfo() {
    }


    public static String getFullServerURL(final ServletRequest req) {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
     try {
         return req == null ? getUrl() : req.getScheme() + "://" + req.getServerName() + ':' + req.getServerPort();

     }
     catch (NullPointerException ex) {
        return TEST_URL;
     }


    }
    private  static String getUrl() {

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
