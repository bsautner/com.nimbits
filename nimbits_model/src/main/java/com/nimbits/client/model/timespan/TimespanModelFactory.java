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

package com.nimbits.client.model.timespan;

import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/28/11
 * Time: 3:41 PM
 */
public class TimespanModelFactory {

    public static Timespan createTimespan(Date start, Date end) {
        return new TimespanModel(start, end, false, false);
    }

    public static Timespan createTimespan(Date start, Date end, boolean startRequiresOffset, boolean endRequiresOffset) {
        return new TimespanModel(start, end, startRequiresOffset, endRequiresOffset);
    }
}
