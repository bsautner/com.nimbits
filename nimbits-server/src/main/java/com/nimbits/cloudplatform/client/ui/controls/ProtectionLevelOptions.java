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

package com.nimbits.cloudplatform.client.ui.controls;

import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.entity.Entity;

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

    public ProtectionLevelOptions(final Entity entity) {

        radioProtectionMe = new Radio();
        radioProtectionConnections = new Radio();
        radioProtectionPublic = new Radio();

        radioProtectionMe.setBoxLabel(ProtectionLevel.onlyMe.getText());
        radioProtectionConnections.setBoxLabel(ProtectionLevel.onlyConnection.getText());
        radioProtectionPublic.setBoxLabel(ProtectionLevel.everyone.getText());

        if (entity != null) {
            radioProtectionConnections.setValue(entity.getProtectionLevel().equals(ProtectionLevel.onlyConnection));
            radioProtectionMe.setValue(entity.getProtectionLevel().equals(ProtectionLevel.onlyMe));
            radioProtectionPublic.setValue(entity.getProtectionLevel().equals(ProtectionLevel.everyone));
        }
        else {
            radioProtectionPublic.setValue(true);
        }
        setFieldLabel("Who can view");
        setName(Parameters.protection.getText());
        add(radioProtectionMe);
        add(radioProtectionConnections);
        add(radioProtectionPublic);


    }

    public ProtectionLevel getProtectionLevel() {
        return radioProtectionMe.getValue()
                ? ProtectionLevel.onlyMe : radioProtectionConnections.getValue()
                ? ProtectionLevel.onlyConnection : radioProtectionPublic.getValue()
                ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;
    }



}
