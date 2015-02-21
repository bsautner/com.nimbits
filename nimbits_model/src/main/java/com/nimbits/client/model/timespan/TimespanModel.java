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
 * Created by bsautner
 * User: benjamin
 * Date: 4/25/11
 * Time: 7:03 PM
 * <p/>
 */
public class TimespanModel implements Timespan {

    private long start;
    private long end;
    private boolean endRequiresOffset;
    private boolean startRequiresOffset;

    public TimespanModel(Date start, Date end, boolean startRequiresOffset, boolean endRequiresOffset) {
        this.start = start.getTime();
        this.end = end.getTime();
        this.startRequiresOffset = startRequiresOffset;
        this.endRequiresOffset = endRequiresOffset;
    }

    public TimespanModel() {
    }

    @Override
    public Date getStart() {
        return new Date(start);
    }

    @Override
    public Date getEnd() {
        return new Date(end);
    }

    @Override
    public boolean isEndRequiresOffset() {
        return endRequiresOffset;
    }

    @Override
    public boolean isStartRequiresOffset() {
        return startRequiresOffset;
    }


}
