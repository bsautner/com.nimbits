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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.file.File;
import com.nimbits.client.model.file.FileFactory;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:43 PM
 */
@Transactional
@Service("blobApi")
public class BlobServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {
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
            String protectionLevelParam = req.getParameter(Parameters.protection.getText());
            final int lastIndex = diagramNameParam.lastIndexOf('\\');
            final String fileName = diagramNameParam.substring(lastIndex + 1);
            session.setAttribute(Parameters.email.getText(), CommonFactoryLocator.getInstance().createEmailAddress(email));

            final EntityName diagramName = CommonFactoryLocator.getInstance().createName(fileName, EntityType.file);
            PrintWriter out = res.getWriter();

            com.nimbits.client.model.file.File file = null;
            if (! uploadType.equals(EntityType.file.name())) {
                Entity entity = EntityModelFactory.createEntity(diagramName, "", EntityType.file, ProtectionLevel.everyone,
                        user.getKey(), user.getKey());
                file = FileFactory.createFile(entity, blobKey.getKeyString());


            }
            else if (entityId != null) {

                List<Entity> result = entityService.getEntityByKey(user, entityId, EntityType.file);
                if (! result.isEmpty()) {
                    file = (File) result.get(0);
                }




            }
            if (file != null) {
                file.setBlobKey(blobKey.getKeyString());
                Entity response = entityService.addUpdateEntity(user, file);
                String json = GsonFactory.getInstance().toJson(response);
                res.setContentType("text/plain");
                res.setStatus(HttpServletResponse.SC_OK);
                out.print(json);
                out.flush();
                // out.close();
            }

        } catch (NimbitsException e) {
            if (user != null) {

            }
        }


    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Parameters.blobkey.getText()));
        blobstoreService.serve(blobKey, res);
    }


    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
