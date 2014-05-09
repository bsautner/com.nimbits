/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The type of socket to  open.
 * You can tell nimbits what kind of socket to open.
 * live will send all new values to the client
 * basic will only send new values with a subscription set to sockets.
 *
 */
public enum SocketType {
    live(ParamConstants.LIVE),
    basic(ParamConstants.BASIC),

    ;

    private static final Map<String, SocketType> lookup = new HashMap<String, SocketType>();

    static {
        for (SocketType s : EnumSet.allOf(SocketType.class))
            lookup.put(s.text, s);
    }

    private final String text;



    private SocketType(String text) {
        this.text = text;
    }

    public static SocketType get(String text) {
        SocketType type = text == null ? basic :  lookup.get(text);
        return type == null ? basic : type;

    }

    @Override
    public String toString() {
        return text;
    }

    private static class ParamConstants {
        private static final String LIVE = "live";
        private static final String BASIC = "basic";

        private ParamConstants() {
        }
    }


}
