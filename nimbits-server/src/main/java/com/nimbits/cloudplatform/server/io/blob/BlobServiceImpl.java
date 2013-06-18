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

package com.nimbits.cloudplatform.server.io.blob;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.service.blob.BlobService;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.springframework.stereotype.Service;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/12/12
 * Time: 6:25 PM
 */
@Service("blobService")

public class BlobServiceImpl  extends RemoteServiceServlet implements BlobService{


    private final BlobstoreService blobstoreService;
    private EntityServiceImpl entityService;


    public BlobServiceImpl() {
        blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    }

    @Override
    public String getBlobStoreUrl(final String url) {
        return blobstoreService.createUploadUrl(url);
    }




}
