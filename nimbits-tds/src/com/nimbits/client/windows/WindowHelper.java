/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.client.windows;

import com.google.gwt.user.client.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.panels.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/3/12
 * Time: 12:27 PM
 */
public class WindowHelper {

    public static void showSubscriptionPanel(final String uuid, Map<String, String> settings) {
        SubscribePanel dp = new SubscribePanel(uuid, settings);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(500);
        w.setHeight(500);
        w.setHeading("Subscribe");
        w.add(dp);
        dp.addSubscriptionAddedListener(new NavigationEventProvider.SubscriptionAddedListener() {
            @Override
            public void onSubscriptionAdded(Subscription model) {
                w.hide();
                Cookies.removeCookie(Action.subscribe.name());

            }
        });

        w.show();
    }
}
