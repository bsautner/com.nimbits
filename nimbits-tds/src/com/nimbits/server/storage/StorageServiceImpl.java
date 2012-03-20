package com.nimbits.server.storage;

import com.google.appengine.api.files.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 2:02 PM
 */
public class StorageServiceImpl {

    public void test() throws IOException {
        FileService fileService = FileServiceFactory.getFileService();
        GSFileOptions.GSFileOptionsBuilder optionsBuilder = new GSFileOptions.GSFileOptionsBuilder()
                .setBucket("nimbits")
                .setKey("my_object")
                .setAcl("public-read");
        AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());


    }



}
