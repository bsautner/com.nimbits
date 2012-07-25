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

package com.nimbits.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.exceptions.GoogleAuthenticationException;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.user.GoogleAuthentication;
import com.nimbits.user.GoogleUser;
import com.nimbits.user.NimbitsUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NimbitsClientImpl implements NimbitsClient {



    final private static Gson gson = GsonFactory.getInstance();
    private static final String PATH_CHART_API = "/service/chartapi";
    private final GoogleAuthentication G;
    private final String host;
    private Cookie authCookie;
    private final NimbitsUser nimbitsUser;
    private final GoogleUser googleUser;
//    private NimbitsClientImpl() {
//    }

    public NimbitsClientImpl(final NimbitsUser n, final String hostUrl) {
        this.host = hostUrl;
        nimbitsUser = n;
        googleUser = null;
        G = GoogleAuthentication.getNewGoogleAuthentication();
        G.setEmail(n.getEmailAddress());
        G.setSecret(n.getNimbitsSecretKey());
    }

    public NimbitsClientImpl(final GoogleUser g, final String hostUrl) throws NimbitsException {
        this.host = hostUrl;
        nimbitsUser = null;
        googleUser = g;
        G = GoogleAuthentication.getNewGoogleAuthentication();
        G.setEmail(g.getGoogleEmailAddress());
        G.Connect(hostUrl, g.getGoogleEmailAddress(), g.getGooglePassword());

    }

    public String getHost() {
        return host;
    }

    public NimbitsClientImpl(final String token, final EmailAddress email, final String hostUrl) throws NimbitsException, GoogleAuthenticationException {
        this.host = hostUrl;
        G = GoogleAuthentication.getNewGoogleAuthentication();
        G.setEmail(email);
        if (!email.getValue().equals(Const.TEST_ACCOUNT)) {

            authCookie = G.ConnectAuth(token, hostUrl);

        }
        nimbitsUser = null;
        googleUser = null;


    }

    public Cookie getAuthCookie() {
        return authCookie;
    }


    public boolean isLoggedIn() throws NimbitsException {

        return Boolean.parseBoolean(doGGet(host + Path.PATH_AUTHTEST_SERVICE, ""));

    }


    public List<User> getUsers() throws NimbitsException {
        String u = host + Path.PATH_USER_SERVICE;
        String params = "action=download";
        String result = doGGet(u, params);

        return gson.fromJson(result, GsonFactory.userListType);


    }

    public String getChart(final String points, final int count) throws NimbitsException {
        final String u = host + PATH_CHART_API;
        String params = null;
        final String result;
        try {
            params = "count=10&points=" + URLEncoder.encode(points, Const.CONST_ENCODING) + "&chxt=y&chxp=1,75,100&cht=lc&chco=76A4FB&chls=2.0&chs=300x200";
        } catch (UnsupportedEncodingException e1) {

            e1.printStackTrace();
        }


        result = doGGet(u, params);


        return result;
    }

    public String getChartURL(final String points, final int count, final String additionalParams) {
        final String u = host + PATH_CHART_API;
        String params = null;

        try {
            params = "count=10&points=" + URLEncoder.encode(points, Const.CONST_ENCODING) + "&" + additionalParams;

        } catch (UnsupportedEncodingException e1) {

            e1.printStackTrace();
        }


        return u + "?" + params;
    }

    public void deletePoint(final EntityName name) {
        final String u = host + Path.PATH_POINT_SERVICE;
        try {
            String params = "name=" + URLEncoder.encode(name.getValue(), Const.CONST_ENCODING) + "&action=delete";
            doGPost(u, params);
        } catch (UnsupportedEncodingException ignored) {

        }

    }

    public void deletePoint(final String name) {

        final String u = host + Path.PATH_POINT_SERVICE;
        try {
            String params = "name=" + URLEncoder.encode(name, Const.CONST_ENCODING) + "&action=delete";
            doGPost(u, params);
        } catch (UnsupportedEncodingException ignored) {

        }

    }

    public Value recordValue(final EntityName name,
                             final double value,
                             final Date timestamp) {
        final String u = host + Path.PATH_CURRENT_VALUE;
        String params;
        try {
            params = new StringBuilder().append(Parameters.point.getText()).append("=").append(URLEncoder.encode(name.getValue(), Const.CONST_ENCODING)).append("&").append(Parameters.timestamp.getText()).append("=").append(timestamp.getTime()).append("&").append(Parameters.value.getText()).append("=").append(value).toString();
        } catch (UnsupportedEncodingException ignored) {
            params = null;
        }

        String json = doGPost(u, params);
        if (StringUtils.isEmpty(json)) {
            json = doGPost(u, params); //retry
        }
        return gson.fromJson(json, ValueModel.class);
    }

    public Value recordValue(final EntityName name,
                             final double value) {
        return recordValue(name, value, new Date());
    }

    public Value recordValue(final String name,
                             final double value) throws NimbitsException {

        final EntityName name1 = CommonFactoryLocator.getInstance().createName(name, EntityType.point);


        return recordValue(name1, value, new Date());
    }


    public Value recordValueWithGet(final EntityName name, final double value, final Date timestamp) throws IOException, NimbitsException {
        final String u = host + Path.PATH_CURRENT_VALUE;
        final String params = "point=" + URLEncoder.encode(name.getValue(), Const.CONST_ENCODING) +
                "&timestamp=" + timestamp.getTime() +
                "&value=" + value;

        final String json = doGGet(u, params);
        System.out.println(json);
        final double d = Double.valueOf(json);
        return ValueFactory.createValueModel(d);

        //return gson.fromJson(json, ValueModel.class);

    }

    @Override
    public Value recordValue(final String name, final double value, final Date timestamp) throws NimbitsException {

        return recordValue(CommonFactoryLocator.getInstance().createName(name, EntityType.point), value, timestamp);
    }

    public String recordBatch(String params) {
        String u = host + Path.PATH_BATCH_SERVICE;

        return doGPost(u, params);
    }


    public Value recordValue(EntityName name, Value v) throws IOException {
        String u = host + Path.PATH_CURRENT_VALUE;
        String json = gson.toJson(v, ValueModel.class);
        String params = Parameters.timestamp.getText() +
                "=" + v.getTimestamp().getTime() +
                "&" + Parameters.point.getText() + "=" +
                URLEncoder.encode(name.getValue(), Const.CONST_ENCODING) +
                "&" + Parameters.json.getText() + "=" + URLEncoder.encode(json, Const.CONST_ENCODING);
        String result = doGPost(u, params);
        return gson.fromJson(result, ValueModel.class);

    }


//    public Entity addCategory(final EntityName EntityName) throws UnsupportedEncodingException {
//
//        final String u = host + Path.PATH_CATEGORY_SERVICE;
//        final String params = "name=" + URLEncoder.encode(EntityName.getValue(), Const.CONST_ENCODING);
//        final String result = doGPost(u, params);
//        return gson.fromJson(result, EntityModel.class);
//
//
//    }

//    public String deleteCategory(final EntityName EntityName) {
//        String retVal = "";
//        try {
//            String u = host + Path.PATH_CATEGORY_SERVICE;
//            String params;
//            params = Parameters.action.getText()
//                    + "=" + Action.delete.name()
//                    + "&" + Parameters.name.getText()
//                    + "=" + URLEncoder.encode(EntityName.getValue(), Const.CONST_ENCODING);
//            retVal = doGPost(u, params);
//        } catch (UnsupportedEncodingException ignored) {
//
//        }
//        return retVal;
//
//
//    }

    public Point addPoint(final EntityName pointName) {
        Point point = null;

        try {
            final String u = host + Path.PATH_POINT_SERVICE;
            final String params = Parameters.name.getText() + "=" +
                    URLEncoder.encode(pointName.getValue(), Const.CONST_ENCODING);
            String json = doGPost(u, params);
            point = gson.fromJson(json, PointModel.class);
        } catch (JsonSyntaxException ignored) {

        } catch (UnsupportedEncodingException ignored) {

        }
        return point;


    }
    @Override
    public void addCalculation(Calculation calculation, EntityName name) {
        final String u = host + Path.PATH_CALC_SERVICE;

        final String params;
        final String json = GsonFactory.getInstance().toJson(calculation);
        try {
            params = Parameters.name.getText() + "=" +
                    URLEncoder.encode(name.getValue(), Const.CONST_ENCODING)
            + "&" + Parameters.json.getText() + "=" + URLEncoder.encode(json, Const.CONST_ENCODING);

            doGPost(u, params);


        } catch (UnsupportedEncodingException e) {


        }

    }
    @Override
    public Point addPoint(String pointName) throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(pointName, EntityType.point);
        return addPoint(name);
    }

    @Override
    public List<Entity> getCategories(boolean includePoints, boolean includeDiagrams) throws NimbitsException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Point getPoint(final EntityName name) throws NimbitsException {
        Point retObj = null;

        try {
            final String u = host + Path.PATH_POINT_SERVICE;
            final String params = Parameters.name.getText() + "=" + URLEncoder.encode(name.getValue(), Const.CONST_ENCODING);
            String json = doGGet(u, params);
            retObj = gson.fromJson(json, PointModel.class);

        } catch (UnsupportedEncodingException e) {
            return null;

        } catch (IOException e) {
            return null;
        } catch (JsonSyntaxException e) {
            return null;
        }

        return retObj;


    }

    @Override
    public Point getPoint(String pointName) throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(pointName, EntityType.point);
        return getPoint(name);
    }

    @Override
    public Point updatePoint(final Point p) {
        Point ret = null;

        try {
            String u = host + Path.PATH_POINT_SERVICE;
            String params;
            String json = gson.toJson(p);
            params = Parameters.json.getText() + "=" + URLEncoder.encode(json, Const.CONST_ENCODING) +
                    "&" + Parameters.action.getText() + "=" + Action.update.getCode();
            String response = doGPost(u, params);
            if (response != null) {
                ret = gson.fromJson(response, PointModel.class);
            }
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        return ret;

    }


    public Point addPoint(final EntityName pointName, final Point p) {
        Point retObj = null;
        final String newPointJson = gson.toJson(p);
        try {
            final String u = host + Path.PATH_POINT_SERVICE;
            final String params = Parameters.json.getText() + '=' + URLEncoder.encode(newPointJson, Const.CONST_ENCODING) +
                    '&' + Parameters.name.getText() + '=' + pointName.getValue();
            final String result = doGPost(u, params);
            retObj = gson.fromJson(result, PointModel.class);
        } catch (UnsupportedEncodingException ignored) {


        }
        return retObj;

    }


//    public Entity getCategory(final EntityName EntityName, final boolean includePoints, final boolean includeDiagrams) throws NimbitsException {
//        Entity c;
//        final String u = host + Path.PATH_CATEGORY_SERVICE;
//        String params = Parameters.name.getText() + "=" + EntityName.getValue();
//
//        if (includePoints) {
//            params += "&" + Parameters.PARAM_INCLUDE_POINTS + "=" + Words.WORD_TRUE;
//        }
//        if (includeDiagrams) {
//            params += "&" + Parameters.PARAM_INCLUDE_DIAGRAMS + "=" + Words.WORD_TRUE;
//        }
//
//        final String json = doGGet(u, params);
//
//        c = gson.fromJson(json, EntityModel.class);
//
//
//        return c;
//
//
//    }

    public String currentValue(final EntityName name) throws IOException, NimbitsException {
        String u = host + Path.PATH_CURRENT_VALUE;
        String params = Parameters.point.getText() + "=" + URLEncoder.encode(name.getValue(), Const.CONST_ENCODING);
        return doGGet(u, params);

    }

    public Object getCurrentDataObject(final EntityName name, Class<?> cls) {
        Value value = getCurrentRecordedValue(name);
        if (value.getData() != null) {
            return gson.fromJson(value.getData().getContent(), cls);
        } else {
            return null;
        }
    }

    @Override
    public Value recordDataObject(EntityName name, Object object, Class<?> cls) throws NimbitsException {
        Value value = ValueFactory.createValueModel(0.0, 0.0, 0.0, new Date(), "", ValueFactory.createValueData(gson.toJson(object)), AlertType.OK);
        try {
            return recordValue(name, value);
        } catch (IOException e) {
            throw new NimbitsException(e.getMessage());
        }


    }

    @Override
    public Value recordDataObject(EntityName name, Object object, Class<?> cls, double latitude, double longitude, double value) throws NimbitsException {
        Value vx = ValueFactory.createValueModel(latitude, longitude, value, new Date(),  "", ValueFactory.createValueData(gson.toJson(object)), AlertType.OK);
        try {
            return recordValue(name, vx);
        } catch (IOException e) {
            throw new NimbitsException(e.getMessage());
        }


    }

    public Value getCurrentRecordedValue(final EntityName name) {
        Value retObj = null;
        String u = host + Path.PATH_CURRENT_VALUE;
        String params;
        String json;

        try {
            params = Parameters.point.getText() + "=" + URLEncoder.encode(name.getValue(), Const.CONST_ENCODING) + "&format=json";
            json = doGGet(u, params);

            if (json != null) {
                retObj = gson.fromJson(json, ValueModel.class);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        return retObj;


    }

    public List<Value> getSeries(final String name, final int count) throws NimbitsException {
        EntityName name1 = CommonFactoryLocator.getInstance().createName(name, EntityType.point);
        return getSeries(name1, count);

    }

    public List<Value> getSeries(final EntityName name, final int count) throws NimbitsException {
        List<Value> retObj = null;

        String result;

        final String destUrl = host + Path.PATH_SERIES_SERVICE;
        String params;
        try {
            params = Parameters.count.getText() + "=" + count + "&" + Parameters.point.getText() + "=" + URLEncoder.encode(name.getValue(), Const.CONST_ENCODING);
            result = doGGet(destUrl, params);

            retObj = gson.fromJson(result, GsonFactory.valueListType);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return retObj;

    }

    public List<Value> getSeries(final EntityName name, final Date startDate, final Date endDate) throws NimbitsException {
        final List<Value> retObj = new ArrayList<Value>();


        String result;

        String destUrl = host + Path.PATH_SERIES_SERVICE;
        String params;
        int seg = 0;

        try {
            while (true) {
                params = "seg=" + seg + "&sd=" + startDate.getTime() + "&ed=" + endDate.getTime() + "&" + Parameters.point.getText() + "=" + URLEncoder.encode(name.getValue(), Const.CONST_ENCODING);
                result = doGGet(destUrl, params);
                List<Value> r = gson.fromJson(result, GsonFactory.valueListType);

                if (r == null || r.size() == 0) {
                    break;
                } else {
                    retObj.addAll(r);
                }
                seg += 1000;
            }

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return retObj;

    }

    @Override
    public void downloadSeries(final EntityName name, final Date startDate, final Date endDate, final String filename) throws IOException, NimbitsException {
        final List<Value> r = getSeries(name, startDate, endDate);

        String json = gson.toJson(r, GsonFactory.valueListType);

        Writer out;

        out = new OutputStreamWriter(new FileOutputStream(filename));
        out.write(json);
        out.close();


    }

    @Override
    public List<Value> loadSeriesFile(final String fileName) throws IOException {

        final StringBuilder sb = new StringBuilder();
        final BufferedReader in = new BufferedReader(new FileReader(fileName));
        String str;

        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        return gson.fromJson(sb.toString(), GsonFactory.valueListType);

    }

    @Override
    public byte[] getChartImage(final String baseURL, final String params) {
        final String url = baseURL + Path.PATH_CHART_SERVICE;
        return doGGetBinary(url, params);

    }


//    public byte[] getBinaryFile(String postUrl, String params) throws Exception {
//        byte[] retObj;
//        int c;
//
//        URL url = new URL(postUrl + "?" + params);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setDoOutput(true);
//        connection.setRequestMethod("GET");
//
//        if (G != null) {
//
//            try {
//                if (G.getAuthCookie() != null) {
//                    connection.addRequestProperty("Cookie", G.getAuthCookie().getValue() + "=" + G.getAuthCookie().getValue());
//                }
//
//            } catch (Exception e) {
//
//            }
//            params += getAuthParams();
//
//        }
//
//        DataInputStream in = new DataInputStream(connection.getInputStream());
//        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
//
//
//        while ((c = in.read()) != -1) {
//            byteArrayOut.write(c);
//        }
//        retObj = byteArrayOut.toByteArray();
//
//
//        return retObj;
//
//    }

    private String getAuthParams() {

        final StringBuilder b = new StringBuilder();
        b.append("&" + Parameters.email.getText() + "=").append(G.getEmail().getValue());
        if (G.getSecret() != null) {
            b.append("&" + Parameters.secret.getText() + "=").append(G.getSecret());
        }

        return b.toString();


    }

    private String doGGet(final String url, final String params) throws NimbitsException {
        String cookie = null;
        String postParams = params;
        if (G != null) {
            if (G.getAuthCookie() != null) {
                cookie = G.getAuthCookie().getName() + "=" + G.getAuthCookie().getValue();
            }
            postParams += getAuthParams();

        }

        return HttpCommonFactory.getInstance().doGet(url, postParams, cookie);


    }

    private byte[] doGGetBinary(final String url, final String params) {
        String cookie = null;
        String postParams = params;
        if (G != null) {
            if (G.getAuthCookie() != null) {
                cookie = G.getAuthCookie().getName() + "=" + G.getAuthCookie().getValue();
            }
            postParams += getAuthParams();

        }
        try {
            return HttpCommonFactory.getInstance().doGetBytes(url, postParams, cookie);
        } catch (NimbitsException e) {
            return null;
        } catch (Exception e) {
            return null;
        }

        //doPost(u, params, cookie);

    }

    private String doGPost(final String url, final String params) {
        String cookie = null;
        String postParams = params;
        if (G != null) {

            if (G.getAuthCookie() != null) {
                cookie = G.getAuthCookie().getName() + "=" + G.getAuthCookie().getValue();
                //  connection.addRequestProperty(Const.WORD_COOKIE, );
            }

            postParams += getAuthParams();

        }
        try {
            return HttpCommonFactory.getInstance().doPost(url, postParams, cookie);
        } catch (NimbitsException e) {
            e.printStackTrace();
            return null;
        }

        //doPost(u, params, cookie);

    }

    public NimbitsUser getNimbitsUser() {
        return nimbitsUser;
    }

    public GoogleUser getGoogleUser() {
        return googleUser;
    }

}
