package com.nimbits.client.model.entity;

/**
 * User: benjamin
 * Date: 6/28/12
 * Time: 9:22 AM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
public class EntityHelper {


    public static String getSafeNamespaceKey(final String key) {

        final StringBuilder sb = new StringBuilder(key.length());
        for (char c : key.toCharArray()) {
            if (String.valueOf(c).matches("[0-9A-Za-z._-]{0,100}")) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
