package com.nimbits.cloudplatform.server.json;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/4/11
 * Time: 9:48 AM
 */
public class JsonHelper {

    private JsonHelper() {
    }

    public static boolean isJson(final String sample) {
        try {
            new JsonParser().parse(sample);
            return hasBrackets(sample);
        } catch (JsonParseException ex) {
            return false;
        }
    }

    private static boolean hasBrackets(String sample) {
        return !sample.isEmpty()
                && sample.charAt(0) == '{' && !sample.isEmpty()
                && sample.charAt(sample.length() - 1) == '}';
    }


}
