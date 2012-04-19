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

package com.nimbits.server.api.impl;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.file.*;
import com.nimbits.client.model.file.File;
import com.nimbits.server.api.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.gson.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:43 PM
 */
public class BlobServletImpl extends ApiServlet {
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {
            doInit(req, res, ExportType.plain);


        final Map<String,List<BlobKey>> blobs = blobstoreService.getUploads(req);
        final BlobKey blobKey = blobs.get(Parameters.myFile.getText()).get(0);
//        String diagramDescParam = req.getParameter(Const.PARAM_DESCRIPTION);
        final String entityId = req.getParameter(Parameters.fileId.getText());
        final String uploadType = req.getParameter(Parameters.uploadTypeHiddenField.getText());
        final String email = req.getParameter(Parameters.emailHiddenField.getText());
        final HttpSession session = req.getSession();

        String diagramNameParam = req.getParameter(Parameters.fileName.getText());
        final int lastIndex = diagramNameParam.lastIndexOf('\\');
        final String fileName = diagramNameParam.substring(lastIndex + 1);
        session.setAttribute(Parameters.email.getText(), CommonFactoryLocator.getInstance().createEmailAddress(email));



            final EntityName diagramName = CommonFactoryLocator.getInstance().createName(fileName, EntityType.file);
            PrintWriter out = res.getWriter();

            com.nimbits.client.model.file.File file = null;
            if (uploadType.equals(UploadType.newFile.name())) {
                Entity entity = EntityModelFactory.createEntity(diagramName, "", EntityType.file, ProtectionLevel.everyone,
                       user.getKey(), user.getKey());
                file = FileFactory.createFile(entity);


            }
            else if (uploadType.equals(UploadType.updatedFile.name()) && entityId != null) {

                List<Entity> result = EntityServiceFactory.getInstance().getEntityByKey(user, entityId, EntityType.file);
                if (! result.isEmpty()) {
                    file = (File) result.get(0);
                }




            }
            if (file != null) {
                file.setBlobKey(blobKey.getKeyString());
                Entity response = EntityServiceFactory.getInstance().addUpdateEntity(user, file);
                String json = GsonFactory.getInstance().toJson(response);
                res.setContentType("text/plain");
                res.setStatus(HttpServletResponse.SC_OK);
                out.print(json);
                out.flush();
               // out.close();
            }

        } catch (NimbitsException e) {
           if (user != null) {
               FeedServiceFactory.getInstance().postToFeed(user, e);
           }
        }


    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Parameters.blobkey.getText()));
        blobstoreService.serve(blobKey, res);
    }

}
