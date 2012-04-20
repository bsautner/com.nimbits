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

package com.nimbits.server.admin;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.server.process.task.*;

import javax.servlet.http.*;
import java.io.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 1:02 PM
 */
public class UpgradeServlet extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        resp.setContentType(Const.CONTENT_TYPE_HTML);

        final PrintWriter out = resp.getWriter();
        out.println(Const.HTML_BOOTSTRAP);
        out.println("<P>Starting upgrade tasks. You can check the default task queue in the admin console for status. When all tasks are completed, the upgrade has finished." +
                "This upgrade includes a migration from storing values in the data store to the blobstore and may take some time. Please monitor your quota to ensure it is not " +
                "exceeded during the upgrade process. Only run this once. Please contact support if you need any help.</P>");


        TaskFactory.getInstance().startUpgradeTask(Action.start, null, 0);

    }



}
