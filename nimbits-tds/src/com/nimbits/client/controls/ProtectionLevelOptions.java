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

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.entity.Entity;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/11/12
 * Time: 12:13 PM
 */
public class ProtectionLevelOptions extends RadioGroup {
    // private final RadioGroup radioGroup = new RadioGroup();
    private final Radio radioProtectionMe;
    private final Radio radioProtectionConnections;
    private final Radio radioProtectionPublic;

    public ProtectionLevelOptions(Entity entity) {

        radioProtectionMe = new Radio();
        radioProtectionConnections = new Radio();
        radioProtectionPublic = new Radio();

        radioProtectionMe.setBoxLabel(ProtectionLevel.onlyMe.getText());
        radioProtectionMe.setValue((entity.getProtectionLevel().equals(ProtectionLevel.onlyMe)));


        radioProtectionConnections.setBoxLabel(ProtectionLevel.onlyConnection.getText());
        radioProtectionConnections.setValue((entity.getProtectionLevel().equals(ProtectionLevel.onlyConnection)));


        radioProtectionPublic.setBoxLabel(ProtectionLevel.everyone.getText());
        radioProtectionPublic.setValue((entity.getProtectionLevel().equals(ProtectionLevel.everyone)));


        setFieldLabel("Who can view");

        add(radioProtectionMe);
        add(radioProtectionConnections);
        add(radioProtectionPublic);


    }

    public ProtectionLevel getProtectionLevel() {
        if (radioProtectionMe.getValue()) {
            return (ProtectionLevel.onlyMe);
        } else if (radioProtectionConnections.getValue()) {
            return (ProtectionLevel.onlyConnection);
        } else if (radioProtectionPublic.getValue()) {
            return (ProtectionLevel.everyone);
        }
        else {
            return ProtectionLevel.onlyMe;
        }
    }



}
