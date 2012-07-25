/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.security;

import sun.misc.*;

import javax.crypto.*;
import java.io.*;
import java.security.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/11/11
 * Time: 11:39 AM
 */
public class Encryptor {
    public static String encode(String str) {
        BASE64Encoder encoder = new BASE64Encoder();
        str = encoder.encodeBuffer(str.getBytes());
        return str;
    }

    public static String decode(String str) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            str = new String(decoder.decodeBuffer(str));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static void writeEncryptedFile(final String fileName, final String unencryptedString) throws IOException {

        final String e = encode(unencryptedString);
        final Writer out = new OutputStreamWriter(new FileOutputStream(fileName));
        out.write(e);
        out.close();
    }
    public static String readEncryptedFile(final String fileName) throws IOException {

        String retStr = null;
        final File file = new File(fileName);
        if (file.exists()) {
            final StringBuilder sb = new StringBuilder();
            final BufferedReader in = new BufferedReader(new FileReader(fileName));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
            retStr = decode(sb.toString());
        }
        return retStr;
    }
}