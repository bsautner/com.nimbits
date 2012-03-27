package com.nimbits;

import com.nimbits.client.enums.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 12:37 PM
 */
public class Settings {
    private static Map<SettingType, String> map;

    public static String getSetting(SettingType setting) {
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

    protected static Map<SettingType, String> loadSettings() throws IOException {
        Map<SettingType, String> retObj = new HashMap<SettingType, String>();
        if (map == null) {


            File file = new File("dev_settings.txt");

            if (file.exists()) {
                System.out.println(file.getAbsolutePath());
                FileInputStream fstream = null;

                fstream = new FileInputStream(file);

                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null)   {
                    String[] s = strLine.split("=");
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
