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
import com.google.appengine.api.files.*;
import com.nimbits.client.enums.*;

import java.io.*;
import java.nio.channels.*;

public class BlobStoreImpl implements BlobStore {

    @Override
    public String createFile(final String data,final ExportType exportType)   {
        // Get a file service

        try {
            final FileService fileService = FileServiceFactory.getFileService();

            // Create a new Blob file with mime-type "text/plain"
            AppEngineFile file = fileService.createNewBlobFile(exportType.getCode());

            // Open a channel to write to it
            boolean lock = false;
            FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

            // Different standard Java ways of writing to the channel
            // are possible. Here we use a PrintWriter:
            PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            out.println(data);


            // Close without finalizing and save the file path for writing later
            out.close();
            String path = file.getFullPath();

            // Write more to the file in a separate request:
            file = new AppEngineFile(path);

            // This time lock because we intend to finalize
            lock = true;
            writeChannel = fileService.openWriteChannel(file, lock);

            // This time we write to the channel using standard Java
            // writeChannel.write(ByteBuffer.wrap
            //        ("And miles to go before I sleep.".getBytes()));

            // Now finalize
            writeChannel.closeFinally();

            // Later, read from the file using the file API
            lock = false; // Let other people read at the same time
            FileReadChannel readChannel = fileService.openReadChannel(file, false);

            // Again, different standard Java ways of reading from the channel.
            BufferedReader reader =
                    new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            String line = reader.readLine();
            // line = "The woods are lovely dark and deep."

            readChannel.close();

            // Now read from the file using the Blobstore API
            BlobKey blobKey = fileService.getBlobKey(file);
            BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
            String segment = new String(blobStoreService.fetchData(blobKey, 30, 40));

            return blobKey.getKeyString();
        }   catch (Exception e) {
            return null;

        }

    }
}
