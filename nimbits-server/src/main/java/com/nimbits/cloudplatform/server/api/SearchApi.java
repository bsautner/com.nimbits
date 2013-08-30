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

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.search.EntitySearchService;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 3/11/13
 * Time: 3:56 PM

 */

public class SearchApi extends ApiBase   {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException  {


            setup(req, resp, false);
            String search = req.getParameter(Parameters.search.name());
            String t =req.getParameter(Parameters.type.getText());
            int tr = Integer.valueOf(t);
            EntityType type = EntityType.get(tr);
            List<Entity> result;

            if (type != null && ! Utils.isEmptyString(search)) {
                Results<ScoredDocument> results = EntitySearchService.findEntity(search, type);
                Iterator iterator = results.iterator();
                result = new ArrayList<Entity>(results.getNumberReturned());
                while (iterator.hasNext()) {

                        ScoredDocument doc = (ScoredDocument) iterator.next();
                        String owner = doc.getOnlyField(Parameters.owner.name()).getText();
                        EntityName name = CommonFactory.createName(doc.getOnlyField(Parameters.name.name()).getText(), type);
                        ProtectionLevel level = ProtectionLevel.get(Integer.valueOf(doc.getOnlyField(Parameters.protection.name()).getText()));
                        if ((user != null && user.getEmail().getValue().equals(owner)) || level.equals(ProtectionLevel.everyone)) {
                            result.add(EntityModelFactory.createEntity(
                                    name, type

                            ));
                        }




                }
            }
            else {
                result = Collections.emptyList();
            }
            String json = GsonFactory.getInstance().toJson(result);
            completeResponse(resp, json);




    }
}