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
