package com.nimbits.server.cron;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import helper.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 4:34 PM
 */
public class SystemMaintTest extends NimbitsServletTest {




    @Test
    public void doGetTest() throws InterruptedException {
        SystemMaint systemMaint = new SystemMaint();
        try {
        systemMaint.doGet(req, resp);
        Thread.sleep(2000);
        Map<SettingType, String> settings = settingsService.getSettings();

        for (SettingType setting : SettingType.values()) {
            if (setting.isCreate()) {
                assertEquals(setting.getDefaultValue(), settingsService.getSetting(setting));
            }
        }
        } catch (IOException e) {
            fail();
        } catch (NimbitsException e) {
            e.printStackTrace();
            fail();
        }

    }
}
