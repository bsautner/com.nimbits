package com.nimbits;

import com.nimbits.client.enums.*;
import com.nimbits.helper.DevelopmentSettingsHelper;
import org.junit.*;
import static org.junit.Assert.assertTrue;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 11:08 AM
 */
public class WebUtilsTest {

    @Test
    public void createSiteMapTest() throws IOException {
         WebUtils.createSiteMap();
         File file = new File(DevelopmentSettingsHelper.getSetting(SettingType.source) + "/nimbits-web/web/sitemap.html");

         assertTrue(file.exists());
    }



}
