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

package com.nimbits.client.ui.helper;

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
