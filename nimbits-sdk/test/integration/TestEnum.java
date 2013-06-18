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

package integration;

import com.nimbits.client.enums.*;
import org.junit.*;
import static org.junit.Assert.assertNull;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 9:21 AM
 */
public class TestEnum {

    @Test
    public void testEnumFail() {
        DateFormatType type = DateFormatType.get("dddd");
        assertNull(type);
    }

}
