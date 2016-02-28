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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.service.settings.SettingsServiceRpc;
import com.nimbits.client.service.settings.SettingsServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;


public class SettingPanel extends BasePanel {

    private SettingsServiceRpcAsync settingsService;

    public SettingPanel(PanelEvent listener) {
        super(listener, "<a href=\"http://www.nimbits.com/howto_server_mail.jsp\">Learn More: Settings Help</a>");
        settingsService = GWT.create(SettingsServiceRpc.class);
        createForm();
    }

    protected void createForm() {

        for (final ServerSetting setting : ServerSetting.values()) {
            if (!setting.isReadOnly()) {
                if (!setting.isFlag()) {
                    final TextField<String> name = new TextField<String>();

                    name.setFieldLabel(setting.getName());
                    name.setPassword(setting.isEncrypted());
                    name.setReadOnly(setting.isReadOnly());

                    settingsService.getSetting(setting.getName(), new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            name.setValue(setting.getDefaultValue());
                        }

                        @Override
                        public void onSuccess(String s) {
                            name.setValue(s);
                        }
                    });
                    simple.add(name);
                } else {

                    final CheckBox checkBox = new CheckBox();
                    //checkBox.setFieldLabel(setting.getName());
                    checkBox.setBoxLabel(setting.getName());
                    checkBox.setLabelSeparator("");
                    checkBox.setWidth(WIDTH);


                    settingsService.getSetting(setting.getName(), new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            checkBox.setValue(Boolean.valueOf(setting.getDefaultValue()));
                        }

                        @Override
                        public void onSuccess(String s) {
                            checkBox.setValue(Boolean.valueOf(s));
                        }
                    });
                    simple.add(checkBox);

                }
            }


        }

        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                for (Field<?> o : simple.getFields()) {
                    String l = o.getFieldLabel();
                    ServerSetting type = ServerSetting.get(l);
                    if (type != null) {
                        //TextField<String> t = (TextField<String>) o;

                        String v = (String) o.getValue();
                        if (v != null && v.length() > 0 && !v.equals(type.getDefaultValue())) {
                            settingsService.updateSetting(type.getName(), v, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    FeedbackHelper.showError(throwable);
                                }


                                @Override
                                public void onSuccess(Void aVoid) {
                                    FeedbackHelper.showInfo("Settings updated");
                                }
                            });
                        }
                    } else {
                        FeedbackHelper.showInfo(l);
                    }

                }

            }
        });

        super.completeForm();
    }
}
