package com.nimbits.cloudplatform.server.gson;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/10/11
 * Time: 5:34 PM
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DoNotSerializePolicy {
    // Field tag only annotation
}

