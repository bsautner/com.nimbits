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

import com.nimbits.client.exception.*;
import com.nimbits.server.intelligence.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/18/11
 * Time: 1:44 PM
 */
public class TestWolframAlpha {

    @Test
    public void testWolframAlpha() throws ParserConfigurationException, IOException, SAXException, NimbitsException {
        IntelligenceServiceImpl i = new IntelligenceServiceImpl();
        assertEquals(2.0, i.getFormulaResult("1+1"), 0.0);

        System.out.println(i.getDataResult("GOOG", "Quote"));


        //  assertEquals(25.0, i.getFormulaResult("5*5"), 0.0);
        //  assertEquals(1.0, i.getFormulaResult("5/5"), 0.0);
        //  assertEquals(5.0, i.getFormulaResult("square root of 25"), 0.0);
        //   assertEquals(5.0, i.getFormulaResult("average decimal value {25,16,22,34}"), 0.0);
    }


    @Test
    public void testParseInput() {

        String t1 = "5+[point.data]-[point2.value]+[point3.value]-8/6";
        StringBuilder result = new StringBuilder();
        if (t1.contains(".data") || t1.contains(".value")) {

            String[] s = t1.split("\\[");

            for (String k : s) {
                // System.out.println(k);
                if (k.contains(".data]") || k.contains(".value]")) {
                    String point = k.split("\\.")[0];
                    String action = k.split("\\.")[1];
                    action = action.substring(0, action.indexOf("]"));

                    System.out.println(action);


                }
            }


        }


    }

}
