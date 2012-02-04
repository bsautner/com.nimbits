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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.model.Const;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class ExportService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final String pointNameParam = req.getParameter(Const.PARAM_POINT);
        final String countStr = req.getParameter(Const.PARAM_COUNT);
        String segStr = req.getParameter(Const.PARAM_SEGMENT);
        final String startDate = req.getParameter(Const.PARAM_START_DATE);
        final String endDate = req.getParameter(Const.PARAM_END_DATE);

        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();

        out.println("<html><body>hello</body></html>");
        out.flush();
        //   out.close();

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Const.PARAM_BLOB_KEY));
        blobstoreService.serve(blobKey, resp);
    }
}
