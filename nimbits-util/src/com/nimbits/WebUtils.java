package com.nimbits;


import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 11:05 AM
 */
public class WebUtils {

    protected static void createSiteMap() throws IOException {
        String source = Settings.getSetting(SettingType.source);
        File dir = new File(source + "/nimbits-web/web/pages");
        File sitemap = new File(source + "/nimbits-web/web/sitemap.html");
        if (sitemap.exists()) {
            sitemap.delete();
        }
        Writer out = new OutputStreamWriter(new FileOutputStream(sitemap));
        out.write(Const.HTML_BOOTSTRAP);
        out.write("<ul>");
        writeFiles(dir, out);
        out.write("</ul>");
        out.write("</body></html>");
        out.close();
     }

    private static void writeFiles(final File dir,final Writer out) throws IOException {
        out.write("<li>" + dir.getName() + "<ul>");
        for (int i = 0; i < dir.list().length; i++) {
            String fn = dir.list()[i];
            File cd = new File(dir.getPath() + "\\" + fn);
            if (cd.isDirectory()) {
                writeFiles(cd, out);
            }
            else {
                int l = dir.getPath().indexOf("pages") -1;
                String path = dir.getPath().substring(l);
                out.write("<li><a href=\"." +path + "/" + fn + "\">" + fn + "</a></li>\n");
            }
        }
        out.write("</ul>");
    }


}
