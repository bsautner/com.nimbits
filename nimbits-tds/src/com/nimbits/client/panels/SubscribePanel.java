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

package com.nimbits.client.panels;

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
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.shared.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class SubscribePanel extends NavigationEventProvider {

    FormData formdata;
    VerticalPanel vp;

    private Entity entity;
    private Subscription subscription;
    private Map<String, String> settings;
    public SubscribePanel(Entity entity, Map<String, String> settings) {
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
       EntityServiceAsync service = GWT.create(EntityService.class);
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


    private ComboBox<Option> alertOptionGroup(final String title,final int selectedValue) {
        ComboBox<Option> combo = new ComboBox<Option>();

        ArrayList<Option> ops = new ArrayList<Option>();

        Option none = (new Option(SubscriptionDeliveryMethod.none));
        ops.add(none);
        ops.add(new Option(SubscriptionDeliveryMethod.stream));

        ops.add(new Option(SubscriptionDeliveryMethod.email));

        if (settings.containsKey(Const.SETTING_TWITTER_CLIENT_ID) && !Utils.isEmptyString(settings.get(Const.SETTING_TWITTER_CLIENT_ID))) {
            ops.add(new Option(SubscriptionDeliveryMethod.twitter));
        }
        if (settings.containsKey(Const.SETTING_FACEBOOK_API_KEY) && !Utils.isEmptyString(settings.get(Const.SETTING_FACEBOOK_API_KEY))) {
            ops.add(new Option(SubscriptionDeliveryMethod.facebook));
        }




        ops.add(new Option(SubscriptionDeliveryMethod.instantMessage));

        ListStore<Option> store = new ListStore<Option>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Const.PARAM_NAME);
        combo.setValueField(Const.PARAM_VALUE);
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        Option selected = combo.getStore().findModel(Const.PARAM_VALUE, selectedValue);
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
        final Radio csvSeparateColumns = new Radio();
        int alertSelected = (subscription == null) ? SubscriptionDeliveryMethod.none.getCode() : subscription.getAlertNotifyMethod().getCode();
        final ComboBox<Option> alertGroup = alertOptionGroup("Alerts", alertSelected);

        final SpinnerField spinnerField = new SpinnerField();
        spinnerField.setIncrement(5d);
        spinnerField.getPropertyEditor().setType(Double.class);
        spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("00"));
        spinnerField.setFieldLabel("Repeat limit (Minutes)");
        spinnerField.setMinValue(5d);

        spinnerField.setValue(subscription == null ? 30 : subscription.getMaxRepeat());
        spinnerField.setMaxValue(1000d);
        int newSelected = (subscription == null) ? SubscriptionDeliveryMethod.none.getCode() : subscription.getDataNotifyMethod().getCode();

        final ComboBox<Option> newValueGroup= alertOptionGroup("New Values",newSelected);

        csvSeparateColumns.setValue(true);
        csvSeparateColumns.setBoxLabel("Export to Spreadsheet (CSV with separate columns)");

        final CheckBox machine = new CheckBox();
        machine.setBoxLabel("Alert message machine readable (JSON format)");
        machine.setValue(subscription != null && subscription.getNotifyFormatJson());
        machine.setLabelSeparator("");
        final CheckBox enabled = new CheckBox();
        enabled.setValue(subscription != null && subscription.getEnabled());
        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {


            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                notifySubscriptionAddedListener(null);
            }
        });

        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                EntityServiceAsync service = GWT.create(EntityService.class);
                final MessageBox box = MessageBox.wait("Progress",
                        "Subscribing to your point", "loading...");
                box.show();

                SubscriptionDeliveryMethod alertStateChangeMethod =alertGroup.getValue().getMethod();
                SubscriptionDeliveryMethod dataUpdateAlertMethod = newValueGroup.getValue().getMethod();
                SubscriptionDeliveryMethod propertyChangeMethod = SubscriptionDeliveryMethod.none;
                Subscription subscription = SubscriptionFactory.createSubscription(
                        dataUpdateAlertMethod,
                        alertStateChangeMethod,
                        propertyChangeMethod,
                        spinnerField.getValue().doubleValue(),
                        new Date(),
                        machine.getValue(),
                        enabled.getValue());


                service.subscribe(entity, subscription , new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable e) {
                        GWT.log(e.getMessage(), e);
                        box.close();
                        MessageBox.alert("Error", e.getMessage(), null);
                        notifySubscriptionAddedListener(null);
                    }

                    @Override
                    public void onSuccess(final Entity result) {
                        box.close();
                        notifySubscriptionAddedListener(result);
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





        vp.add(h);
        vp.add(pn);
        simple.add(alertGroup, formdata);
        simple.add(newValueGroup, formdata);
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


    private class Option extends BaseModelData {
        SubscriptionDeliveryMethod method;


        public Option(SubscriptionDeliveryMethod value) {
            this.method = value;
            set(Const.PARAM_VALUE, value.getCode());
            set(Const.PARAM_NAME, value.name());
        }

        public SubscriptionDeliveryMethod getMethod() {
            return method;
        }
    }

}
