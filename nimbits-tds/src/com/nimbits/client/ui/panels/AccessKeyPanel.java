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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class AccessKeyPanel extends NavigationEventProvider {


    private static final int WIDTH = 350;
    private FormData formdata;
    private VerticalPanel vp;

    private final Entity entity;

    public AccessKeyPanel(final Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);

        try {
            createForm();
            add(vp);
            doLayout();
        } catch (NimbitsException e) {
            FeedbackHelper.showError(e);
        }



    }




    private void createForm() throws NimbitsException {

        FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);

        final TextField<String> name = new TextField<String>();
        final TextField<String> key = new TextField<String>();
        name.setFieldLabel("Key Name");
        key.setFieldLabel("Key");


        RadioGroup radioGroup = new RadioGroup();
        Radio userRadio = new Radio();
        userRadio.setBoxLabel("User");
        Radio pointRadio = new Radio();
        pointRadio.setBoxLabel("Point");

        try {

            AuthLevel level = null;
            if (entity.getEntityType().equals(EntityType.accessKey)) {
                AccessKey accessKey = (AccessKey) entity;
                name.setValue(entity.getName().getValue());
                key.setValue(accessKey.getCode());
                level = ((AccessKey) entity).getAuthLevel();
                radioGroup.setReadOnly(true);
                userRadio.setValue(accessKey.getAuthLevel().compareTo(AuthLevel.readAll) >= 0);
                pointRadio.setValue(accessKey.getAuthLevel().compareTo(AuthLevel.readAll) < 0);
            }
            else {
                name.setValue(entity.getName().getValue() + " Access Key");
                key.setValue("");

                if (entity.getEntityType().equals(EntityType.user)) {
                    userRadio.setValue(true);
                    pointRadio.disable();
                    level = AuthLevel.readWriteAll;
                }
                else if (entity.getEntityType().equals(EntityType.point)) {
                    pointRadio.setValue(true);
                    level = AuthLevel.readPoint;

                }
            }

            final ComboBox<TypeOption> typeCombo = typeOptionComboBox("Permission Level", level);




            Button submit = new Button("Submit");
            Button cancel = new Button("Cancel");
            cancel.addSelectionListener(new CancelButtonEventSelectionListener());

            submit.addSelectionListener(new SubmitEventSelectionListener(name, key, typeCombo, userRadio, pointRadio));


            Html h = new Html("<p>If you create a read/write key for a data point, the only credentials an api call " +
                    "needs to provide is the point's UUID and the key you enter here. This can help save space " +
                    "on low powered micro-controller projects but will weaken your security. Anyone with this key code may " +
                    "read and/or write data to this point depending on these settings. You can also create a key with a global scope, " +
                    "allowing the same level of access to any point</p>");


            Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");
            Html pu = new Html("<p><b>UUID: </b>" + entity.getUUID() + "</p>");










            radioGroup.setFieldLabel("Scope");
            radioGroup.add(userRadio);
            radioGroup.add(pointRadio);



            vp.add(h);
            vp.add(pn);
            vp.add(pu);
            simple.add(name, formdata);
            simple.add(key, formdata);
            simple.add(typeCombo, formdata);
            simple.add(radioGroup, formdata);
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
        } catch (NimbitsException caught) {
            FeedbackHelper.showError(caught);
        }
    }




    private class UpdateEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        UpdateEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);
            box.close();
            try {
                notifyEntityAddedListener(null);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }

        @Override
        public void onSuccess(Entity result) {
            box.close();
            try {
                notifyEntityAddedListener(result);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitEventSelectionListener extends SelectionListener<ButtonEvent> {

        private final TextField<String> name;
        private final TextField<String> k;
        private final ComboBox<TypeOption> typeCombo;
        private final Radio userRadio;

        private final Radio pointRadio ;
        SubmitEventSelectionListener(final TextField<String> name,
                                     final TextField<String> k,
                                     final ComboBox<TypeOption> typeCombo,
                                     final Radio userRadio,
                                     final Radio pointRadio) throws NimbitsException {
            this.name =  name;
            this.k = k;
            this.typeCombo = typeCombo;
            this.userRadio = userRadio;
            this.pointRadio = pointRadio;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            EntityServiceAsync service = GWT.create(EntityService.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Creating Access Key", "please wit...");
            box.show();
            try {
                EntityName newName = CommonFactoryLocator.getInstance().createName(name.getValue(), EntityType.accessKey);

                if (entity.getEntityType().equals(EntityType.accessKey)) {


                    AccessKey accessKey = (AccessKey)entity;
                    accessKey.setName(newName);
                    accessKey.setCode(k.getValue());
                    accessKey.setAuthLevel(typeCombo.getValue().getMethod());

                    service.addUpdateEntity(accessKey, new UpdateEntityAsyncCallback(box));


                }
                else {

                    Entity en = EntityModelFactory.createEntity(newName, "", EntityType.accessKey, ProtectionLevel.onlyMe, entity.getKey(), entity.getOwner());
                    String scope;
                    if (userRadio.getValue()) {
                        scope = entity.getOwner();
                    }
                    else {
                        scope = entity.getKey();
                    }

                    AccessKey update =AccessKeyFactory.createAccessKey(en, k.getValue(), scope,typeCombo.getValue().getMethod());



                    if (update != null) {
                        update.setName(newName);
                        update.setCode(k.getValue());
                        service.addUpdateEntity(update, new UpdateEntityAsyncCallback(box));



                    }



                }
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private static ComboBox<TypeOption> typeOptionComboBox(final String title, final AuthLevel selectedValue) {
        ComboBox<TypeOption> combo = new ComboBox<TypeOption>();

        List<TypeOption> ops = new ArrayList<TypeOption>(SummaryType.values().length);

        for (AuthLevel type : AuthLevel.values()) {
            if (type.isUserVisible()) {
                ops.add(new TypeOption(type));
            }
        }

        ListStore<TypeOption> store = new ListStore<TypeOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        TypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }
    private static class TypeOption extends BaseModelData {
        AuthLevel type;


        TypeOption(AuthLevel value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public AuthLevel getMethod() {
            return type;
        }
    }

    private class CancelButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        CancelButtonEventSelectionListener() {
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            try {
                notifyEntityAddedListener(null);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }
}


