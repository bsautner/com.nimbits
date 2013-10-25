/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.ui.controls;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.user.User;

/**
 * Created by benjamin on 8/30/13.
 */
public class ReportHelper {
    private static final String PARAM_DEFAULT_WINDOW_OPTIONS = "menubar=no," +
            "Location=false," +
            "resizable=yes," +
            "scrollbars=yes," +
            "width=980px," +
            "height=800," +
            "status=no," +
            "dependent=true";
    public static void openUrl(User user, final String uuid, final String title, EntityType entityType) {
        String u = com.google.gwt.user.client.Window.Location.getHref()
                + "report.html?type=" + entityType.getCode() + "&" + Parameters.session.getText() + "=" + user.getSessionId() + "&uuid=" + uuid;

        u = u.replace("/#?", "?");
        u = u.replace("https", "http");
        com.google.gwt.user.client.Window.open(u, title, PARAM_DEFAULT_WINDOW_OPTIONS);
    }
}
