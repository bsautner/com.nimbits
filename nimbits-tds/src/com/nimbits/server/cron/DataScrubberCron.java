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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.cron;

import com.nimbits.client.model.Const;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class DataScrubberCron extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(DataScrubberCron.class.getName());

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
//        Cache cache = null;
//        PrintWriter out = null;
//
//
//        try {
//            out = resp.getWriter();
//
//            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
//            List<Long> confirmedPoints;
//
//
//            long start = 0;
//            long end;
//
//            if (cache.containsKey("scrubberstart")) {
//                start = (Long) cache.get("scrubberstart");
//            } else {
//                cache.put("scrubberstart", 0);
//            }
//
//            if (cache.containsKey("scrubberconfirmed")) {
//                confirmedPoints = (ArrayList<Long>) cache.get("scrubberconfirmed");
//            } else {
//
//                confirmedPoints = new ArrayList<Long>();
//            }
//
//            end = start + 999;
//
//
//            out.println("Checking " + start + " to " + end);
//            cache.remove("scrubberstart");
//            cache.put("scrubberstart", end + 1);
//            List<Value> r = null;
//
//
//            int i = 0;
//            r = RecordedValueTransactionFactory.getInstance().getValueRange(start, end);
//
//            if (r.size() > 0) {
//
//                for (Value vx : r) {
//                    i++;
//
//                    if (!confirmedPoints.contains(vx.getPoint())) {
//
//                        Point p = PointTransactionsFactory.getInstance().getPointByID(vx.getPoint());
//
//                        if (p == null) {
//                            //pm.deletePersistent(vx);
//                            out.println("Deleting lost data: " + vx.getPoint());
//                            confirmedPoints.add(vx.getPoint());
//                            RecordedValueServiceFactory.getInstance().startDeleteDataTask(vx.getPoint(), false, 0, null);
//
//                            cache.remove("scrubberstart");
//                            cache.put("scrubberstart", start + i);
//
//
//                        } else {
//                            out.println("Found Owner: " + p.getName());
//                            confirmedPoints.add(vx.getPoint());
//                        }
//                    }
//                }
//            } else {
//                cache.remove("scrubberstart");
//                start = 0;
//                cache.remove("scrubberstart");
//                cache.put("scrubberstart", start);
//                cache.remove("scrubberconfirmed");
//            }
//
//
//            cache.put("scrubberconfirmed", confirmedPoints);
//
//        } catch (Exception e) {
//            log.severe(e.getMessage());
//            if (cache != null) {
//
//                cache.remove("scrubberstart");
//                cache.remove("scrubberconfirmed");
//            }
//            out.println(e.getMessage());
//
//        }


    }

}
