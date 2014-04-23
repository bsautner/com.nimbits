package com.nimbits.http;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.SettingType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpClientHelper {



    public static String doGet(String email, String base, String path, List<BasicNameValuePair> parameters) throws IOException {

        DefaultHttpClient httpClient;


        parameters.add((new BasicNameValuePair(Parameters.email.getText(), email)));
        String u = base + path + "?";
        for (BasicNameValuePair p : parameters) {
            u += p.getName() + "=" + p.getValue() + "&";

        }
        u = u.substring(0, u.length()-1);

        HttpGet http = new HttpGet(u);

        httpClient = HttpClientFactory.getInstance(SettingType.apiKey.getDefaultValue());

        HttpResponse response = httpClient.execute(http);

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
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parameters.size());
        for (BasicNameValuePair value : parameters) {
            nameValuePairs.add(value);
        }


        httpClient = HttpClientFactory.getInstance(SettingType.apiKey.getDefaultValue());
//        if (userApiKey) {
        httppost.setHeader(SettingType.apiKey.getName(), SettingType.apiKey.getDefaultValue());
//        }
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


        HttpResponse response = httpClient.execute(httppost);

        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        return streamToString(inputStream);


    }
}
