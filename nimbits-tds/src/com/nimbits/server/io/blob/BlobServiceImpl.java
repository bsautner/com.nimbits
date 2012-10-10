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

package com.nimbits.server.io.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.model.file.File;
import com.nimbits.client.service.blob.BlobService;
import com.nimbits.server.process.task.Task;
import com.nimbits.server.transactions.service.entity.EntityServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/12/12
 * Time: 6:25 PM
 */
@Service("blobService")
@Transactional
public class BlobServiceImpl  extends RemoteServiceServlet implements
        RequestCallback, BlobService{


    private final BlobstoreService blobstoreService;
    private EntityServiceImpl entityService;
    private Task taskFactory;

    public BlobServiceImpl() {
        blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    }

    @Override
    public String getBlobStoreUrl(final String url) {
        return blobstoreService.createUploadUrl(url);
    }

    @Override
    public void deleteBlob(final File entity) {
        blobstoreService.delete(new BlobKey(entity.getBlobKey()));
    }

    @Override
    public void onResponseReceived(final Request request, final Response response) {

    }

    @Override
    public void onError(final Request request,final Throwable throwable) {

    }

    public void setEntityService(EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    public EntityServiceImpl getEntityService() {
        return entityService;
    }

    public void setTaskFactory(Task taskFactory) {
        this.taskFactory = taskFactory;
    }

    public Task  getTaskFactory() {
        return taskFactory;
    }
}
