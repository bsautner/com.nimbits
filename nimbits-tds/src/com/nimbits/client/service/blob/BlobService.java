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

package com.nimbits.client.service.blob;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.model.file.File;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 5:38 PM
 */
@RemoteServiceRelativePath("blobService")
public interface BlobService extends RemoteService {

    String getBlobStoreUrl(final String url);
    void deleteBlob(final File entity);

    public static class App {
        private static BlobServiceAsync ourInstance = GWT.create(BlobService.class);

        public static synchronized BlobServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
