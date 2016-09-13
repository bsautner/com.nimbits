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
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.List;


public class SubscriptionPanel extends BasePanel {


    private final Entity entity;


    public SubscriptionPanel(PanelEvent listener, final Entity entity, User user) {

        super(user, listener, "<a href=\"https://github.com/bsautner/com.nimbits/wiki/Usage:-summaries-and-subscriptions\">Learn More: Subscription Help</a>");

        this.entity = entity;
        createForm();
    }

    private static ComboBox<SubscriptionTypeOption> subscriptionTypeOptionComboBox(final String title, final SubscriptionType selectedValue) {
        ComboBox<SubscriptionTypeOption> combo = new ComboBox<SubscriptionTypeOption>();

        List<SubscriptionTypeOption> ops = new ArrayList<SubscriptionTypeOption>(SubscriptionType.values().length);

        for (SubscriptionType type : SubscriptionType.values()) {
            ops.add(new SubscriptionTypeOption(type));
        }

        ListStore<SubscriptionTypeOption> store = new ListStore<SubscriptionTypeOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        SubscriptionTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private ComboBox<DeliveryMethodOption> deliveryMethodComboBox(final String title, final SubscriptionNotifyMethod selectedValue) {
        final ComboBox<DeliveryMethodOption> combo = new ComboBox<DeliveryMethodOption>();

        final List<DeliveryMethodOption> ops = new ArrayList<DeliveryMethodOption>(SubscriptionNotifyMethod.values().length);


        for (SubscriptionNotifyMethod method : SubscriptionNotifyMethod.values()) {

            ops.add(new DeliveryMethodOption(method));

        }


        ListStore<DeliveryMethodOption> store = new ListStore<DeliveryMethodOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        DeliveryMethodOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void createForm() {


        final TextField<String> subscriptionName = new TextField<String>();
        subscriptionName.setFieldLabel("Subscription Name");

        final TextField<String> target = new TextField<String>();
        target.setFieldLabel("Send To (optional)");

        final EntityCombo webHookCombo;


        final CheckBox enabled = new CheckBox();

        //spinnerField.setMinValue(MIN_VALUE);
        //spinnerField.setMaxValue(MAX_VALUE);
        // int alertSelected = (subscription == null) ? SubscriptionNotifyMethod.none.getCode() : subscription.getAlertNotifyMethod().getCode();
        SubscriptionType type;
        SubscriptionNotifyMethod method;
        if (entity.getEntityType().equals(EntityType.subscription)) {
            Subscription subscription = (Subscription) entity;
            type = subscription.getSubscriptionType();
            method = subscription.getNotifyMethod();
            subscriptionName.setValue(entity.getName().getValue());
            target.setValue(subscription.getTarget());


            enabled.setValue(subscription.getEnabled());


            if (subscription.getNotifyMethod().equals(SubscriptionNotifyMethod.webhook)) {
                webHookCombo = new EntityCombo(user, EntityType.webhook, subscription.getTarget(), "Web Hook Target");


                webHookCombo.setVisible(true);


            } else {
                webHookCombo = new EntityCombo(user, EntityType.webhook, "", "Web Hook Target");
                webHookCombo.setVisible(false);
            }


        } else {
            type = SubscriptionType.none;
            method = SubscriptionNotifyMethod.none;

            subscriptionName.setValue(entity.getName().getValue() + " Subscription");

            webHookCombo = new EntityCombo(user, EntityType.webhook, "", "Web Hook Target");
            webHookCombo.setVisible(false);
            enabled.setValue(true);
        }

        webHookCombo.setFieldLabel("Web Hook Target");
        final ComboBox<SubscriptionTypeOption> typeCombo = subscriptionTypeOptionComboBox("When this happens", type);
        final ComboBox<DeliveryMethodOption> methodCombo = deliveryMethodComboBox("Relay Data To", method);


        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        submit.addSelectionListener(new SubmitButtonEventSelectionListener(webHookCombo, methodCombo, typeCombo, subscriptionName, target, enabled));


        Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");


        methodCombo.setFireChangeEventOnSetValue(true);
        methodCombo.addSelectionChangedListener(new SelectionChangedListener<DeliveryMethodOption>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<DeliveryMethodOption> selectionChangedEvent) {
                target.setVisible(selectionChangedEvent.getSelectedItem().getMethod().equals(SubscriptionNotifyMethod.email));
                webHookCombo.setVisible(selectionChangedEvent.getSelectedItem().getMethod().equals(SubscriptionNotifyMethod.webhook));

            }
        });

        vp.add(pn);
        simple.add(subscriptionName, formdata);


        simple.add(typeCombo, formdata);
        simple.add(methodCombo, formdata);
        simple.add(webHookCombo, formdata);
        simple.add(target, formdata);


        simple.add(enabled, formdata);


        completeForm();


    }

    private static class DeliveryMethodOption extends BaseModelData {
        SubscriptionNotifyMethod method;


        DeliveryMethodOption(SubscriptionNotifyMethod value) {
            this.method = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        private DeliveryMethodOption() {
        }

        public SubscriptionNotifyMethod getMethod() {
            return method;
        }
    }

    private static class SubscriptionTypeOption extends BaseModelData {
        SubscriptionType type;

        SubscriptionTypeOption() {

        }

        SubscriptionTypeOption(SubscriptionType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public SubscriptionType getMethod() {
            return type;
        }
    }

    private class UpdateEntityAsyncCallback implements AsyncCallback<Entity> {

        final MessageBox box;

        UpdateEntityAsyncCallback(final MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(Throwable caught) {
            box.close();
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(Entity result) {
            try {
                box.close();
                notifyEntityAddedListener(result);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final ComboBox<DeliveryMethodOption> methodCombo;
        private final ComboBox<SubscriptionTypeOption> typeCombo;
        private final EntityCombo webhookCombo;
        private final TextField<String> subscriptionName;
        private final TextField<String> target;
        private final CheckBox enabled;

        SubmitButtonEventSelectionListener(EntityCombo webhookCombo, ComboBox<DeliveryMethodOption> methodCombo, ComboBox<SubscriptionTypeOption> typeCombo, TextField<String> subscriptionName, TextField<String> target, CheckBox enabled) {
            this.methodCombo = methodCombo;
            this.typeCombo = typeCombo;
            this.subscriptionName = subscriptionName;
            this.enabled = enabled;
            this.target = target;
            this.webhookCombo = webhookCombo;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {

            final MessageBox box = MessageBox.wait("Progress",
                    "Subscribing to your point", "loading...");
            box.show();

            SubscriptionNotifyMethod subscriptionNotifyMethod = methodCombo.getValue().getMethod();
            SubscriptionType subscriptionType = typeCombo.getValue().getMethod();
            try {
                EntityName name = CommonFactory.createName(subscriptionName.getValue(), EntityType.subscription);


                SubscriptionModel.Builder builder = new SubscriptionModel.Builder();

                if (entity.getEntityType().equals(EntityType.subscription)) {


                    builder.init((Subscription) entity)
                            .name(name);


                    if (subscriptionNotifyMethod.equals(SubscriptionNotifyMethod.webhook)) {
                        builder.target(webhookCombo.getValue().getId());
                    } else {
                        builder.target(target.getValue());
                    }


                } else {


                    String parent = user.getId().equals(entity.getOwner()) ? entity.getId() : "";


                    String targetValue;
                    if (subscriptionNotifyMethod.equals(SubscriptionNotifyMethod.webhook)) {
                        targetValue = (webhookCombo.getValue().getId());
                    } else {
                        targetValue = (target.getValue());


                    }

                    builder.parent(parent)
                            .subscriptionType(subscriptionType)
                            .subscribedEntity(entity.getId())
                            .notifyMethod(subscriptionNotifyMethod)
                            .target(targetValue);

                }

                builder.name(name)
                        .enabled(enabled.getValue())
                        .subscriptionType(subscriptionType)
                        .notifyMethod(subscriptionNotifyMethod);


                final Subscription update = builder.create();
                EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
                service.addUpdateEntityRpc(user, update, new UpdateEntityAsyncCallback(box));


            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }


    }
}
