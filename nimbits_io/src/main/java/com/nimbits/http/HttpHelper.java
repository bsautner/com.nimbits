package com.nimbits.http;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.SettingType;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpHelper {



    public static String doGet(String email, String base, String path, List<BasicNameValuePair> parameters) throws IOException {

        DefaultHttpClient httpClient;


        parameters.add((new BasicNameValuePair(Parameters.email.getText(), email)));
        String u = base + path + "?";
        for (BasicNameValuePair p : parameters) {
            u += p.getName() + "=" + p.getValue() + "&";

        }
        u = u.substring(0, u.length()-1);
        HttpParams headerParams = new BasicHttpParams();
        headerParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        headerParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        HttpGet http = new HttpGet(u);

        httpClient = new DefaultHttpClient(headerParams);
        http.setHeader(SettingType.apiKey.getName(), SettingType.apiKey.getDefaultValue());


        HttpResponse response = httpClient.execute(http);
        if (response.getStatusLine().getStatusCode() != 200) {
            for (Header header : response.getAllHeaders()) {
                System.out.println(header.getName() + "=" + header.getValue());
            }
            throw new RuntimeException("server returned an error: " + response.getStatusLine().getReasonPhrase());

        }
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        return streamToString(inputStream);


    }

    public static String streamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static String doPost(String email, String base, String path, List<BasicNameValuePair> parameters) throws IOException {

        DefaultHttpClient httpClient;

        HttpPost httppost = new HttpPost(base + path);
        parameters.add((new BasicNameValuePair(Parameters.email.getText(), email)));
//        if (key != null) {
//            parameters.add((new BasicNameValuePair(Parameters.key.getText(), key.getCode())));
//        }
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parameters.size());
        for (BasicNameValuePair value : parameters) {
            nameValuePairs.add(value);
        }
        HttpParams headerParams = new BasicHttpParams();
        headerParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        headerParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        headerParams.setParameter(SettingType.apiKey.getName(), SettingType.apiKey.getDefaultValue());

        httpClient = new DefaultHttpClient(headerParams);
//        if (userApiKey) {
        httppost.setHeader(SettingType.apiKey.getName(), SettingType.apiKey.getDefaultValue());
//        }
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


        HttpResponse response = httpClient.execute(httppost);
        if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 204) {

        }
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        return streamToString(inputStream);


    }
}
