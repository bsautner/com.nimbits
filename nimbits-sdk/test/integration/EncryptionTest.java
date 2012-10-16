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

import com.nimbits.security.Encryptor;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/15/11
 * Time: 5:23 PM
 */
public class EncryptionTest {

    @Test
    public void testDES() throws NoSuchAlgorithmException {

      //  final com.nimbits.security.Encryptor d = new Encryptor(key);
        String s = "hello world";

        final String e = Encryptor.encode(s);
        final String u = Encryptor.decode(e);
        Assert.assertEquals(s, u);

    }
    @Test
    public void testDES2() throws NoSuchAlgorithmException, IOException {

        String s = "hello world";
        String fileName = "t.txt";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        Encryptor.writeEncryptedFile(fileName, s);
        String r = Encryptor.readEncryptedFile(fileName);
        Assert.assertEquals(s, r);
         File file2 = new File(fileName);
        if (file2.exists()) {
            file2.delete();
        }
    }
}
