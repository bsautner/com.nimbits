/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.process.cron;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 2:00 PM
 */
public class DeleteOrphanBlobCron  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointCron.class.getName());


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
            processRequest();
    }

    protected  static void processRequest() throws IOException {
//        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
//
//        if  (iterator.hasNext()){
//            final BlobInfo i = iterator.next();
//            TaskFactory.getInstance().startDeleteOrphanedBlobTask(i.getBlobKey());
//
//        }


    }
}
