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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.service.facebook.*;

import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 4/13/11
 * Time: 12:38 PM
 */
public class FacebookPanel extends LayoutContainer {

    private final String facebookOauthCode;
    private final Map<SettingType, String> settings;


    public FacebookPanel(String code, Map<SettingType, String> someSettings) {
        facebookOauthCode = code;
        this.settings = someSettings;

    }

    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        ContentPanel c = new ContentPanel(new FillLayout());
        c.setHeaderVisible(false);
        c.setFrame(false);
        c.setBodyBorder(false);
        final Label l = new Label();


        String authURL = "https://graph.facebook.com/oauth/authorize" +
                "?client_id=" + settings.get(SettingType.facebookClientId) +
                "&redirect_uri=" + "http://apps.facebook.com/nimbits/" +
                "&scope=user_status,publish_stream,offline_access,email";


        if (facebookOauthCode == null) {

            l.setText("<P></P>" +
                    "<P>Nimbits does not run inside of facebook... When you use the nimbits server on <a href = \"http://app.nimbits.com\" target=\"_blank\"> http://app.nimbits.com</A>, " +
                    "you can configure data points to post to your facebook news feed whenever they are updated or if they go into" +
                    " an alarm state. </P> <BR><BR>" +
                    "<P>In order to have Nimbits post to your facebook News Feed" +
                    " you must add it to your profile. </P><BR><BR><BR>" +
                    "<center><P><font size =+1> <A href =\"" + authURL + "\" target=\"_top\" >Authorize Nimbits by clicking here.</A></font></p></center>" +
                    "");

        } else {
            FacebookServiceAsync facebookService = GWT.create(FacebookService.class);
            facebookService.facebookLogin(facebookOauthCode, new AsyncCallback<EmailAddress>() {

                @Override
                public void onFailure(Throwable caught) {

                    l.setText(caught.getMessage() + "  " + facebookOauthCode);

                }

                @Override
                public void onSuccess(EmailAddress result) {
                    l.setText("<P>You have successfuly added facebook to your Nimbits account. You can now log into <A href =\"http://www.nimbits.com\" target=\"_blank\" >Nimbits Data Logger</A> and configure your data points to relay new values and alerts to facebook.</p>" +
                            "");

                    //	l.setText(result);

                }

            });


        }

        c.add(l);


        add(c);

    }
}