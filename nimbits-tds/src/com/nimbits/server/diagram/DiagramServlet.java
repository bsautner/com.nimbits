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

package com.nimbits.server.diagram;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.enums.UploadType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.diagram.DiagramName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.dao.diagram.DiagramDaoFactory;
import com.nimbits.server.user.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:43 PM
 */
public class DiagramServlet extends HttpServlet {
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        final Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        final BlobKey blobKey = blobs.get(Const.PARAM_MY_FILE);
        final String diagramNameParam = req.getParameter(Const.PARAM_NAME);
        final String diagramId = req.getParameter(Const.PARAM_DIAGRAM_ID);
        final String uploadType = req.getParameter(Const.PARAM_UPLOAD_TYPE_HIDDEN_FIELD);


        final User u;
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);

            final DiagramName diagramName = CommonFactoryLocator.getInstance().createDiagramName(diagramNameParam);

            if (uploadType.equals(UploadType.newFile.name())) {
                DiagramDaoFactory.getInstance().addDiagram(u, blobKey, diagramName);
            } else if (uploadType.equals(UploadType.updatedFile.name()) && diagramId != null) {
                long id = Long.valueOf(diagramId);
                DiagramDaoFactory.getInstance().updateDiagram(u, blobKey, diagramName, id);
            }
        } catch (NimbitsException ignored) {

        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Const.PARAM_BLOB_KEY));
        blobstoreService.serve(blobKey, res);
    }

}
