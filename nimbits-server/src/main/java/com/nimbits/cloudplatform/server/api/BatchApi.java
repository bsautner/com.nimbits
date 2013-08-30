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


import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Author: Benjamin Sautner
 * Date: 12/28/12
 * Time: 4:11 PM
 */
//TODO accept JSON in body instead of param



public class BatchApi extends ApiBase {
    final Logger log = Logger.getLogger(BatchApi.class.getName());
    public final static Type listType = new TypeToken<Map<String, List<ValueModel>>>() {
    }.getType();


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {


        setup(req, resp, true);


        if (user != null && !user.isRestricted()) {


            HashMap<String, List<Value>> map  = GsonFactory.getInstance().fromJson(json, listType);
            for (String id : map.keySet()) {

                List<Entity> entitySample = EntityServiceImpl.getEntityByKey(user, id, EntityType.point);
                if (! entitySample.isEmpty()) {
                    List<Value> valueList = map.get(id);

                    for (Value v : valueList) {
                        ValueTransaction.recordValue(user, entitySample.get(0), v);

                    }


                }

            }


            resp.setStatus(HttpServletResponse.SC_OK);




        }



    }



    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp)  {





        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);






    }

}