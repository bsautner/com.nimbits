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
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.sync.SyncModel;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;


public class SyncPanel extends BasePanel {
    private final Entity entity;


    public SyncPanel(PanelEvent listener, final Entity entity) {
        super(listener, "<a href=\"http://www.nimbits.com/howto_sync.jsp\">Learn More</a>");
        this.entity = entity;
        createForm();
    }


    protected void createForm() {


        // final TextField<String> name = new TextField<String>();
        final TextField<String> targetInstance = new TextField<String>();
        final TextField<String> targetPoint = new TextField<String>();
        final TextField<String> accessKey = new TextField<String>();
        // name.setFieldLabel("Name");
        targetInstance.setFieldLabel("Target URL");
        targetPoint.setFieldLabel("Target Point");
        accessKey.setFieldLabel("Remote Access Token");


        if (entity.getEntityType().equals(EntityType.sync)) {
            Sync sync = (SyncModel) entity;
            targetInstance.setValue(sync.getTargetInstance());
            targetPoint.setValue(sync.getTarget());
            accessKey.setValue(sync.getAccessKey());
        } else {
            //name.setValue(entity.getName().getValue() + " Access Key");
            targetInstance.setValue("cloud.nimbits.com");
            targetPoint.setValue(entity.getName().getValue());
            // accessKey.setValue("access id on target server");
        }

        submit.addSelectionListener(new SubmitEventSelectionListener(targetPoint, targetInstance, accessKey));


        simple.setHeaderVisible(false);
        simple.add(targetInstance, formdata);
        simple.add(targetPoint, formdata);
        simple.add(accessKey, formdata);

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

        private final TextField<String> targetPoint;
        private final TextField<String> targetInstance;
        private final TextField<String> accessKey;

        SubmitEventSelectionListener(final TextField<String> targetPoint,
                                     final TextField<String> targetInstance,
                                     final TextField<String> accessKey) {
            this.targetInstance = targetInstance;
            this.targetPoint = targetPoint;
            this.accessKey = accessKey;

        }

        public void componentSelected(ButtonEvent buttonEvent) {

            EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            String tp = targetPoint.getValue();
            String ti = targetInstance.getValue();
            String key = accessKey.getValue();
            ti = ti.replace("http://", "").replace("https://", "");
            final MessageBox box = MessageBox.wait("Progress",
                    "Connecting point to " + tp, "please wait...");
            box.show();
            try {

                EntityName name = CommonFactory.createName(entity.getName().getValue() + " synced " + tp + "@" + ti, EntityType.sync);

                Sync update;
                SyncModel.Builder builder = new SyncModel.Builder();

                if (entity.getEntityType().equals(EntityType.sync)) {

                    builder.init((Sync) entity);

                } else {
                    builder
                            .owner(entity.getOwner())
                            .parent(entity.getId())
                            .protectionLevel(ProtectionLevel.everyone)
                            .trigger(entity.getId());






                }

                update = builder
                        .name(name)
                        .target(tp)
                        .targetInstance(ti)
                        .accessKey(key)
                        .create();
                if (update != null) {
                    update.setName(name);
                    service.addUpdateEntityRpc(update, new UpdateEntityAsyncCallback(box));
                }

            } catch (Exception ex) {
                FeedbackHelper.showError(ex);
            }


        }
    }


}


