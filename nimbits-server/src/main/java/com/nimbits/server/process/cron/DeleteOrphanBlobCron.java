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

package com.nimbits.server.process.cron;

import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 2:00 PM
 */
@Service("deleteOrphanCron")

public class DeleteOrphanBlobCron extends HttpServlet implements org.springframework.web.HttpRequestHandler{

    private static final long serialVersionUID = 1L;


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
            processRequest();
    }

    protected  static void processRequest() throws IOException {
//        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
//
//        if  (iterator.hasNext()){
//            final BlobInfo i = iterator.next();
//            TaskImpl.startDeleteOrphanedBlobTask(i.getBlobKey());
//
//        }


    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
