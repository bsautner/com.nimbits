package com.nimbits.server.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nimbits.client.model.Const;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 11/17/11
 * Time: 2:55 PM
 */
public class DateSerializer implements JsonSerializer<Date> {

    private static SimpleDateFormat format = new SimpleDateFormat(Const.GSON_DATE_FORMAT);


    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(format.format(src));
    }
}