/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package integration;/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.json.JsonHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/7/11
 * Time: 4:02 PM
 */
public class JsonTest {

    @Test
    public void testString() {
        String string = "foobar";
        String s = "bar";

        String x = string.substring(0, string.length() - s.length());
        assertEquals(x, "foo");


    }

    @Test
    public void testM2M() throws IOException, InterruptedException, NimbitsException {

        EntityName pointName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);


        Point point = ClientHelper.client().addPoint(pointName);
        point.setFilterValue(-1);
        ClientHelper.client().updatePoint(point);
        Robot robot = new Robot();
        robot.setEmotion(Robot.Emotion.sad);


        ClientHelper.client().recordDataObject(pointName, robot, Robot.class);


        sleep(1000);

        Robot r = (Robot) ClientHelper.client().getCurrentDataObject(pointName, Robot.class);

        assertEquals(r.getEmotion(), Robot.Emotion.sad);


    }

    @Test
    public void testIsJson() {

        List<String> l = Arrays.asList("a,b,c");

        String not = "hello world";

        String json = GsonFactory.getInstance().toJson(l);

        assertTrue(JsonHelper.isJson(json));
        assertFalse(JsonHelper.isJson(not));


    }
}
