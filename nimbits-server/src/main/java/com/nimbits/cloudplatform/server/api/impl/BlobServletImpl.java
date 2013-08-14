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

package com.nimbits.cloudplatform.server.api.impl;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:43 PM
 */

@Service("blob")
public class BlobServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();



    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {
            doInit(req, res, ExportType.plain);


            final Map<String,List<BlobKey>> blobs = blobstoreService.getUploads(req);
            final BlobKey blobKey = blobs.get(Parameters.myFile.getText()).get(0);
            final String entityId = req.getParameter(Parameters.fileId.getText());
            final String uploadType = req.getParameter(Parameters.uploadTypeHiddenField.getText());
            final String email = req.getParameter(Parameters.emailHiddenField.getText());
            final HttpSession session = req.getSession();
            session.setAttribute(Parameters.email.getText(), CommonFactory.createEmailAddress(email));

             //data upload
            if (uploadType.equals(EntityType.point.name())) {
                List<Entity> result = EntityServiceImpl.getEntityByKey(user, entityId, EntityType.point);
                if (! result.isEmpty()) {
                    Entity point =  result.get(0);
                    TaskImpl.startUploadTask(user, (Point) point, blobKey);
                }



            }


        } catch (Exception e) {
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
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        if (isPost(req)) {

            doPost(req, resp);
        }
        else {
            doGet(req, resp);
        }


    }

}
