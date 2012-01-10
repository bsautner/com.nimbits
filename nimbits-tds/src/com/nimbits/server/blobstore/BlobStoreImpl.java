package com.nimbits.server.blobstore;


import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.*;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;

public class BlobStoreImpl implements BlobStore {

    @Override
    public String createFile(final String data,final ExportType exportType) throws NimbitsException {
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
        } catch (IOException e) {
            throw new NimbitsException(e.getMessage());
        }
    }
}
