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
import com.google.gwt.http.client.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.service.blob.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/12/12
 * Time: 6:25 PM
 */
public class BlobServiceImpl  extends RemoteServiceServlet implements
        RequestCallback, BlobService{
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();


    @Override
    public String getBlobStoreUrl(String url) {
        return blobstoreService.createUploadUrl(url);
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onError(Request request, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
