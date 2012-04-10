/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.*;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.point.*;
import helper.NimbitsServletTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 2:27 PM
 */
public class EntityServiceTest extends NimbitsServletTest {


    @Test
    public void deleteEntityTest() throws NimbitsException {

        List<Entity> entities =  EntityServiceFactory.getInstance().getEntities();
        assertTrue(!entities.isEmpty());
        Entity e = EntityServiceFactory.getInstance().getEntityByName(pointName,EntityType.point);
        assertNotNull(e);
        Entity c = EntityServiceFactory.getInstance().getEntityByName(pointChildName,EntityType.point);
        Entity g = EntityServiceFactory.getInstance().getEntityByName(groupName,EntityType.category);

        assertNotNull(c);
        assertNotNull(g);
        List<Entity> children =  EntityServiceFactory.getInstance().getChildren(g, EntityType.point);
        assertTrue(!children.isEmpty());

        EntityServiceFactory.getInstance().deleteEntity(g);
        e = EntityServiceFactory.getInstance().getEntityByName(pointName,EntityType.point);
        c = EntityServiceFactory.getInstance().getEntityByName(pointChildName,EntityType.point);
        g = EntityServiceFactory.getInstance().getEntityByName(groupName,EntityType.category);


        assertNull(g);
        assertNull(e);
        assertNull(c);


    }

    @Test(expected=NimbitsException.class)
    public void duplicateNameTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.point);
        Entity model = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
               user.getKey(), user.getKey());

      //  Entity e = EntityServiceFactory.getInstance().addUpdateEntity(model);
        Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
              user.getKey(), user.getKey());
        ///Entity e2 = EntityServiceFactory.getInstance().addUpdateEntity(model2);
        Point p1 = PointModelFactory.createPointModel(model);
        PointServiceFactory.getInstance().addPoint(user, p1);
        Point p2 = PointModelFactory.createPointModel(model2);
        PointServiceFactory.getInstance().addPoint(user, p2);

    }
    @Test
    public void duplicateNameGroupsOKTest()  {
        try {
            EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.category);
            Entity model = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());
            Entity e = EntityServiceFactory.getInstance().addUpdateEntity(model);
            Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                 user.getKey(), user.getKey());

            Entity e2 = EntityServiceFactory.getInstance().addUpdateEntity(model2);
        } catch (NimbitsException e1) {
            fail();
        }


    }

    @Test
    public void getEntitiesTest() throws NimbitsException {

        List<Entity> entities =  EntityServiceFactory.getInstance().getEntities();
        assertTrue(!entities.isEmpty());

    }

    @Test
    public void getEntityByNameTest() throws NimbitsException {
        Point  r = (Point) EntityServiceFactory.getInstance().getEntityByName(user, pointName,PointEntity.class.getName());
        assertNotNull(r);

    }

}
