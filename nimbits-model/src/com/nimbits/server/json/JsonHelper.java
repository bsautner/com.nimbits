package com.nimbits.server.json;

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
            return true;
        } catch (JsonParseException ex) {
            return false;
        }
    }


}
