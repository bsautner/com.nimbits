package com.nimbits.server.blob;

import com.nimbits.client.service.blob.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 12:42 PM
 */
public class BlobServiceFactory {

    public static BlobService getInstance() {

        return new BlobServiceImpl();

    }
}
