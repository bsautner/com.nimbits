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

package com.nimbits.server.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.UploadType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.point.*;
import com.nimbits.server.user.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:43 PM
 */
public class BlobServlet extends HttpServlet {
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private static final Logger log = Logger.getLogger(BlobServlet.class.getName());
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        final Map<String,List<BlobKey>> blobs = blobstoreService.getUploads(req);
        final BlobKey blobKey = blobs.get(Const.PARAM_MY_FILE).get(0);
        String diagramDescParam = req.getParameter(Const.PARAM_DESCRIPTION);
        final String entityId = req.getParameter(Const.PARAM_FILE_ID);
        final String uploadType = req.getParameter(Const.PARAM_UPLOAD_TYPE_HIDDEN_FIELD);
        final String email = req.getParameter(Const.PARAM_EMAIL_HIDDEN_FIELD);
        final HttpSession session = req.getSession();

        String diagramNameParam = req.getParameter(Const.PARAM_FILE_NAME);
        final int lastIndex = diagramNameParam.lastIndexOf('\\');
        final String fileName = diagramNameParam.substring(lastIndex + 1);
        session.setAttribute(Const.PARAM_EMAIL, CommonFactoryLocator.getInstance().createEmailAddress(email));
        final User u;
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);


            final EntityName diagramName = CommonFactoryLocator.getInstance().createName(fileName);





            if (uploadType.equals(UploadType.newFile.name())) {
                Entity entity = EntityModelFactory.createEntity(diagramName, "", EntityType.file, ProtectionLevel.everyone, UUID.randomUUID().toString(),
                        u.getUuid(), u.getUuid(),blobKey.getKeyString());
                EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
               // PointServiceFactory.getInstance().addPoint(u, entity);


            //    DiagramTransactionFactory.getInstance(u).addDiagram(blobKey, diagramName);
            }
            else if (uploadType.equals(UploadType.updatedFile.name()) && entityId != null) {

                Entity entity = EntityTransactionFactory.getInstance(u).getEntityByUUID(entityId);
                entity.setBlobKey(blobKey.getKeyString());

                EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
              //  DiagramTransactionFactory.getInstance(u).updateDiagram(blobKey, diagramName, diagramId);

            }
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }

    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Const.PARAM_BLOB_KEY));
        blobstoreService.serve(blobKey, res);
    }

}
