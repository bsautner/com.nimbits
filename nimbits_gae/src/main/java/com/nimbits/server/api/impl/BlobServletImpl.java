/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.api.impl;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.process.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class BlobServletImpl extends ApiBase {
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    private Logger logger = Logger.getLogger(BlobServletImpl.class.getName());

    @Autowired
    private TaskService taskService;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            addHeaders(resp);
            User user = (User) req.getAttribute(Parameters.user.getText());



            final Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
            final BlobKey blobKey = blobs.get(Parameters.myFile.getText()).get(0);
            final String entityId = req.getParameter(Parameters.fileId.getText());
            final String uploadType = req.getParameter(Parameters.uploadTypeHiddenField.getText());
            final String email = req.getParameter(Parameters.emailHiddenField.getText());
            final HttpSession session = req.getSession();
            session.setAttribute(Parameters.email.getText(), CommonFactory.createEmailAddress(email));

            //data upload
            if (uploadType.equals(EntityType.point.name())) {
                List<Entity> result = entityService.getEntityByKey(user, entityId, EntityType.point);
                if (!result.isEmpty()) {
                    Entity point = result.get(0);
                    taskService.startUploadTask(user, (Point) point, blobKey.getKeyString());
                }


            }


        } catch (Exception e) {
            logger.severe(e.getMessage());
        }


    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Parameters.blobkey.getText()));
        blobstoreService.serve(blobKey, res);
    }


}
