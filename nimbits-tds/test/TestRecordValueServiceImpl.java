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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/22/11
 * Time: 3:00 PM
 */
public class TestRecordValueServiceImpl {

    @Test
    public void testStartRecordValueTask() {
        Value v = ValueModelFactory.createValueModel(0.0, 0.0, 0.0, new Date(), 0, "");
        // 0000&p2=IN2&v2=0000&p3=IN3&v3=0000&p4=IN4&v4=0088&p5=IN5&v5=0328&p6=IN6&v6=0070&p7=INP1&v7=000&p8=INP2&v8=0&p9=Temp&v9=23.3&p10=Bat&v10=0.3&p11=Frq&v11=00.0&p12=Phi&v12=00.0" "AppEngine-Google; (+http://code.google.com/appengine)" "app.nimbits.com" ms=191 cpu_ms=287 api_cpu_ms=217 cpm_usd=0.008349 queue_name=recordvaluequeue task_name=10957361277155433609 instance=00c61b117cb1364c5c6603c8348714ca19165c
        String j = GsonFactory.getInstance().toJson(v);
        System.out.println(j);

        assertFalse(Double.valueOf("0000").isInfinite());
        assertFalse(Double.valueOf("0088").isInfinite());
        assertFalse(Double.valueOf("000").isInfinite());
        assertFalse(Double.valueOf("00.0").isInfinite());
    }

}
