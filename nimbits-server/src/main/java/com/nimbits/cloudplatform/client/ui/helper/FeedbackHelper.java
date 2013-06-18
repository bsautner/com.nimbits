/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.cloudplatform.client.ui.helper;

import com.extjs.gxt.ui.client.widget.MessageBox;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:32 AM
 */
public class FeedbackHelper {
    private FeedbackHelper() {
    }

    public static void showError(Throwable caught) {
        String message = caught.getMessage();
        if ( message!=null && ! message.trim().equals("0") && ! message.trim().equals("")) {
            final MessageBox box = MessageBox.alert("Error", caught.getMessage(), null);
            box.show();
        }
        Logger logger = Logger.getLogger("NameOfYourLogger");

        logger.log(Level.SEVERE, "Null Exception Hit", caught);

    }

    public static void showInfo(String message) {

        if ( message!=null && ! message.trim().equals("0") && ! message.trim().equals("")) {
            final MessageBox box = MessageBox.info("Info", message, null);
            box.show();
        }

    }
}
