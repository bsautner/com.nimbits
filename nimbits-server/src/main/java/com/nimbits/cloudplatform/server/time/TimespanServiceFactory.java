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

package com.nimbits.cloudplatform.server.time;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/6/11
 * Time: 12:55 PM
 */
 @Deprecated
public class TimespanServiceFactory {
    private static TimespanService instance;


    protected TimespanServiceFactory() {
        // Exists only to defeat instantiation.
    }

    public static TimespanService getInstance() {
        if (instance == null) {
            instance = new TimespanService();
        }
        return instance;


    }
}
