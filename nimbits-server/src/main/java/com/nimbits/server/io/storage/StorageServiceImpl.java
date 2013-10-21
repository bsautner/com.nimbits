/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.io.storage;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.GSFileOptions;
import com.nimbits.server.transaction.user.service.UserService;

import java.io.IOException;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 2:02 PM
 */
public class StorageServiceImpl {

    private UserService userService;

    public void test() throws IOException {
        FileService fileService = FileServiceFactory.getFileService();
        GSFileOptions.GSFileOptionsBuilder optionsBuilder = new GSFileOptions.GSFileOptionsBuilder()
                .setBucket("com/nimbits")
                .setKey("my_object")
                .setAcl("public-read");
        AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());


    }


    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }
}
