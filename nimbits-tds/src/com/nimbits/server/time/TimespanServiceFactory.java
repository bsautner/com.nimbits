/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.time;

import com.nimbits.client.service.timespan.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/6/11
 * Time: 12:55 PM
 */
public class TimespanServiceFactory {
    private static TimespanService instance;


    protected TimespanServiceFactory() {
        // Exists only to defeat instantiation.
    }

    public static TimespanService getInstance() {
        if (instance == null) {
            instance = new TimespanServiceImpl();
        }
        return instance;


    }
}
