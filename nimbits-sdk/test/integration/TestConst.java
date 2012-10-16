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

import com.nimbits.client.constants.Const;

import com.nimbits.client.model.email.EmailAddress;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/6/11
 * Time: 4:46 PM
 */
public class TestConst {


    @Test
    public void testConvert() throws ParseException {
       // DecimalFormat decimal = new DecimalFormat("##0.##E0");





        String e = "1.3194036e+12";
        String e2 = "1319403600000";

        BigDecimal m = new BigDecimal(e);


        System.out.println(m.longValue());

          BigDecimal m2 = new BigDecimal(e2);


        System.out.println(m2.longValue());



    }


}
