package com.nimbits.server.blobstore;

import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;

/**
 * Created by IntelliJ IDEA.
 * User: ubuntu
 * Date: 11/15/11
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlobStore {
    String createFile(final String data, final ExportType exportType) throws NimbitsException;
}
