package com.nimbits.io.http;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.server.apikey.ApiKey;
import com.nimbits.client.model.server.apikey.ApiKeyFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class HttpClientDirect {



    public static String doGet(String email, String base, String path, List<BasicNameValuePair> parameters) throws IOException {

        DefaultHttpClient httpClient;


        parameters.add((new BasicNameValuePair(Parameters.email.getText(), email)));
        String u = base + "/service/v2/" + path + "?";
        for (BasicNameValuePair p : parameters) {
            u += p.getName() + "=" + p.getValue() + "&";

        }
        u = u.substring(0, u.length()-1);

        HttpGet http = new HttpGet(u);

        httpClient = getInstance(ApiKeyFactory.createApiKey(ServerSetting.apiKey.getDefaultValue()));

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

        HttpPost httppost = new HttpPost(base + "/service/v2/" + path);
        parameters.add((new BasicNameValuePair(Parameters.email.getText(), email)));
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parameters.size());
        for (BasicNameValuePair value : parameters) {
            nameValuePairs.add(value);
        }


        httpClient =  getInstance(ApiKeyFactory.createApiKey(ServerSetting.apiKey.getDefaultValue()));

        httppost.setHeader(ServerSetting.apiKey.getName(), ServerSetting.apiKey.getDefaultValue());

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


        HttpResponse response = httpClient.execute(httppost);

        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        return streamToString(inputStream);


    }

    public static DefaultHttpClient getInstance(final ApiKey apiKey) {
        DefaultHttpClient httpClient;
        HttpParams headerParams = new BasicHttpParams();
        headerParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        headerParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        if (! apiKey.isEmpty()) {
            headerParams.setParameter(ServerSetting.apiKey.getName(), apiKey);
        }
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(headerParams, timeoutConnection);

        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(headerParams, timeoutSocket);

        httpClient = new DefaultHttpClient(headerParams);



        return httpClient;
    }

}
