package com.nimbits.server.blobstore;


public class BlobStoreFactory {
    private static BlobStore instance;

    public static BlobStore getInstance() {
        if (instance == null) {
            instance = new BlobStoreImpl();
        }
        return instance;
    }


}
