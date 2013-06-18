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

package com.nimbits.helper;

import com.nimbits.cloudplatform.client.enums.*;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 12:37 PM
 */
public class DevelopmentSettingsHelper {
    private static Map<SettingType, String> map;
    private static final Pattern COMPILE = Pattern.compile("=");

    public static String getSetting(final SettingType setting) {
        if (map.containsKey(setting)) {
            return map.get(setting);
        }
        else {
            return setting.getDefaultValue();
        }
    }

    static {
        try {
            map = loadSettings();
        } catch (IOException e) {
            map = null;
        }
    }

    public static Map<SettingType, String> loadSettings() throws IOException {
        final Map<SettingType, String> retObj = new EnumMap<SettingType, String>(SettingType.class);
        if (map == null) {


            final File file = new File("dev_settings.txt");

            if (file.exists()) {
                System.out.println(file.getAbsolutePath());
                FileInputStream fstream = null;

                fstream = new FileInputStream(file);

                final DataInputStream in = new DataInputStream(fstream);
                final BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                String[] s;
                while ((strLine = br.readLine()) != null)   {
                     s = COMPILE.split(strLine);
                    retObj.put(SettingType.get(s[0]), s[1]);
                }
                //Close the input stream
                in.close();
                return retObj;

            }
            else {
                return null;
            }
        }
        else {
            return map;
        }

    }

}
