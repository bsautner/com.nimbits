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

import com.nimbits.server.orm.PointEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/6/12
 * Time: 11:34 AM
 */
public class TestJava {


    @Test
    public void test() throws ClassNotFoundException {

        String s = this.getClass().getName();
        assertEquals(Class.forName(s).getName(), s);
        System.out.println(PointEntity.class.getName());
    }

    @Test
    public void test2() throws ClassNotFoundException {

       short i;
       int x;

        x = Integer.MAX_VALUE;
        i = (short) x;
        System.out.println(x);
        System.out.println(i);


    }
    class B {
        public int x;

    }


    @Test
    public void test3()   {


        B a = new B();
        a.x  = 100;

        B b = new B();
        b.x  = 10;

        change(a, b);
        System.out.println(a.x);
        System.out.println(b.x);

    }

    private void change(B a, B b) {

      a = b;
      a.x =300;


    }






}
