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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
            init(req, res, ExportType.plain);


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

            Entity entity = null;
            if (uploadType.equals(UploadType.newFile.name())) {
                entity = EntityModelFactory.createEntity(diagramName, "", EntityType.file, ProtectionLevel.everyone, UUID.randomUUID().toString(),
                       user.getUuid(), user.getUuid(),blobKey.getKeyString());

            }
            else if (uploadType.equals(UploadType.updatedFile.name()) && entityId != null) {

                entity = EntityServiceFactory.getInstance().getEntityByUUID(user, entityId);
                entity.setBlobKey(blobKey.getKeyString());



            }
            if (entity != null) {
                Entity response = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
                String json = GsonFactory.getInstance().toJson(response);
                res.setContentType("text/plain");
                res.setStatus(HttpServletResponse.SC_OK);
                out.print(json);
                out.flush();
               // out.close();
            }

        } catch (NimbitsException e) {
           if (user != null) {
               FeedServiceFactory.getInstance().postToFeed(super.user, e);
           }
        }


    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Parameters.blobkey.getText()));
        blobstoreService.serve(blobKey, res);
    }

}
