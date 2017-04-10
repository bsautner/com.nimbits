/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.nimbits.client.ui.panels.login.LoginListener;

public abstract class AbstractLoginPanel extends LayoutContainer {

    protected static final int WIDTH = 400;
    protected static final int BUTTON_WIDTH = 90;
    protected final LoginListener loginListener;

    protected final VerticalPanel vp;
    protected final Html tosHtml = new Html("<hr><p>By using this software you are agreeing to our " +
            "<a href = \"https://github.com/bsautner/com.nimbits/wiki/TOS\">Terms of Use</a></p>");

    protected TextField<String> emailField = new TextField<String>();


    public AbstractLoginPanel(LoginListener loginListener ) {
        this.loginListener = loginListener;

        vp = new VerticalPanel();
        vp.setSpacing(15);
    }


    public void setEmail(String email) {
        emailField.setValue(email);
        emailField.setFieldLabel("Email");
    }

    protected CheckBox rmCheckbox() {
        CheckBox rm = new CheckBox();
        rm.setBoxLabel("Remember me on this device for 30 days.");
        rm.setLabelSeparator("");
        rm.setValue(true);
        return rm;
    }
}
