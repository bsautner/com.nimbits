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

package com.nimbits.server.time;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.service.timespan.TimespanService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 5/3/11
 * Time: 7:55 PM
 */
@Service("timespanService")
public class TimespanServiceImpl extends RemoteServiceServlet implements TimespanService {
    private static final long serialVersionUID = 1L;

    @Override
    public Date zeroOutDate(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND) * -1);
        c.add(Calendar.SECOND, c.get(Calendar.SECOND) * -1);
        c.add(Calendar.MINUTE, c.get(Calendar.MINUTE) * -1);
        c.add(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) * -1);
        return c.getTime();
    }

    @Override
    public Timespan createTimespan(final String start, final String end) throws NimbitsException {

        return interpretTimespan(start, end);
    }

    @Override
    public Timespan createTimespan(final String start, final String end,final int offset) throws NimbitsException {
       Timespan ts = interpretTimespan(start, end);
       int offMs = offset * 60 * 1000 ;

        Date sx = ts.isStartRequiresOffset() ?  new Date(ts.getStart().getTime() + offMs) : ts.getStart();

       Date ex = ts.isEndRequiresOffset() ?  new Date(ts.getEnd().getTime() + offMs) : ts.getEnd();


       return TimespanModelFactory.createTimespan(sx, ex);
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
    private boolean isLongDateStr(final String s) {
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
    private boolean isLongEuropean(final String s) {
        return s.matches("^((((31\\/(0?[13578]|1[02]))|((29|30)\\/(0?[1,3-9]|1[0-2])))\\/(1[6-9]|[2-9]\\d)?\\d{2})|(29\\/0?2\\/(((1[6-9]|[2-9]\\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))|(0?[1-9]|1\\d|2[0-8])\\/((0?[1-9])|(1[0-2]))\\/((1[6-9]|[2-9]\\d)?\\d{2})) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$");


    }

    //TODO replace with regex
    private boolean isSQL(final String s) {
        SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");
        boolean retVal;

        try {
            dtf.parse(s);
            retVal = true;
        } catch (ParseException e) {
            retVal = false;
        }
        return retVal;

    }
    //TODO replace with regex
    private boolean isMil(final String s) {
        SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
        boolean retVal;

        try {
            dtf.parse(s);
            retVal = true;
        } catch (ParseException e) {
            retVal = false;
        }
        return retVal;

    }
    private Date parseLongDateStr(final String s) throws ParseException {
        SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");

        return dtf.parse(s);
    }
    private Date parseMilDateStr(final String s) throws ParseException {
        SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");

        return dtf.parse(s);
    }
    private Date parseLongEurpeanStr(final String s) throws ParseException {
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dtf.parse(s);
    }

    private Timespan interpretTimespan(final String startSample, final String endSample) throws NimbitsException {

        Timespan retObj;

        Date start = null, end = null;
        boolean startRequiresOffset = false;
        boolean endRequiresOffset = false;

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
                    endRequiresOffset = true;
                } else if (isLongDateStr(endSample)) {
                    end = parseLongDateStr(endSample);
                    endRequiresOffset = true;
                } else if (isLongEuropean(endSample)) {
                    end = parseLongEurpeanStr(endSample);
                    endRequiresOffset = true;
                } else if (isSQL(endSample)) {
                    end = parseLongDateStr(endSample);
                    endRequiresOffset = true;
                } else if (isMil(endSample)) {
                   end = parseMilDateStr(endSample);
                   endRequiresOffset = true;
                } else

                {
                    throw new NimbitsException("Invalid End Date: " + endSample);
                }

            } catch (NumberFormatException e) {
                throw new NimbitsException("Invalid End Date");
            } catch (ParseException e) {
                throw new NimbitsException(e.getMessage());
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
                    startRequiresOffset = true;
                } else if (isLongEuropean(startSample)) {
                    start = parseLongEurpeanStr(startSample);
                    startRequiresOffset = true;
                } else if (isSQL(endSample)) {
                    start = parseLongDateStr(startSample);
                    startRequiresOffset = true;
                } else if (isMil(endSample)) {
                    start = parseMilDateStr(startSample);
                    startRequiresOffset = true;
                } else {
                    throw new NimbitsException("Invalid Start Date");
                }


            } catch (NumberFormatException e) {
                throw new NimbitsException("Invalid Start Date");
            } catch (ParseException e) {
                throw new NimbitsException(e.getMessage());
            }

        }

        if (start == null || end == null) {
            throw new NimbitsException("Problem parsing date");
        } else if (start.getTime() >= end.getTime()) {
            throw new NimbitsException("Start time was more recent than end time");

        } else {

            retObj = TimespanModelFactory.createTimespan(start, end, startRequiresOffset, endRequiresOffset);

        }


        return retObj;


    }

    private Date processRelativeDate(final String startSample) {
        int pos = 0;
        int year = Integer.parseInt(startSample.substring(pos, pos += 4));
        int month = Integer.parseInt(startSample.substring(pos, pos += 2));
        month--;    // GregorianCalendar has zero based month.
        int day = Integer.parseInt(startSample.substring(pos, pos += 2));
        int hour = Integer.parseInt(startSample.substring(pos, pos += 2));
        int minute = Integer.parseInt(startSample.substring(pos, pos += 2));
        int seconds = Integer.parseInt(startSample.substring(pos, pos += 2));

        GregorianCalendar gcDate = new GregorianCalendar(year, month, day, hour, minute, seconds);
        //    milliseconds = gcDate.getTimeInMillis();

        return gcDate.getTime();
    }

    public Timespan createTimespan(Date startSample, Date endSample) {
        return  TimespanModelFactory.createTimespan(startSample, endSample);
    }
}
