/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.system;


import javax.servlet.ServletRequest;
import java.util.zip.Deflater;


public class ServerInfo {


    private static final String TEST_URL = "http://localhost:8080";


    public String getFullServerURL(final ServletRequest req) {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        try {
            return req == null ? getUrl() : req.getScheme() + "://" + req.getServerName() + ':' + req.getServerPort();

        } catch (NullPointerException ex) {
            return TEST_URL;
        }


    }

    private String getUrl() {

        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if (environment != null && environment.equals("Production")) {
            String applicationId = System.getProperty("com.google.appengine.application.id");
            //String version = System.getProperty("com.google.appengine.application.version");
            return "http://" + applicationId + ".appspot.com/";
        } else {
            return TEST_URL;
        }

    }
}
