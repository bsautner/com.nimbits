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

package com.nimbits.server.api.impl;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.api.ApiServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ExportServiceImpl extends ApiServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try {
            doInit(req, resp, ExportType.unknown);
            BlobKey blobKey = new BlobKey(req.getParameter(Parameters.blobkey.getText()));
            blobstoreService.serve(blobKey, resp);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NimbitsException e) {
             resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
             resp.addHeader("ERROR", e.getMessage());
        }

    }
}
