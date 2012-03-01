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

package com.nimbits.server.service;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.model.*;

import javax.servlet.http.*;
import java.io.*;


public class ExportService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final String pointNameParam = req.getParameter(Const.Params.PARAM_POINT);
        final String countStr = req.getParameter(Const.Params.PARAM_COUNT);
        String segStr = req.getParameter(Const.Params.PARAM_SEGMENT);
        final String startDate = req.getParameter(Const.Params.PARAM_START_DATE);
        final String endDate = req.getParameter(Const.Params.PARAM_END_DATE);

        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();

        out.println("<html><body>hello</body></html>");
        out.flush();
        //   out.close();

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Const.Params.PARAM_BLOB_KEY));
        blobstoreService.serve(blobKey, resp);
    }
}
