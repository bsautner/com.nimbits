/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.client.model.timespan;

import com.extjs.gxt.ui.client.util.*;
import com.google.gwt.i18n.client.*;
import com.nimbits.client.exception.*;

import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 5/10/11
 * Time: 8:19 PM
 */
public class TimespanServiceClientImpl {

    public static Timespan createTimespan(final String start, final String end) throws NimbitsException {

        return interpretTimespan(start, end);
    }

    /*
    Description
    allows for a flexible date and time combination, but requires a 12-hour clock (am/pm).
    Many versions of the am/pm are supported.
    Matches
    12/31/2002 | 12/31/2002 08:00 | 12/31/2002 08:00 AM
    Non-Matches
    12/31/02 | 12/31/2002 14:00
    */
    private static boolean isLongDateStr(final String s) {
        return s.matches("^(((((0[13578])|([13578])|(1[02]))[\\-\\/\\s]?((0[1-9])|([1-9])|([1-2][0-9])|(3[01])))|((([469])|(11))[\\-\\/\\s]?((0[1-9])|([1-9])|([1-2][0-9])|(30)))|((02|2)[\\-\\/\\s]?((0[1-9])|([1-9])|([1-2][0-9]))))[\\-\\/\\s]?\\d{4})(\\s(((0[1-9])|([1-9])|(1[0-2]))\\:([0-5][0-9])((\\s)|(\\:([0-5][0-9])\\s))([AM|PM|am|pm]{2,2})))?$");
    }

    /*
   Description
   this expression validates a date-time field in European d/m/y h:m:s format.
   Matches
   29/02/2004 20:15:27 | 29/2/04 8:9:5 | 31/3/2004 9:20:17
   Non-Matches
   29/02/2003 20:15:15 | 2/29/04 20:15:15 | 31/3/4 9:20:17
    */
//    private static boolean isLongEuropean(final String s) {
//        return s.matches("^((((31\\/(0?[13578]|1[02]))|((29|30)\\/(0?[1,3-9]|1[0-2])))\\/(1[6-9]|[2-9]\\d)?\\d{2})|(29\\/0?2\\/(((1[6-9]|[2-9]\\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))|(0?[1-9]|1\\d|2[0-8])\\/((0?[1-9])|(1[0-2]))\\/((1[6-9]|[2-9]\\d)?\\d{2})) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$");
//
//
//    }

    //TODO replace with regex
    private static boolean isSQL(final String s) {
        DateTimeFormat dtf = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss aaa");
        boolean retVal;

        try {
            dtf.parse(s);
            retVal = true;
        } catch (Exception e) {
            retVal = false;
        }
        return retVal;

    }

    private static Date parseLongDateStr(final String s) {
        final DateTimeFormat dtf = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss aaa");

        return dtf.parse(s);
    }

    private static Date parseLongEurpeanStr(final String s) {
        final DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss");
        return dtf.parse(s);
    }

    private static Date parseCommonStr(final String s) {
        final DateTimeFormat dtf = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss");
        return dtf.parse(s);
    }

    private static Timespan interpretTimespan(final String startSample, final String endSample) throws NimbitsException {

        Timespan retObj;

        Date start = null, end = null;

        if (endSample != null) {
            try {
                if (endSample.equals("now") || endSample.equals("*")) {
                    end = new Date();
                }
                // Absolute Date.
                else if (endSample.length() == 14) {

                    end = processRelativeDate(endSample);
                }
                //unix time in seconds
                else if (endSample.length() == 10) {
                    Long l = Long.parseLong(endSample);
                    end = new Date(l * 1000);
                }
                //unix time in ms
                else if (endSample.length() == 13) {
                    end = new Date(Long.parseLong(endSample));
                } else if (isLongDateStr(endSample)) {
                    end = parseLongDateStr(endSample);
                    //} //else if (isLongEuropean(endSample)) {
                    // end = parseLongEurpeanStr(endSample);
                } else if (isSQL(endSample)) {
                    end = parseLongDateStr(endSample);
                } else

                {
                    end = parseCommonStr(endSample);
                    //throw new InvalidTimespanException("Invalid End Date: " + endSample) ;
                }

            } catch (NumberFormatException e) {
                throw new NimbitsException("Invalid End Date");
            }
        }


        if (startSample != null) {
            try {
                if (startSample.startsWith("-")) {
                    long sl = 0;
                    String s = "" + startSample.charAt(startSample.length() - 1);
                    String v = startSample.replace("-", "").replace(s, "");
                    int v1 = Integer.valueOf(v);

                    if (s.equals("s")) {
                        sl = end.getTime() - (1000 * v1);
                    } else if (s.equals("m")) {
                        sl = end.getTime() - (60 * 1000 * v1);
                    } else if (s.equals("h")) {
                        sl = end.getTime() - (60 * 60 * 1000 * v1);
                    } else if (s.equals("d")) {
                        sl = end.getTime() - (24 * 60 * 60 * 1000 * v1);
                    } else if (s.equals("y")) {
                        sl = end.getTime() - (365 * 24 * 60 * 60 * 1000 * v1);
                    }
                    start = new Date(sl);
                } else if (startSample.length() == 14) {

                    start = processRelativeDate(startSample);
                }
                //unix time in seconds
                else if (startSample.length() == 10) {
                    Long l = Long.parseLong(startSample);
                    start = new Date(l * 1000);
                }
                //unix time in ms
                else if (startSample.length() == 13) {
                    start = new Date(Long.parseLong(startSample));
                } else if (isLongDateStr(endSample)) {
                    start = parseLongDateStr(startSample);

                    //   } else if (isLongEuropean(startSample)) {
                    //      start = parseLongEurpeanStr(startSample);
                } else if (isSQL(endSample)) {
                    start = parseLongDateStr(startSample);
                } else {
                    start = parseCommonStr(startSample);
                }


            } catch (NumberFormatException e) {
                throw new NimbitsException("Invalid Start Date");
            }

        }

        if (start == null || end == null) {
            throw new NimbitsException("Problem parsing date");
        } else if (start.getTime() >= end.getTime()) {
            throw new NimbitsException("Start time was more recent than end time");

        } else {

            retObj = TimespanModelFactory.createTimespan(start, end);

        }


        return retObj;


    }

    private static Date processRelativeDate(String startSample) {
        int pos = 0;
        int year = Integer.parseInt(startSample.substring(pos, pos += 4));
        int month = Integer.parseInt(startSample.substring(pos, pos += 2));
        month--;    // GregorianCalendar has zero based month.
        int day = Integer.parseInt(startSample.substring(pos, pos += 2));
        int hour = Integer.parseInt(startSample.substring(pos, pos += 2));
        int minute = Integer.parseInt(startSample.substring(pos, pos += 2));
        int seconds = Integer.parseInt(startSample.substring(pos, pos += 2));
        DateWrapper w = new DateWrapper(year, month, day);
        w.addHours(hour);
        w.addDays(day);
        w.addMinutes(minute);
        w.addSeconds(seconds);
        // GregorianCalendar gcDate = new GregorianCalendar(year, month, day, hour, minute, seconds);
        //    milliseconds = gcDate.getTimeInMillis();

        return w.asDate();
    }


}
