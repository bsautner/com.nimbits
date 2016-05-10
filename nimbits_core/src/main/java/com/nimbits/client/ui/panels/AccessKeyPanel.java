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
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.accesskey.AccessKeyModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;



public class AccessKeyPanel extends BasePanel {
    private final Entity entity;


    public AccessKeyPanel(PanelEvent listener, final Entity entity) {
        super(listener, "<a href=\"http://www.nimbits.com/howto_security.jsp\">Learn More: Security Help</a>");
        this.entity = entity;
        createForm();
    }


    protected void createForm() {


        final TextField<String> name = new TextField<String>();
        final TextField<String> key = new TextField<String>();
        name.setFieldLabel("Name");
        key.setFieldLabel("Key");


        if (entity.getEntityType().equals(EntityType.accessKey)) {

            name.setValue(entity.getName().getValue());
            key.setValue("<id encrypted>");
            key.disable();
            submit.disable();

        } else {

            key.setValue("");


        }


        submit.addSelectionListener(new SubmitEventSelectionListener(name, key));


        Html pn = new Html("<p><b>Parent: </b>" + entity.getName().getValue() + "</p>");



        vp.add(pn);

        simple.setHeaderVisible(false);
        simple.add(name, formdata);
        simple.add(key, formdata);


        super.completeForm();


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
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }

        @Override
        public void onSuccess(Entity result) {
            box.close();
            try {
                notifyEntityAddedListener(result);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitEventSelectionListener extends SelectionListener<ButtonEvent> {

        private final TextField<String> name;
        private final TextField<String> k;


        SubmitEventSelectionListener(final TextField<String> name,
                                     final TextField<String> k
                                      ) {
            this.name = name;
            this.k = k;

        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Creating Access Key", "please wait...");

            try {
                if (! entity.getEntityType().equals(EntityType.accessKey)) {
                EntityName newName = CommonFactory.createName(name.getValue(), EntityType.accessKey);
                AccessKeyModel.Builder builder = new AccessKeyModel.Builder();

                    box.show();


                    builder.owner(entity.getOwner())
                            .parent(entity.getId());

                    builder.name(newName)
                            .code(k.getValue());

                    service.addUpdateEntityRpc(builder.create(), new UpdateEntityAsyncCallback(box));



                }

            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }






}


