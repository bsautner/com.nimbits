package com.nimbits.cloudplatform.client.model.timespan;

import java.util.*;

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
