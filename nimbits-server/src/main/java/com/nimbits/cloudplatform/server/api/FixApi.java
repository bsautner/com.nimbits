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

import com.nimbits.cloudplatform.client.enums.AuthLevel;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
import com.nimbits.cloudplatform.server.transactions.user.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 2/2/13
 * Time: 12:08 PM
 */


public class FixApi extends ApiBase {


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        StringBuilder sb = new StringBuilder();
        List<User> user = UserServiceFactory.getInstance().getUserByKey("pchsimmonds@gmail.com", AuthLevel.admin);
        if (user.isEmpty()) {
            sb.append("user not found");
        }
         else {
            sb.append("got user<br />");
            EntityName name = CommonFactory.createName("Temperature", EntityType.point);
            Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.get(0).getKey(),user.get(0).getKey() );
            Point point = PointModelFactory.createPointModel(entity, 0.0, 90, "", 0.0, false, false, false, 0, false, FilterType.none, 0.0, false, PointType.basic,0, false, 0.0);

            entityService.addUpdateEntity(user.get(0), point);
            sb.append("created entity<br />");
        }


        completeResponse(resp, user.toString());

    }
}
