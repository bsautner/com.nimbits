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

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.user.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

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
        final BlobKey blobKey = blobs.get(Parameters.myFile.getText()).get(0);
//        String diagramDescParam = req.getParameter(Const.PARAM_DESCRIPTION);
        final String entityId = req.getParameter(Parameters.fileId.getText());
        final String uploadType = req.getParameter(Parameters.uploadTypeHiddenField.getText());
        final String email = req.getParameter(Parameters.email.getText());
        final HttpSession session = req.getSession();

        String diagramNameParam = req.getParameter(Parameters.fileName.getText());
        final int lastIndex = diagramNameParam.lastIndexOf('\\');
        final String fileName = diagramNameParam.substring(lastIndex + 1);
        session.setAttribute(Parameters.email.getText(), CommonFactoryLocator.getInstance().createEmailAddress(email));
        final User u;
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);


            final EntityName diagramName = CommonFactoryLocator.getInstance().createName(fileName, EntityType.file);
            PrintWriter out = res.getWriter();




            Entity entity = null;
            if (uploadType.equals(UploadType.newFile.name())) {
                entity = EntityModelFactory.createEntity(diagramName, "", EntityType.file, ProtectionLevel.everyone, UUID.randomUUID().toString(),
                        u.getUuid(), u.getUuid(),blobKey.getKeyString());

            }
            else if (uploadType.equals(UploadType.updatedFile.name()) && entityId != null) {

                entity = EntityServiceFactory.getInstance().getEntityByUUID(u, entityId);
                entity.setBlobKey(blobKey.getKeyString());



            }
            if (entity != null) {
                Entity response = EntityServiceFactory.getInstance().addUpdateEntity(u, entity);
                String json = GsonFactory.getInstance().toJson(response);
                res.setContentType("text/plain");
                res.setStatus(HttpServletResponse.SC_OK);
                out.print(json);
                out.flush();
               // out.close();
            }

        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }


    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Parameters.blobkey.getText()));
        blobstoreService.serve(blobKey, res);
    }

}
