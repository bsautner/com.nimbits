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

package com.nimbits.server.admin.logging;


import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/5/12
 * Time: 8:26 AM
 */
@Deprecated
public class LogHelper {

    private LogHelper() {
    }

    @Deprecated
    public static void logException(final Class<?> c, final Throwable ex) {
        final Logger log = Logger.getLogger(c.getName());
        log.severe(ex.getMessage());


    }

    @Deprecated
    public static void log(final Class<?> c, final String ex) {
        final Logger log = Logger.getLogger(c.getName());
        log.fine(ex);


    }
}
