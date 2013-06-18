package com.nimbits.cloudplatform.server.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nimbits.cloudplatform.client.constants.Const;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/17/11
 * Time: 10:10 AM
 */
public class DateDeserializer implements JsonDeserializer<Date> {
    private static SimpleDateFormat format = new SimpleDateFormat(Const.GSON_DATE_FORMAT);

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return format.parse(json.getAsJsonPrimitive().getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e.getMessage());
        }
    }
}
