package com.nimbits.cloudplatform.server.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/10/11
 * Time: 5:35 PM
 */
public class NimbitsExclusionStrategy implements ExclusionStrategy {
    private final Class<?> typeToSkip;

    NimbitsExclusionStrategy(Class<?> typeToSkip) {
        this.typeToSkip = typeToSkip;
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return (clazz == typeToSkip);
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(DoNotSerializePolicy.class) != null;
    }
}
