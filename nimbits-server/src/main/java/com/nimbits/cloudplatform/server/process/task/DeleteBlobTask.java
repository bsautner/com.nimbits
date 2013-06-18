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

package com.nimbits.cloudplatform.server.process.task;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.shared.Utils;
import org.springframework.stereotype.Service;


import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 1:30 PM
 */
@Service("deleteBlobTask")

public class DeleteBlobTask extends HttpServlet  implements org.springframework.web.HttpRequestHandler{

    private static final Logger log = Logger.getLogger(DeleteBlobTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

        try {
            processRequest(req);

        } catch (Exception e1) {
            LogHelper.logException(this.getClass(), e1);
        }
    }

    public static void processRequest(final ServletRequest req)  {


        final String key = req.getParameter(Parameters.key.getText());

        if (!Utils.isEmptyString(key)) {
            final BlobKey blobKey = new BlobKey(key);
            final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            blobstoreService.delete(blobKey);


        }

    }


}

