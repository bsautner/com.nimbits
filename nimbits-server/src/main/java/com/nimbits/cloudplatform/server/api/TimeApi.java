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

package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.server.gson.GsonFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Author: Benjamin Sautner
 * Date: 2/2/13
 * Time: 12:08 PM
 */


public class TimeApi  extends ApiBase {


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {


        Long time = new Date().getTime();
        String json = GsonFactory.getInstance().toJson(time);

        completeResponse(resp, json);

    }
}
