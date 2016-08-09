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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.List;


public class WebHookPanel extends BasePanel {
    public static final String GET_RESULTS_POINT = "GET Results Point";
    private final Entity entity;

    public WebHookPanel(User user, PanelEvent listener, final Entity entity) {
        super(user, listener, "<a href=\"http://www.nimbits.com/howto_webhooks.jsp\">Learn More: WebHook Help</a>");
        this.entity = entity;

        createForm();
    }


    private void createForm() {


        final CheckBox enabled = new CheckBox();
        final TextField<String> nameField = new TextField<String>();
        final TextField<String> urlField = new TextField<String>();


        nameField.setFieldLabel("Web Hook Name");
        urlField.setFieldLabel("External Url");
        urlField.setWidth(400);


        HttpMethod method;
        DataChannel pathChannel;
        DataChannel bodyChannel;


        final EntityCombo entityCombo;
        final ComboBox<DataChannelOption> pathChannelCombo;
        final ComboBox<DataChannelOption> bodyChannelCombo;

        if (entity.getEntityType().equals(EntityType.webhook)) {
            WebHook hook = ((WebHook) entity);
            pathChannel = hook.getPathChannel();
            bodyChannel = hook.getBodyChannel();
            bodyChannelCombo = dataChannelComboBox("Body Source", bodyChannel);
            pathChannelCombo = dataChannelComboBox("Path Source", pathChannel);

            nameField.setValue(entity.getName().getValue());

            enabled.setValue(hook.isEnabled());
            urlField.setValue(hook.getUrl().getUrl());
            method = hook.getMethod();


            entityCombo = new EntityCombo(user, EntityType.point, hook.getDownloadTarget(), GET_RESULTS_POINT);
            entityCombo.setFieldLabel(GET_RESULTS_POINT);
            //  entityCombo.setVisible(hook.equals(HttpMethod.GET));


            //bodyChannelCombo.setVisible(hook.equals(HttpMethod.POST));
        } else {
            bodyChannel = DataChannel.data;
            pathChannel = DataChannel.none;
            bodyChannelCombo = dataChannelComboBox("POST Body Source", bodyChannel);
            pathChannelCombo = dataChannelComboBox("Path Source", pathChannel);
            method = HttpMethod.POST;

            enabled.setValue(true);
            entityCombo = new EntityCombo(user, EntityType.point, "", GET_RESULTS_POINT);
            entityCombo.setFieldLabel(GET_RESULTS_POINT);
            // entityCombo.setVisible(false);

        }

        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");


        ComboBox<HttpMethodOption> methodCombo = webHookComboBox("HTTP Method", method);
        methodCombo.addSelectionChangedListener(new SelectionChangedListener<HttpMethodOption>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<HttpMethodOption> selectionChangedEvent) {
                //   entityCombo.setVisible(selectionChangedEvent.getSelectedItem().getMethod().equals(HttpMethod.GET));

            }
        });


        submit.addSelectionListener(new SubmitButtonEventSelectionListener(nameField, methodCombo, pathChannelCombo, bodyChannelCombo, urlField, enabled, entityCombo));


        methodCombo.setWidth(100);
        entityCombo.setLabelSeparator("");

        simple.add(nameField, formdata);

        simple.add(urlField, formdata);
        simple.add(pathChannelCombo, formdata);
        simple.add(bodyChannelCombo, formdata);
        simple.add(methodCombo, formdata);
        simple.add(entityCombo, formdata);

        simple.add(enabled, formdata);

        super.completeForm();

    }

    private ComboBox<HttpMethodOption> webHookComboBox(final String title, final HttpMethod selectedValue) {
        final ComboBox<HttpMethodOption> combo = new ComboBox<HttpMethodOption>();

        final List<HttpMethodOption> ops = new ArrayList<HttpMethodOption>(HttpMethod.values().length);


        for (HttpMethod method : HttpMethod.values()) {

            ops.add(new HttpMethodOption(method));

        }


        ListStore<HttpMethodOption> store = new ListStore<HttpMethodOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        HttpMethodOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private ComboBox<DataChannelOption> dataChannelComboBox(final String title, final DataChannel selectedValue) {
        final ComboBox<DataChannelOption> combo = new ComboBox<DataChannelOption>();

        final List<DataChannelOption> ops = new ArrayList<DataChannelOption>(DataChannel.values().length);


        for (DataChannel method : DataChannel.values()) {

            ops.add(new DataChannelOption(method));

        }


        ListStore<DataChannelOption> store = new ListStore<DataChannelOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        DataChannelOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private static class HttpMethodOption extends BaseModelData {
        HttpMethod method;

        HttpMethodOption() {

        }

        HttpMethodOption(HttpMethod value) {
            this.method = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.name());
        }

        public HttpMethod getMethod() {
            return method;
        }
    }


    private static class DataChannelOption extends BaseModelData {
        DataChannel method;

        DataChannelOption() {

        }

        DataChannelOption(DataChannel value) {
            this.method = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.name());
        }

        public DataChannel getMethod() {
            return method;
        }
    }


    private class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final TextField<String> nameField;
        private final ComboBox<DataChannelOption> pathChannelComboBox;
        private final ComboBox<DataChannelOption> bodyChannelComboBox;
        private final ComboBox<HttpMethodOption> methodCombo;
        private final TextField<String> urlField;
        private final CheckBox enabled;
        private final EntityCombo entityCombo;

        public SubmitButtonEventSelectionListener(TextField<String> nameField,

                                                  ComboBox<HttpMethodOption> methodCombo,
                                                  ComboBox<DataChannelOption> pathChannelComboBox,
                                                  ComboBox<DataChannelOption> bodyChannelComboBox,
                                                  TextField<String> urlField,
                                                  CheckBox enabled,
                                                  EntityCombo entityCombo) {
            this.nameField = nameField;
            this.methodCombo = methodCombo;
            this.pathChannelComboBox = pathChannelComboBox;
            this.bodyChannelComboBox = bodyChannelComboBox;
            this.urlField = urlField;
            this.enabled = enabled;
            this.entityCombo = entityCombo;

        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            final WebHook update;
            final MessageBox box;
            if (entity.getEntityType().equals(EntityType.webhook)) {

                box = MessageBox.wait("Progress",
                        "Updating Webhook", "please wait...");
                box.show();

                try {
                    update = (WebHook) entity;
                    update.setName(CommonFactory.createName(nameField.getValue(), EntityType.webhook));
                    update.setUrl(UrlContainer.getInstance(urlField.getValue()));
                    update.setMethod(methodCombo.getValue().getMethod());
                    update.setPathChannel(pathChannelComboBox.getValue().getMethod());
                    update.setBodyChannel(bodyChannelComboBox.getValue().getMethod());
                    if (((WebHook) entity).getMethod().equals(HttpMethod.GET) && entityCombo.getValue() != null) {
                        update.setDownloadTarget(entityCombo.getValue().getId());
                    }
                } catch (Exception e) {
                    box.close();
                    FeedbackHelper.showError(e);
                    e.printStackTrace();
                    return;
                }
            } else {
                box = MessageBox.wait("Progress",
                        "Creating Webhook", "please wait...");
                box.show();

                try {
                    final EntityName name = CommonFactory.createName(nameField.getValue(), EntityType.webhook);
                    final UrlContainer url = UrlContainer.getInstance(urlField.getValue());
                    String target = null;
                    if (entityCombo.getValue() != null) {
                        target = entityCombo.getValue().getId();
                    }

                    update = createWebHook(name, url, methodCombo, pathChannelComboBox, bodyChannelComboBox, enabled, target);
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                    return;
                }
            }


            service.addUpdateEntityRpc(user, update, new AddEntityAsyncCallback(box));
        }

    }

    private WebHook createWebHook(EntityName name, UrlContainer url,
                                  ComboBox<HttpMethodOption> methodCombo,
                                  ComboBox<DataChannelOption> pathChannelComboBox,
                                  ComboBox<DataChannelOption> bodyChannelComboBox,
                                  CheckBox enabled, String downloadTarget) {
        return new WebHookModel.Builder()
                .name(name).parent(entity.getId()).method(methodCombo.getValue().getMethod()).pathChannel(pathChannelComboBox.getValue().getMethod())
                .bodyChannel(bodyChannelComboBox.getValue().getMethod()).url(url.getUrl()).downloadTarget(downloadTarget).enabled(enabled.getValue()).create();

    }
}
