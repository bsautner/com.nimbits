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

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.service.subscription.*;
import com.nimbits.client.ui.helper.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class SubscriptionPanel extends NavigationEventProvider {

    FormData formdata;
    VerticalPanel vp;

    private Entity entity;
    private Subscription subscription;
    private Map<SettingType, String> settings;
    public SubscriptionPanel(Entity entity, Map<SettingType, String> settings) {
        this.entity = entity;
        this.settings = settings;

    }
    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        // setLayout(new FillLayout());
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);

        if (entity != null) {
            if (entity.getEntityType().equals(EntityType.subscription)) {
                getExistingSubscription();
            }
            else {
                createForm();
                add(vp);
                doLayout();
            }

        }
        else {
            createNotFoundForm();
            doLayout();
        }


    }
    private void getExistingSubscription() {
        SubscriptionServiceAsync service = GWT.create(SubscriptionService.class);
        service.readSubscription(entity, new AsyncCallback<Subscription>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
            }

            @Override
            public void onSuccess(Subscription result) {
                subscription = result;
                createForm();
                add(vp);
                doLayout();
            }
        });
    }


    private ComboBox<SubscriptionTypeOption> subscriptionTypeOptionComboBox(final String title,final SubscriptionType selectedValue) {
        ComboBox<SubscriptionTypeOption> combo = new ComboBox<SubscriptionTypeOption>();

        ArrayList<SubscriptionTypeOption> ops = new ArrayList<SubscriptionTypeOption>();


        ops.add(new SubscriptionTypeOption(SubscriptionType.none));
        ops.add(new SubscriptionTypeOption(SubscriptionType.newValue));
        ops.add(new SubscriptionTypeOption(SubscriptionType.anyAlert));
        ops.add(new SubscriptionTypeOption(SubscriptionType.high));
        ops.add(new SubscriptionTypeOption(SubscriptionType.low));
        ops.add(new SubscriptionTypeOption(SubscriptionType.idle));
        ops.add(new SubscriptionTypeOption(SubscriptionType.changed));
        ListStore<SubscriptionTypeOption> store = new ListStore<SubscriptionTypeOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(com.nimbits.client.constants.Params.PARAM_NAME);
        combo.setValueField(com.nimbits.client.constants.Params.PARAM_VALUE);
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        SubscriptionTypeOption selected = combo.getStore().findModel(com.nimbits.client.constants.Params.PARAM_VALUE, selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private ComboBox<DeliveryMethodOption> deliveryMethodComboBox(final String title, final SubscriptionNotifyMethod selectedValue) {
        ComboBox<DeliveryMethodOption> combo = new ComboBox<DeliveryMethodOption>();

        ArrayList<DeliveryMethodOption> ops = new ArrayList<DeliveryMethodOption>();

        DeliveryMethodOption none = (new DeliveryMethodOption(SubscriptionNotifyMethod.none));
        ops.add(none);
        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.email));
        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.feed));

        if (settings.containsKey(SettingType.twitterClientId) && !Utils.isEmptyString(settings.get(SettingType.twitterClientId))) {
            ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.twitter));
        }
        if (settings.containsKey(SettingType.facebookAPIKey) && !Utils.isEmptyString(settings.get(SettingType.facebookAPIKey))) {
            ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.facebook));
        }




        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.instantMessage));

        ListStore<DeliveryMethodOption> store = new ListStore<DeliveryMethodOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(com.nimbits.client.constants.Params.PARAM_NAME);
        combo.setValueField(com.nimbits.client.constants.Params.PARAM_VALUE);
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        DeliveryMethodOption selected = combo.getStore().findModel(com.nimbits.client.constants.Params.PARAM_VALUE, selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void createForm() {

        FormPanel simple = new FormPanel();
        simple.setWidth(350);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);

       // int alertSelected = (subscription == null) ? SubscriptionNotifyMethod.none.getCode() : subscription.getAlertNotifyMethod().getCode();

        SubscriptionType type =  (subscription == null) ? SubscriptionType.none : subscription.getSubscriptionType() ;
        SubscriptionNotifyMethod method = (subscription==null) ? SubscriptionNotifyMethod.none : subscription.getNotifyMethod();
        final ComboBox<SubscriptionTypeOption> typeCombo = subscriptionTypeOptionComboBox("When this happens", type);
        final ComboBox<DeliveryMethodOption> methodCombo = deliveryMethodComboBox("Relay Data To", method);




        final TextField<String> subscriptionName = new TextField<String>();
        subscriptionName.setFieldLabel("Subscription Name");

        if (subscription != null && entity.getEntityType().equals(EntityType.subscription)) {
            subscriptionName.setValue(entity.getName().getValue());
        }
        else {
            subscriptionName.setValue(entity.getName().getValue() + " Subscription");
        }


        final SpinnerField spinnerField = new SpinnerField();
        spinnerField.setIncrement(5d);
        spinnerField.getPropertyEditor().setType(Double.class);
        spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("00"));
        spinnerField.setFieldLabel("Repeat limit (Minutes)");
        spinnerField.setMinValue(5d);

        spinnerField.setValue(subscription == null ? 30 : subscription.getMaxRepeat());
        spinnerField.setMaxValue(1000d);



        final CheckBox machine = new CheckBox();
        machine.setBoxLabel("Send message in JSON format");
        machine.setValue(subscription != null && subscription.getNotifyFormatJson());
        machine.setLabelSeparator("");
        if (subscription != null) {
            machine.setEnabled(subscription.getNotifyMethod().isJsonCompatible());
        }
        final CheckBox enabled = new CheckBox();
        enabled.setValue(subscription != null && subscription.getEnabled());
        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {


            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                notifyEntityAddedListener(null);
            }
        });

        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                SubscriptionServiceAsync service = GWT.create(SubscriptionService.class);
                final MessageBox box = MessageBox.wait("Progress",
                        "Subscribing to your point", "loading...");
                box.show();

                SubscriptionNotifyMethod subscriptionNotifyMethod = methodCombo.getValue().getMethod();
                SubscriptionType subscriptionType =  typeCombo.getValue().getMethod();


                final Subscription update;

                if (entity.getEntityType().equals(EntityType.subscription) && subscription != null) {

                    update = SubscriptionFactory.createSubscription(
                            subscription.getSubscribedEntity(),
                            subscriptionType,
                            subscriptionNotifyMethod,
                            spinnerField.getValue().doubleValue(),
                            new Date(0),
                            machine.getValue(),
                            enabled.getValue());
                }
                else {
                    update = SubscriptionFactory.createSubscription(
                            entity.getEntity(),
                            subscriptionType,
                            subscriptionNotifyMethod,
                            spinnerField.getValue().doubleValue(),
                            new Date(0),
                            machine.getValue(),
                            enabled.getValue());
                }
                EntityName name = null;
                try {
                    name = CommonFactoryLocator.getInstance().createName(subscriptionName.getValue(), EntityType.subscription);
                } catch (NimbitsException caught) {
                    FeedbackHelper.showError(caught);
                    return;
                }
                service.subscribe(entity, update ,name, new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable e) {
                        GWT.log(e.getMessage(), e);
                        box.close();
                        MessageBox.alert("Error", e.getMessage(), null);
                        notifyEntityAddedListener(null);
                    }

                    @Override
                    public void onSuccess(final Entity result) {
                        box.close();

                        notifyEntityAddedListener(result);
                    }
                });

            }
        });


        Html h = new Html("<p>You can subscribe to this data point to receive alerts when it " +
                "receives new data or goes into an alert state (high, low or idle). Select how " +
                "you would like to be notified when this data point receives new data.</p> " +
                "<br><p><b>Be Careful!</b> Subscribing any new value on a data point that is updated at a " +
                "high frequency can result in hundreds of emails or posts to your social network! Select a high " +
                "update frequency to limit the number of alerts you receive</p>" +
                "<br><p> In order to receive facebook, twitter and IM message, " +
                "you must enable them on the main menu.");


        Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");


        methodCombo.addSelectionChangedListener(new SelectionChangedListener<DeliveryMethodOption>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<DeliveryMethodOption> deliveryMethodOptionSelectionChangedEvent) {
                setMachineEnabled(deliveryMethodOptionSelectionChangedEvent.getSelectedItem().getMethod());
            }

            private void setMachineEnabled(SubscriptionNotifyMethod method) {
                machine.setEnabled( method.isJsonCompatible());
                if (! method.isJsonCompatible()) {
                   machine.setValue(false);
                }
            }
        });


        vp.add(h);
        vp.add(pn);
        simple.add(subscriptionName, formdata);
        simple.add(typeCombo, formdata);
        simple.add(methodCombo, formdata);
        simple.add(spinnerField, formdata);
        simple.add(enabled, formdata);
        simple.add(machine, formdata);

        LayoutContainer c = new LayoutContainer();
        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(5));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        c.setLayout(layout);
        cancel.setWidth(100);
        submit.setWidth(100);
        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        c.add(cancel, layoutData);
        c.add(submit, layoutData);



        vp.add(simple);
        vp.add(c);
    }

    private void createNotFoundForm() {



        Html h = new Html("<p>Sorry, the UUID provided for the point you're trying to subscribe too " +
                " can not be located. It has either been deleted or marked as private by the point owner.");




        vp.add(h);

    }


    private class DeliveryMethodOption extends BaseModelData {
        SubscriptionNotifyMethod method;


        public DeliveryMethodOption(SubscriptionNotifyMethod value) {
            this.method = value;
            set(com.nimbits.client.constants.Params.PARAM_VALUE, value.getCode());
            set(com.nimbits.client.constants.Params.PARAM_NAME, value.getText());
        }

        public SubscriptionNotifyMethod getMethod() {
            return method;
        }
    }

    private class SubscriptionTypeOption extends BaseModelData {
        SubscriptionType type;


        public SubscriptionTypeOption(SubscriptionType value) {
            this.type = value;
            set(com.nimbits.client.constants.Params.PARAM_VALUE, value.getCode());
            set(com.nimbits.client.constants.Params.PARAM_NAME, value.getText());
        }

        public SubscriptionType getMethod() {
            return type;
        }
    }

}
