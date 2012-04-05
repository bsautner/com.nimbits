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

package com.nimbits.server.task;

import javax.servlet.http.*;
import java.util.logging.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/5/11
 * Time: 11:08 AM
 */
public class UpdatePointStatsTask extends HttpServlet

{

    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
//        final Gson gson = GsonFactory.getInstance();
//        final String pointJson = req.getParameter(Const.PARAM_POINT);
//        final String valueJson = req.getParameter(Const.PARAM_VALUE);
//        final String userJson = req.getParameter(Const.PARAM_USER);
//
//        final Point p = gson.fromJson(pointJson, PointModel.class);
//        final Value v = gson.fromJson(valueJson, ValueModel.class);
//        final User u = gson.fromJson(userJson, UserModel.class);
//
//
//        try {
//         //   PointTransactionsFactory.getInstance().updatePointStats(u, p, v, false);
//        } catch (ConcurrentModificationException e) {
//            //log.warning("Restarted Point Stat Update task for point " + p.getName().getValue() + " " + u.getEmail().getValue());
//            //// delete me context.addTrace("Restarted point stat update" + e.getMessage());
//            //  TaskFactoryLocator.getInstance().startUpdatePointStatsTask(context, u, p, v);
//        } catch (NimbitsException e) {
//            log.severe(e.getMessage());
//        }

    }


}
