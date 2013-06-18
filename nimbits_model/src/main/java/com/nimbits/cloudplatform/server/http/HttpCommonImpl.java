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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.http;


import com.nimbits.cloudplatform.client.common.*;
import com.nimbits.cloudplatform.client.constants.*;
import com.nimbits.cloudplatform.client.enums.*;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 3/28/11
 * Time: 2:21 PM
 */
public class HttpCommonImpl implements HttpCommon {


    private static final int DEFAULT_BUILDER_SIZE = 1024;

    @Override
    public String doPost(final String postUrl, final String params, final String authCookie) throws IOException {
       // String retVal = "";
        // String postParams = params;

            final URL url = new URL(postUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(Const.METHOD_POST);
            connection.setReadTimeout(Const.DEFAULT_HTTP_TIMEOUT);

            if (Utils.isNotEmpty(authCookie)) {
                connection.addRequestProperty(Words.WORD_COOKIE, authCookie);
            }


            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(params);
            writer.close();
            StringBuilder sv = new StringBuilder(DEFAULT_BUILDER_SIZE);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sv.append(line);
                }
                reader.close();
            }
            return sv.toString();



    }

    @Override
    public String doGet(final String postUrl, final String params) {

            return doGet(postUrl, params, null);

    }

    @Override
    public String doPost(final String postUrl, final String params) {
        try {
            return doPost(postUrl, params, null);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public byte[] doGetBytes(final String postUrl, final String params, final String authCookie) throws IOException {

        int c;
        final URL url = new URL(postUrl + "?" + params);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(Const.METHOD_GET);
        //  connection.setReadTimeout(Const.DEFAULT_HTTP_TIMEOUT);
        if (Utils.isNotEmpty(authCookie)) {
            connection.addRequestProperty(Words.WORD_COOKIE, authCookie);
        }

        final DataInputStream in = new DataInputStream(connection.getInputStream());
        final ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        while ((c = in.read()) != -1) {
            byteArrayOut.write(c);
        }
        byteArrayOut.flush();

        in.close();
        return byteArrayOut.toByteArray();

    }

    @Override
    public String doJsonPost(String postUrl, String params, String json) {
        final URL url;
        try {
            url = new URL(postUrl + "?" + params);
            StringBuilder sb = new StringBuilder(DEFAULT_BUILDER_SIZE);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(Const.METHOD_POST);
            connection.setReadTimeout(Const.DEFAULT_HTTP_TIMEOUT);
            connection.setRequestProperty(Parameters.contentType.getText(), Const.CONTENT_TYPE_JSON);


            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);

            writer.close();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
            }
            return sb.toString();

        } catch (MalformedURLException e) {
            return e.getMessage();
        } catch (ProtocolException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    public String doGet(final String postUrl, final String params, final String authCookie)  {
        final StringBuilder sb = new StringBuilder(DEFAULT_BUILDER_SIZE);
        HttpURLConnection connection;

        StringBuilder paramBuilder = new StringBuilder();

        try {

        final URL url = new URL((postUrl.toString()  + "?" + params));
        connection = (HttpURLConnection) url.openConnection();
        // connection.setDoOutput(true);
        //connection.setReadTimeout(15000);

            connection.setRequestMethod(Const.METHOD_GET);

        if (Utils.isNotEmpty(authCookie)) {
            connection.addRequestProperty(Words.WORD_COOKIE, authCookie);
        }
      //  connection.addRequestProperty(ACCEPT, APPLICATION_JSON);
       // connection.addRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
       // connection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
        // connection.addRequestProperty("user-agent", "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

        String line;

        while ((line = reader.readLine()) != null) {

            sb.append(line);
        }
        reader.close();
        connection.disconnect();
        } catch (ProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return sb.toString();






    }


}
