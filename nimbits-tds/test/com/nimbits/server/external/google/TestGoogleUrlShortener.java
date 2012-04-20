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

package com.nimbits.server.external.google;

import static junit.framework.Assert.*;
import org.junit.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/6/12
 * Time: 2:53 PM
 */
public class TestGoogleUrlShortener {

    @Test
    public void testURLShr() throws IOException {

        String retVal = "";
        retVal = GoogleURLShortener.shortenURL("http://www.nimbits.com");
        assertNotNull(retVal);
        assertTrue(retVal.contains("http"));


    }

}
