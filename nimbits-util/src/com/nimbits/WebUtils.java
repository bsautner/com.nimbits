package com.nimbits;


import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.helper.DevelopmentSettingsHelper;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 11:05 AM
 */
public class WebUtils {

    protected static void createSiteMap() throws IOException {
        final String source = DevelopmentSettingsHelper.getSetting(SettingType.source);
        final File dir = new File(source + "/nimbits-web/web/pages");
        final File sitemap = new File(source + "/nimbits-web/web/sitemap.html");
        if (sitemap.exists()) {
            sitemap.delete();
        }
        final Writer out = new OutputStreamWriter(new FileOutputStream(sitemap));
        out.write(Const.HTML_BOOTSTRAP);
        out.write("<ul>");
        writeFiles(dir, out);
        out.write("</ul>");
        out.write("</body></html>");
        out.close();
     }

    private static void writeFiles(final File dir,final Writer out) throws IOException {
        out.write("<li>" + dir.getName() + "<ul>");
        File cd;
        String path;
        int l;
        String fn;
        for (int i = 0; i < dir.list().length; i++) {
             fn = dir.list()[i];
            cd = new File(dir.getPath() + "\\" + fn);
            if (cd.isDirectory()) {
                writeFiles(cd, out);
            }
            else {
                l = dir.getPath().indexOf("pages") -1;
                path  = dir.getPath().substring(l);
                out.write("<li><a href=\"." +path + '/' + fn + "\">" + fn + "</a></li>\n");
            }
        }
        out.write("</ul>");
    }


}
