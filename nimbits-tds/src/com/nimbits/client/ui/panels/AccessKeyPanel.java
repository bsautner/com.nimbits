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

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.accesskey.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.ui.helper.*;

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

    public AccessKeyPanel(Entity entity) {
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





        try {


            if (entity.getEntityType().equals(EntityType.accessKey)) {
                AccessKey accessKey = (AccessKey) entity;
                name.setValue(entity.getName().getValue());
                key.setValue(accessKey.getAccessKey());
            }
            else {
                name.setValue(entity.getName().getValue() + " Read/Write Key");
                key.setValue("");
            }





            Button submit = new Button("Submit");
            Button cancel = new Button("Cancel");
            cancel.addSelectionListener(new CancelButtonEventSelectionListener());

            submit.addSelectionListener(new SubmitEventSelectionListener(name, key));


            Html h = new Html("<p>If you create a read/write key for a data point, the only credentials an api call " +
                    "needs to provide, is the point's UUID and the key you enter here. This can help save space " +
                    "on low powered micro-controller projects but will weaken your security. Anyone with this key code may " +
                    "read and write data to this point.</p>");


            Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");
            Html pu = new Html("<p><b>UUID: </b>" + entity.getUUID() + "</p>");





            vp.add(h);
            vp.add(pn);
            vp.add(pu);
            simple.add(name, formdata);
            simple.add(key, formdata);

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


        SubmitEventSelectionListener(final TextField<String> name, final TextField<String> k) throws NimbitsException {
            this.name =  name;
            this.k = k;
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
                    accessKey.setAccessKey(k.getValue());

                    service.addUpdateEntity(accessKey, new UpdateEntityAsyncCallback(box));


                }
                else {

                    Entity en = EntityModelFactory.createEntity(newName, "", EntityType.accessKey, ProtectionLevel.onlyMe, entity.getKey(), entity.getOwner());
                    AccessKey update =AccessKeyFactory.createAccessKey(en, k.getValue(), entity.getKey());



                    if (update != null) {
                        update.setName(newName);
                        update.setAccessKey(k.getValue());
                        service.addUpdateEntity(update, new UpdateEntityAsyncCallback(box));
                    }



                }
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
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


