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

package com.nimbits.server.io.blob;


import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.files.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;

import java.io.*;
import java.nio.channels.*;

public class BlobStoreImpl implements BlobStore {
  //  private final Logger log = Logger.getLogger(BlobStoreImpl.class.getName());
    @Override
    public String createFile(EntityName name, final String data,final ExportType exportType) throws IOException {
        // Get a file service

        final FileService fileService = FileServiceFactory.getFileService();
        AppEngineFile file = fileService.createNewBlobFile(exportType.getCode(), name.getValue() + '.' + exportType.getFileExtension());

        FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        try {
            out.println(data);
            writeChannel.closeFinally();
            String path = file.getFullPath();
            file = new AppEngineFile(path);
            BlobKey blobKey = fileService.getBlobKey(file);
            return blobKey.getKeyString();
        }
        finally {
            out.close();
        }
    }




}
