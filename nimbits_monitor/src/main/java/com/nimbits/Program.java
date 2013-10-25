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

package com.nimbits;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 11:31 AM
 */
public class Program {


    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
        Random r = new Random();
        String urlParameters = "email=support@nimbits.com&key=mysecretkey&point=foo&value=" + r.nextDouble();
        String request = "http://localhost:8080/service/v2/value";
        URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
        connection.setUseCaches (false);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        connection.disconnect();
        Thread.sleep(1000);
         System.out.println("wrote test" + new Date().getTime());
        }
    }

    public static void testSummary() {
//        String p1 = "summary_test";
//        Value v = ValueHelper.recordValue(p1, 1.0);
//        System.out.println("Returned: " + v.getDoubleValue());

    }

}
