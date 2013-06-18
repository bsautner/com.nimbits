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

package com.nimbits.console;

import com.nimbits.cloudplatform.client.enums.*;
import com.nimbits.security.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/16/11
 * Time: 2:16 PM
 */
public class KeyFile {
    public static String genKey(final Map<String, String> argsMap) throws IOException {

        final String filename = argsMap.get(Parameters.out.getText());
        final StringBuilder sb = new StringBuilder();
        if (argsMap.containsKey(Parameters.key.getText())) {
            sb.append("-key=").append(argsMap.get(Parameters.key.getText())).append(" ");
        }
        if (argsMap.containsKey(Parameters.email.getText())) {
            sb.append("-email=").append(argsMap.get(Parameters.email.getText())).append(" ");
        }
        if (argsMap.containsKey(Parameters.password.getText())) {
            sb.append("-password=").append(argsMap.get(Parameters.password.getText())).append(" ");
        }
        if (argsMap.containsKey(Parameters.host.getText())) {
            sb.append("-host=").append(argsMap.get(Parameters.host.getText())).append(" ");
        }

        final File file = new File(filename);
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }

        Encryptor.writeEncryptedFile(filename, sb.toString());

        return (new File(filename).exists()) ? "Key file generated" : "Could not create key file";

    }

    public static String[] processKeyFile(final Map<String, String> argsMap) throws IOException {
        final String fn =  argsMap.get(Parameters.i.getText());
        final File file = new File(fn);
        String[] args = null;
        if (file.exists()) {
            final String params = Encryptor.readEncryptedFile(fn);
            if (params != null) {
                args = params.split(" ");
            }
        }
        return args;
    }
}
