package com.nimbits.cloudplatform.http;

import com.google.gson.Gson;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Benjamin Sautner
 * Date: 1/18/13
 * Time: 7:01 AM
 */
@SuppressWarnings({"unchecked", "unused"})
public class HttpHelper {

    private volatile static Map<String, List> listMap;
    private volatile static Map<String, Long> expireMap;
    private static int MAX_LIST = 1000;
    private static Date lastFlushCheck;
    static {
        lastFlushCheck = new Date();
        listMap = new HashMap<String, List>();
        expireMap = new HashMap<String, Long>();

    }

    public static void init(Cookie authCookie, Gson aGson) {

        HttpTransaction.init(authCookie, aGson);
    }

    public static void flush() {
        listMap.clear();
        expireMap.clear();
    }

    private static String buildCode(final UrlContainer postUrl,
                                    final List<BasicNameValuePair> parameters
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append(postUrl.getUrl().hashCode());
        for (BasicNameValuePair pair : parameters) {
            sb.append(pair.getName().hashCode()).append(pair.getValue().hashCode());

        }
        return  sb.toString();

    }


    public static <T, K> List<T> doGet(final Class<K> clz,
                                       final UrlContainer postUrl,
                                       final List<BasicNameValuePair> parameters,
                                       final Type type,
                                       final boolean expectList
    ) {


        return doGet(clz, postUrl, parameters, type, false, false, expectList, new Date());


    }
    public static <T, K> List<T> doGet(final Class<K> clz,
                                       final UrlContainer postUrl,
                                       final List<BasicNameValuePair> parameters,
                                       final Type type,
                                       final boolean doCache,
                                       final boolean doDisk,
                                       final boolean expectList,
                                       final Date expires
    ) {
        List<T> result;
        String code = buildCode(postUrl, parameters);
        if (expireMap.containsKey(code) && expireMap.get(code) < expires.getTime()) {
            expireMap.remove(code);
            listMap.remove(code);
            result =  doHttpGet(clz, postUrl, parameters, type, expectList, code);
        }
        else if (listMap.containsKey(code)) {

            result = (List<T>) listMap.get(code);
        }
        else {
            result = doHttpGet(clz, postUrl, parameters, type, expectList, code);
        }

        return result;


    }

    private static <T, K> List<T> doHttpGet(Class<K> clz, UrlContainer postUrl, List<BasicNameValuePair> parameters, Type type, boolean expectList, String code) {
        List<T> response = HttpTransaction.doGet(clz, postUrl, parameters, type, expectList);
        listMap.put(code, response);
        return response;
    }

    public static <T, K>  List<T> doPost(final Class<K> clz,
                                         final UrlContainer postUrl,
                                         final List<BasicNameValuePair> parameters,
                                         final Type type,
                                         final FlushType flushType,
                                         final boolean expectList) {
        switch (flushType) {


            case none:
                break;
            case complete:
                listMap.clear();
        }

        return HttpTransaction.doPost(clz, postUrl, parameters, type, expectList);
    }


    public static List<Cookie> getAuthCookie(final UrlContainer gaeAppLoginUrl,
                                             final String authToken,
                                             final String baseUrl) {
        return HttpTransaction.getAuthCookie(gaeAppLoginUrl, authToken, baseUrl);

    }

}
