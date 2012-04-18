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

package com.nimbits.server.transactions.dao.point;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.value.RecordedValueServiceFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 8:02 PM
 */
public class PointDaoImplTest extends NimbitsServletTest {

    @Test
    public void getPointsTest() throws NimbitsException {

        Map<String, Point> e = new HashMap<String, Point>(2);

        e.put(point.getKey(), (Point) EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), PointEntity.class.getName()).get(0));
        e.put(pointChild.getKey(), (Point) EntityServiceFactory.getInstance().getEntityByKey(pointChild.getKey(), PointEntity.class.getName()).get(0));
        Map<String, Entity> result = RecordedValueServiceFactory.getInstance().getCurrentValues(e);
        assertEquals(2, result.size());


    }
}
